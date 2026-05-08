package ulb.models.tower.room;

public final class RewardRoom extends Room {

    @Override
    public void visitIfNotVisited(RoomVisitor roomVisitor) {
        if (this.isVisited()) {
            return;
        }
        roomVisitor.visitRewardRoom(this);
        this.setVisited();
    }

    @Override
    public RoomType getType() {
        return RoomType.REWARD;
    }
}
