package ulb.models.tower.room;

public final class RewardRoom implements Room {
    public RewardRoom() {
    }

    @Override
    public boolean isCompleted() {
        return true;
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
