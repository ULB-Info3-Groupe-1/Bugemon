package ulb.models.tower.room;

public final class RewardRoom extends Room {

    @Override
    public void visit(RoomVisitor roomVisitor) {
        roomVisitor.visitRewardRoom(this);
        this.setVisited();
    }

    @Override
    public RoomType getType() {
        return RoomType.REWARD;
    }
}
