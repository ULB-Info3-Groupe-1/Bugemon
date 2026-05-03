package ulb.models.tower.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ulb.models.tower.FloorNode;
import ulb.models.tower.room.CombatRoom;
import ulb.models.tower.room.EmptyRoom;
import ulb.models.tower.room.RewardRoom;
import ulb.models.utils.Position;

public class FloorGenerator {

    static final int GRID_SIZE = 5;
    static final int MAX_DEPTH = 6;

    static final int MIN_BRANCHES = 3;
    static final int MAX_BRANCHES = 4;

    static final int MIN_COMBATS = 4;
    static final int MAX_COMBATS = 6;

    static final int MIN_REWARD = 2;
    static final int MAX_REWARD = 3;

    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private static final int[][] DIRECTIONS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

    private final Random random;
    private final CombatFactory combatFactory;

    private FloorNode root;
    private FloorNode bossNode;
    private int maxDepthReached;

    private int rewardCount;
    private int branchCount;
    private int combatCount;

    private Set<FloorNode> visitedNode;

    public FloorGenerator(CombatFactory combatFactory) {
        this.combatFactory = combatFactory;
        this.random = new Random();
        this.generateNewFloor();
    }

    public void generateNewFloor() {
        for (int attempt = 0; attempt < MAX_GENERATION_ATTEMPTS; attempt++) {
            this.initialize();
            this.generateFloor();
            if (this.placeInterestPoints()) {
                return;
            }
        }
        throw new IllegalStateException(
                "Failed to generate a valid floor after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    public FloorNode getRoot() {
        return this.root;
    }

    public FloorNode getBossNode() {
        return this.bossNode;
    }

    private void initialize() {
        this.rewardCount = this.random.nextInt(MIN_REWARD, MAX_REWARD + 1);
        this.branchCount = this.random.nextInt(MIN_BRANCHES, MAX_BRANCHES + 1);
        this.combatCount = this.random.nextInt(MIN_COMBATS, MAX_COMBATS + 1);
        this.maxDepthReached = 0;
        this.bossNode = null;
        this.visitedNode = new HashSet<>();

        this.root = new FloorNode(new Position(GRID_SIZE / 2, GRID_SIZE / 2), new EmptyRoom(), new ArrayList<>(), null,
                0);
    }

    private void generateFloor() {
        this.visitedNode.add(this.root);

        List<FloorNode> rootNeighbors = this.getAvailableNeighbors(this.root);
        Collections.shuffle(rootNeighbors, this.random);

        for (int i = 0; i < this.branchCount && i < rootNeighbors.size(); i++) {
            FloorNode tmp = rootNeighbors.get(i);
            FloorNode child = new FloorNode(new Position(tmp.getX(), tmp.getY()), null, new ArrayList<>(), this.root,
                    1);
            this.visitedNode.add(child);
            this.root.addChild(child);
        }

        for (FloorNode child : this.root.getChildren()) {
            this.generateBranch(child);
        }
    }

    private void generateBranch(FloorNode node) {
        if (node.getDepth() > this.maxDepthReached) {
            this.maxDepthReached = node.getDepth();
            this.bossNode = node;
        }

        // A bit weird but keep it need to ask to the client what he really wants
        // Say what he really really wants really wants --> Spice Girls - Wannabe
        if (node.getDepth() >= MAX_DEPTH || node.getBranchCount() >= MAX_DEPTH) {
            return;
        }

        List<FloorNode> neighbors = this.getAvailableNeighbors(node);
        Collections.shuffle(neighbors, this.random);

        for (FloorNode next : neighbors) {
            if (!this.visitedNode.contains(next)) {
                this.visitedNode.add(next);
                node.addChild(next);
                this.generateBranch(next);
            }
        }
    }

    private List<FloorNode> getAvailableNeighbors(FloorNode node) {
        List<FloorNode> neighbors = new ArrayList<>();
        int x = node.getX();
        int y = node.getY();

        for (int[] dir : DIRECTIONS) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];

            if (nextX >= 0 && nextX < GRID_SIZE && nextY >= 0 && nextY < GRID_SIZE) {
                FloorNode neighbor = new FloorNode(new Position(nextX, nextY), null, new ArrayList<>(), node,
                        node.getDepth() + 1);
                if (!this.visitedNode.contains(neighbor)) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private boolean placeInterestPoints() {
        this.bossNode.setRoom(new CombatRoom(this.combatFactory, true));

        List<FloorNode> remaining = this.getAllNonRootNodes();

        List<FloorNode> combatNodes = this.placeCombatRooms(remaining);
        int rewardPlaced = this.placeRewardRooms(remaining, combatNodes);
        this.fillEmptyRooms(remaining);

        // check if all interest points are placed correctly
        return combatNodes.size() == this.combatCount && rewardPlaced == this.rewardCount;
    }

    private List<FloorNode> getAllNonRootNodes() {
        // Shuffle the nodes to ensure random placement of interest points
        // .stream() ai generated, as I wanted something more compact
        List<FloorNode> nodes = this.visitedNode.stream().filter(n -> n != this.root).collect(ArrayList::new,
                ArrayList::add, ArrayList::addAll);
        Collections.shuffle(nodes, this.random);
        return nodes;
    }

    private List<FloorNode> placeCombatRooms(List<FloorNode> remaining) {
        List<FloorNode> combatNodes = new ArrayList<>();
        Iterator<FloorNode> it = remaining.iterator();

        while (it.hasNext() && combatNodes.size() < this.combatCount) {
            FloorNode node = it.next();
            if (!node.equals(this.bossNode)) {
                node.setRoom(new CombatRoom(this.combatFactory, false));
                combatNodes.add(node);
                it.remove();
            }
        }
        return combatNodes;
    }

    private int placeRewardRooms(List<FloorNode> remaining, List<FloorNode> combatNodes) {
        Set<FloorNode> combatSet = new HashSet<>(combatNodes); // maybe overkill ?
        Collections.shuffle(remaining, this.random);
        int placed = 0;

        for (FloorNode node : remaining) {
            if (placed >= this.rewardCount) {
                break;
            }

            if (node.getParent().isPresent() && combatSet.contains(node.getParent().orElseThrow())
                    && !node.equals(this.bossNode)) {
                node.setRoom(new RewardRoom());
                placed++;
            }
        }
        return placed;
    }

    private void fillEmptyRooms(List<FloorNode> remaining) {
        for (FloorNode node : remaining) {
            if (node.getRoom() == null && !node.equals(this.bossNode)) {
                node.setRoom(new EmptyRoom());
            }
        }
    }

    // To print the floor with the associated branch (each number represents a
    // branch)
    @Override
    public String toString() {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        List<FloorNode> branches = this.root.getChildren();

        int branchIdx = 0;
        for (FloorNode branch : branches) {
            branchIdx++;
            Deque<FloorNode> queue = new LinkedList<>();
            queue.add(branch);

            while (!queue.isEmpty()) {
                FloorNode node = queue.poll();
                grid[node.getX()][node.getY()] = branchIdx;
                queue.addAll(node.getChildren());
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                stringBuilder.append(grid[i][j]).append(' ');
            }
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

}
