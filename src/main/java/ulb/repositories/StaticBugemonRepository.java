package ulb.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import ulb.models.bugemon.Attack;
import ulb.models.bugemon.Bugemon;
import ulb.repositories.dto.CreateBugemonDTO;

public interface StaticBugemonRepository {
    Optional<Bugemon> findByName(String name);
    List<Bugemon> getAllDefaultBugemons();
    Map<String, Attack> getAllAttacks();
    void saveBugemon(CreateBugemonDTO bugemon);
}
