package ulb.models.tower.room;

public final class RewardRoom extends Room {
    public RewardRoom() {
    }

    @Override
    public void visit(RoomVisitor roomVisitor) {
        roomVisitor.visitRewardRoom(this);
    }

    @Override
    public RoomType getType() {
        return RoomType.REWARD;
    }
}
