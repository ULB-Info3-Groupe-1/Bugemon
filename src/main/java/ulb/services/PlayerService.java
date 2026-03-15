package ulb.services;

import ulb.models.trainer.Trainer;

public class PlayerService {
    private Trainer player;

    public void setPlayer(Trainer player) {
        this.player = player;
    }

    public Trainer getPlayer() {
        return this.player;
    }

    public int getPlayerTeamSize() {
        return this.player.getTeamSize();
    }
}
