package bugemon.server.net;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bugemon.common.dto.persistence.CreateBugemonDTO;
import bugemon.common.dto.persistence.TowerDTO;
import bugemon.common.models.item.Inventory;
import bugemon.common.models.player.PlayerState;
import bugemon.common.models.skills.SkillTreeState;
import bugemon.common.models.team.Team;
import bugemon.common.net.AckPacket;
import bugemon.common.net.AttacksResponsePacket;
import bugemon.common.net.ConnectionRequestPacket;
import bugemon.common.net.ConnectionResponsePacket;
import bugemon.common.net.CreateBugemonRequestPacket;
import bugemon.common.net.CreateBugemonResponsePacket;
import bugemon.common.net.DeleteTeamRequestPacket;
import bugemon.common.net.DeleteTowerRequestPacket;
import bugemon.common.net.GetAttacksRequestPacket;
import bugemon.common.net.GetSkillTreeRequestPacket;
import bugemon.common.net.GetStaticDataRequestPacket;
import bugemon.common.net.GetTeamScreenDataRequestPacket;
import bugemon.common.net.GetTowerInitDataRequestPacket;
import bugemon.common.net.ModifyTeamRequestPacket;
import bugemon.common.net.Packet;
import bugemon.common.net.PlayerSnapshotPacket;
import bugemon.common.net.RenameTeamRequestPacket;
import bugemon.common.net.RequestPlayerDataPacket;
import bugemon.common.net.ResetGameRequestPacket;
import bugemon.common.net.SaveGameRequestPacket;
import bugemon.common.net.SaveInventoryRequestPacket;
import bugemon.common.net.SavePlayerBugemonRequestPacket;
import bugemon.common.net.SaveSkillTreeRequestPacket;
import bugemon.common.net.SaveTeamRequestPacket;
import bugemon.common.net.SaveTowerRequestPacket;
import bugemon.common.net.SetActiveTeamRequestPacket;
import bugemon.common.net.SkillTreeResponsePacket;
import bugemon.common.net.StaticDataPacket;
import bugemon.common.net.TeamOpResponsePacket;
import bugemon.common.net.TeamScreenDataPacket;
import bugemon.common.net.TowerInitDataPacket;
import bugemon.server.bootstrap.GameBootstrapper;
import bugemon.server.bootstrap.ServiceRegistry;
import bugemon.server.repositories.exceptions.BugemonNameIsEmptyException;
import bugemon.server.repositories.exceptions.IdentifierNotFoundException;
import bugemon.server.repositories.exceptions.PlayernameAlreadyExistsException;
import bugemon.server.services.LoginService;
import bugemon.server.services.exceptions.BugemonNameAlreadyExistsException;
import bugemon.server.services.exceptions.TeamNameEmptyException;
import bugemon.server.services.exceptions.TeamNotFoundException;

/**
 * Routes an incoming {@link Packet} to the server-side logic that produces its reply.
 *
 * <p>
 * A single dispatcher instance is shared by every {@link ClientHandler}; it is therefore stateless apart from the
 * bootstrapper and login service it wraps. Per-connection state (the authenticated player and their session services)
 * lives in the {@link ClientSession} passed to {@link #dispatch}.
 *
 * <p>
 * New packet types are added as additional {@code case} branches as the remaining database-backed services are moved
 * behind the socket.
 */
public class RequestDispatcher {

    private static final Logger LOG = LoggerFactory.getLogger(RequestDispatcher.class);

    private final GameBootstrapper bootstrapper;
    private final LoginService loginService;

    /**
     * Builds a dispatcher backed by the fully wired services from the bootstrapper.
     *
     * @param bootstrapper
     *            the server bootstrap holding repositories, static data and the shared RNG
     */
    public RequestDispatcher(GameBootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;
        this.loginService = new LoginService(bootstrapper.getRepositories().playerRepository,
                bootstrapper.getDefaultInventory());
    }

    /**
     * Produces the reply packet for the given request.
     *
     * @param request
     *            the decoded request payload
     * @param session
     *            the calling connection's mutable session state
     * @return the reply packet to send back
     * @throws IllegalArgumentException
     *             if the packet type is not supported
     * @throws IllegalStateException
     *             if a request requires authentication that has not occurred
     */
    public Packet dispatch(Packet request, ClientSession session) {
        return switch (request) {
            case ConnectionRequestPacket auth -> this.handleAuthentication(auth, session);
            case RequestPlayerDataPacket ignored -> this.handlePlayerData(session);
            case ResetGameRequestPacket ignored -> this.handleResetGame(session);
            case GetSkillTreeRequestPacket ignored -> this.handleGetSkillTree(session);
            case SaveSkillTreeRequestPacket save -> this.handleSaveSkillTree(save, session);
            case GetTeamScreenDataRequestPacket ignored -> this.handleGetTeamScreenData(session);
            case SaveTeamRequestPacket save -> this.handleSaveTeam(save, session);
            case DeleteTeamRequestPacket delete -> this.handleDeleteTeam(delete, session);
            case RenameTeamRequestPacket rename -> this.handleRenameTeam(rename, session);
            case ModifyTeamRequestPacket modify -> this.handleModifyTeam(modify, session);
            case SetActiveTeamRequestPacket setActive -> this.handleSetActiveTeam(setActive, session);
            case GetAttacksRequestPacket ignored -> this.handleGetAttacks(session);
            case CreateBugemonRequestPacket create -> this.handleCreateBugemon(create, session);
            case GetTowerInitDataRequestPacket ignored -> this.handleGetTowerInitData(session);
            case SaveTowerRequestPacket save -> this.handleSaveTower(save, session);
            case DeleteTowerRequestPacket ignored -> this.handleDeleteTower(session);
            case SaveGameRequestPacket save -> this.handleSaveGame(save, session);
            case GetStaticDataRequestPacket ignored -> this.handleGetStaticData(session);
            case SaveInventoryRequestPacket save -> this.handleSaveInventory(save, session);
            case SavePlayerBugemonRequestPacket save -> this.handleSavePlayerBugemon(save, session);
            default -> throw new IllegalArgumentException(
                    "Unsupported packet type: " + request.getClass().getName());
        };
    }

    private ConnectionResponsePacket handleAuthentication(ConnectionRequestPacket request, ClientSession session) {
        try {
            if (request.mode() == ConnectionRequestPacket.Mode.LOGIN) {
                return this.handleLogin(request, session);
            }
            return this.handleAccountCreation(request, session);
        } catch (IdentifierNotFoundException e) {
            return ConnectionResponsePacket.failure(ConnectionResponsePacket.Status.IDENTIFIER_NOT_FOUND);
        } catch (PlayernameAlreadyExistsException e) {
            return ConnectionResponsePacket.failure(ConnectionResponsePacket.Status.USERNAME_ALREADY_EXISTS);
        }
    }

    private ConnectionResponsePacket handleLogin(ConnectionRequestPacket request, ClientSession session)
            throws IdentifierNotFoundException {
        if (this.loginService.login(request.username(), request.password())) {
            this.bindSession(session, request.username());
            LOG.info("Player '{}' logged in", request.username());
            return ConnectionResponsePacket.success(request.username());
        }
        return ConnectionResponsePacket.failure(ConnectionResponsePacket.Status.INVALID_CREDENTIALS);
    }

    private ConnectionResponsePacket handleAccountCreation(ConnectionRequestPacket request, ClientSession session)
            throws PlayernameAlreadyExistsException {
        this.loginService.createAccount(request.username(), request.password());
        this.bindSession(session, request.username());
        LOG.info("Account created for player '{}'", request.username());
        return ConnectionResponsePacket.success(request.username());
    }

    private void bindSession(ClientSession session, String playerName) {
        session.authenticate(playerName, this.bootstrapper.createServices(playerName));
    }

    private PlayerSnapshotPacket handlePlayerData(ClientSession session) {
        ServiceRegistry services = session.getServices();
        return new PlayerSnapshotPacket(services.team.getActiveTeam().orElse(null),
                services.inventory.getInventory(), services.skill.getSkillTreeState());
    }

    private PlayerSnapshotPacket handleResetGame(ClientSession session) {
        ServiceRegistry services = session.getServices();
        // clear() wipes the player's DB rows and re-seeds the default inventory into the passed PlayerState; the
        // initial field values are irrelevant since clear() resets them first.
        PlayerState fresh = new PlayerState(session.getPlayerName(), null, new Inventory(), new SkillTreeState());
        services.save.clear(fresh);
        return new PlayerSnapshotPacket(fresh.getActiveTeam().orElse(null), fresh.getInventory(),
                fresh.getSkillTreeState());
    }

    private SkillTreeResponsePacket handleGetSkillTree(ClientSession session) {
        return new SkillTreeResponsePacket(session.getServices().skill.getSkillTree());
    }

    private AckPacket handleSaveSkillTree(SaveSkillTreeRequestPacket request, ClientSession session) {
        session.getServices().skill.save(request.skillTreeState());
        return new AckPacket();
    }

    private TeamScreenDataPacket handleGetTeamScreenData(ClientSession session) {
        ServiceRegistry services = session.getServices();
        List<Team> teams = services.team.getTeamNames().stream().map(services.team::getTeam).filter(Optional::isPresent)
                .map(Optional::get).toList();
        return new TeamScreenDataPacket(services.bugemon.getPlayerBugemons(), teams);
    }

    private AckPacket handleSaveTeam(SaveTeamRequestPacket request, ClientSession session) {
        session.getServices().team.save(request.team());
        return new AckPacket();
    }

    private TeamOpResponsePacket handleDeleteTeam(DeleteTeamRequestPacket request, ClientSession session) {
        try {
            session.getServices().team.deleteTeam(request.teamName());
            return new TeamOpResponsePacket(TeamOpResponsePacket.Status.OK);
        } catch (TeamNotFoundException e) {
            return new TeamOpResponsePacket(TeamOpResponsePacket.Status.NOT_FOUND);
        }
    }

    private TeamOpResponsePacket handleRenameTeam(RenameTeamRequestPacket request, ClientSession session) {
        try {
            session.getServices().team.renameTeam(request.team(), request.newName());
            return new TeamOpResponsePacket(TeamOpResponsePacket.Status.OK);
        } catch (TeamNameEmptyException e) {
            return new TeamOpResponsePacket(TeamOpResponsePacket.Status.NAME_EMPTY);
        } catch (TeamNotFoundException e) {
            return new TeamOpResponsePacket(TeamOpResponsePacket.Status.NOT_FOUND);
        }
    }

    private TeamOpResponsePacket handleModifyTeam(ModifyTeamRequestPacket request, ClientSession session) {
        try {
            session.getServices().team.modify(request.team());
            return new TeamOpResponsePacket(TeamOpResponsePacket.Status.OK);
        } catch (TeamNotFoundException e) {
            return new TeamOpResponsePacket(TeamOpResponsePacket.Status.NOT_FOUND);
        }
    }

    private AckPacket handleSetActiveTeam(SetActiveTeamRequestPacket request, ClientSession session) {
        session.getServices().team.setActiveTeam(request.teamName());
        return new AckPacket();
    }

    private AttacksResponsePacket handleGetAttacks(ClientSession session) {
        return new AttacksResponsePacket(session.getServices().bugemon.getAttacks());
    }

    private CreateBugemonResponsePacket handleCreateBugemon(CreateBugemonRequestPacket request, ClientSession session) {
        Path temp = null;
        try {
            temp = Files.createTempFile("bugemon-sprite-", ".png");
            Files.write(temp, request.spriteBytes());
            CreateBugemonDTO dto = session.getServices().bugemon.createBugemon(request.name(), request.type(),
                    temp.toUri().toURL(), request.attackValue(), request.defenseValue(), request.initiativeValue(),
                    request.hpValue(), request.attacks());
            session.getServices().bugemon.saveNewBugemon(dto);
            return new CreateBugemonResponsePacket(CreateBugemonResponsePacket.Status.OK);
        } catch (BugemonNameIsEmptyException e) {
            return new CreateBugemonResponsePacket(CreateBugemonResponsePacket.Status.NAME_EMPTY);
        } catch (BugemonNameAlreadyExistsException e) {
            return new CreateBugemonResponsePacket(CreateBugemonResponsePacket.Status.NAME_EXISTS);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            this.deleteQuietly(temp);
        }
    }

    private void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LOG.warn("Failed to delete temp sprite file {}", path, e);
        }
    }

    private TowerInitDataPacket handleGetTowerInitData(ClientSession session) {
        ServiceRegistry services = session.getServices();
        Optional<TowerDTO> saved = services.tower.findSavedTower();
        TowerDTO dto = saved.orElse(null);
        Team team = saved.map(d -> d.team().teamName()).flatMap(services.team::getTeam).orElse(null);
        return new TowerInitDataPacket(dto, team, services.inventory.getItems(), services.skill.getSkillTree());
    }

    private AckPacket handleSaveTower(SaveTowerRequestPacket request, ClientSession session) {
        session.getServices().tower.saveTower(request.towerDTO());
        return new AckPacket();
    }

    private AckPacket handleDeleteTower(ClientSession session) {
        session.getServices().tower.delete();
        return new AckPacket();
    }

    private AckPacket handleSaveGame(SaveGameRequestPacket request, ClientSession session) {
        ServiceRegistry services = session.getServices();
        services.skill.save(request.skillTreeState());
        if (request.activeTeam() != null) {
            services.bugemon.save(request.activeTeam());
            services.team.save(request.activeTeam());
        }
        services.inventory.save(request.inventory());
        if (request.tower() != null) {
            services.tower.saveTower(request.tower());
        }
        return new AckPacket();
    }

    private StaticDataPacket handleGetStaticData(ClientSession session) {
        ServiceRegistry services = session.getServices();
        return new StaticDataPacket(services.bugemon.getDefaultBugemons(), services.bugemon.getAttacks(),
                services.inventory.getItems(), services.skill.getSkillTree());
    }

    private AckPacket handleSaveInventory(SaveInventoryRequestPacket request, ClientSession session) {
        session.getServices().inventory.save(request.inventory());
        return new AckPacket();
    }

    private AckPacket handleSavePlayerBugemon(SavePlayerBugemonRequestPacket request, ClientSession session) {
        session.getServices().bugemon.savePlayerBugemon(request.playerBugemon());
        return new AckPacket();
    }
}
