package ulb.services;

public class RewardService {

    private final RewardGenerator rewardGenerator;

    public RewardService(AttackRepository attackRepository, ItemRepository itemRepository, Random random) {
        this.rewardGenerator = new RewardGenerator(attackRepository, itemRepository, random);
    }


}
