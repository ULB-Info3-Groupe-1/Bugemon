package ulb.models.tower.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import ulb.models.tower.FloorMap;
import ulb.models.tower.FloorMap.RoomPosition;
import ulb.models.tower.room.Room;
import ulb.models.tower.room.Room.RoomType;

public class FloorMapFactory {
    // TODO: those constants shouldn't be here
    static final int GRID_SIZE = 5;
    static final int MAX_DEPTH = 6;
    static final int CENTER = GRID_SIZE / 2;

    static final int MIN_BRANCHES = 3;
    static final int MAX_BRANCHES = 4;

    static final int MIN_COMBATS = 4;
    static final int MAX_COMBATS = 6;

    static final int MIN_REWARD = 2;
    static final int MAX_REWARD = 3;

    private static final int MAX_GENERATION_ATTEMPTS = 10;

    private static final int[][] DIRECTIONS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

    private final int seed;

    private int maxDepthReached;

    private int rewardCount;
    private int combatCount;

    public FloorMapFactory(int seed) {
        this.seed = seed;
    }

    public FloorMap create(int floor) {
        for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
            Optional<FloorMap> floormap = this.tryCreate(floor);

            if (floormap.isPresent()) {
                return floormap.orElseThrow();
            }
        }

        throw new IllegalStateException(
                "Failed to create FloorMap after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    public Optional<FloorMap> tryCreate(int floor) {
        // seed to generate n-th floor = game_seed + floor
        Random random = new Random(this.seed + floor);

        Map<String, int[]> nodeCoords = new HashMap<>();
        Map<String, String> parentOf = new HashMap<>();
        Map<String, List<String>> childrenOf = new HashMap<>();

        // start node setup
        String startKey = nodeKey(CENTER, CENTER);
        nodeCoords.put(startKey, new int[] {CENTER, CENTER, 0});
        childrenOf.put(startKey, new ArrayList<>());
        parentOf.put(startKey, null);

        // compute branch count (between MIN_BRANCHES and MAX_BRANCHES)
        int branchCount = random.nextInt(MIN_BRANCHES, MAX_BRANCHES);

        // shuffle directions in which to grow the branches
        List<int[]> shuffledDirs = new ArrayList<>(Arrays.asList(DIRECTIONS));
        Collections.shuffle(shuffledDirs, random);

        int numBranchesGenerated = 0; // keep track of number of generated branches to return empty if could not
                                      // generate enough branches.

        // grow the branches
        for (int b = 0; b < branchCount; b++) {
            int[] dir = shuffledDirs.get(b); // allows us to grow every branch in a different direction

            // compute start position of branch to grow
            int col = CENTER + dir[0];
            int row = CENTER + dir[1];

            boolean branchStartPosInBounds = inBounds(col, row);
            boolean branchStartPosAvailable = !nodeCoords.containsKey(nodeKey(col, row));

            if (!branchStartPosInBounds || !branchStartPosAvailable) {
                continue;
            }

            // TODO: grow the branch

            numBranchesGenerated++;
        }

        if (numBranchesGenerated < numBranchesGenerated) {
            return Optional.empty();
        }
    }

    private static List<int[]> validNextDirs(int col, int row, int[] lastDir, Map<String, int[]> nodeCoords) {
        List<int[]> result = new ArrayList<>();
        for (int[] d : DIRECTIONS) {
            if (d[0] == -lastDir[0] && d[1] == -lastDir[1]) {
                continue;
            }
            int nextCol = col + d[0];
            int nextRow = row + d[1];

            boolean inBounds = inBounds(nextCol, nextRow);
            boolean overlapping = nodeCoords.containsKey(nodeKey(nextCol, nextRow));

            if (!inBounds && !overlapping) {
                result.add(d);
            }
        }
        return result;
    }

    private void generateNewFloor() {
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

    private void initialize() {
        this.depthPerNode = new HashMap<>();
        this.rewardCount = this.random.nextInt(MIN_REWARD, MAX_REWARD + 1);
        this.branchCount = this.random.nextInt(MIN_BRANCHES, MAX_BRANCHES + 1);
        this.combatCount = this.random.nextInt(MIN_COMBATS, MAX_COMBATS + 1);
        this.maxDepthReached = 0;
        this.bossNode = null;
        this.visitedNodes = new HashSet<>();

        this.root = new FloorNode(new RoomPosition(GRID_SIZE / 2, GRID_SIZE / 2), new Room(RoomType.EMPTY),
                new ArrayList<>(), null);
        this.setNodeDepth(this.root, 0);
    }

    private void generateFloor() {
        this.visitedNodes.add(this.root);

        List<FloorNode> rootNeighbors = this.getAvailableNeighbors(this.root);
        Collections.shuffle(rootNeighbors, this.random);

        for (int i = 0; i < this.branchCount && i < rootNeighbors.size(); i++) {
            FloorNode tmp = rootNeighbors.get(i);
            FloorNode child = new FloorNode(new RoomPosition(tmp.getX(), tmp.getY()), null, new ArrayList<>(),
                    this.root);
            this.setNodeDepth(child, 1);
            this.visitedNodes.add(child);
            this.root.addChild(child);
        }

        for (FloorNode child : this.root.getChildren()) {
            this.generateBranch(child);
        }
    }

    private void generateBranch(FloorNode node) {
        if (this.getNodeDepth(node) > this.maxDepthReached) {
            this.maxDepthReached = this.getNodeDepth(node);
            this.bossNode = node;
        }

        if (this.getNodeDepth(node) >= MAX_DEPTH || this.getBranchCount(node) >= MAX_DEPTH) {
            return;
        }

        List<FloorNode> neighbors = this.getAvailableNeighbors(node);
        Collections.shuffle(neighbors, this.random);

        for (FloorNode next : neighbors) {
            if (!this.visitedNodes.contains(next)) {
                this.visitedNodes.add(next);
                node.addChild(next);
                this.generateBranch(next);
            }
        }
    }

    private int getBranchCount(FloorNode node) {
        while (this.depthPerNode.get(node) > 1 && node.getParent().isPresent()) {
            node = node.getParent().orElseThrow();
        }
        return this.countSubTree(node);
    }

    private int countSubTree(FloorNode node) {
        int count = 1;
        for (FloorNode child : node.getChildren()) {
            count += this.countSubTree(child);
        }
        return count;
    }

    private List<FloorNode> getAvailableNeighbors(FloorNode node) {
        List<FloorNode> neighbors = new ArrayList<>();
        int x = node.getX();
        int y = node.getY();

        for (int[] dir : DIRECTIONS) {
            int nextX = x + dir[0];
            int nextY = y + dir[1];

            if (nextX >= 0 && nextX < GRID_SIZE && nextY >= 0 && nextY < GRID_SIZE) {
                FloorNode neighbor = new FloorNode(new RoomPosition(nextX, nextY), null, new ArrayList<>(), node);
                this.setNodeDepth(neighbor, this.getNodeDepth(node) + 1);

                if (!this.visitedNodes.contains(neighbor)) {
                    neighbors.add(neighbor);
                }
            }
        }
        return neighbors;
    }

    private boolean placeInterestPoints() {
        this.bossNode.setRoom(new Room(RoomType.BOSS));

        List<FloorNode> remaining = this.getAllNonRootNodes();
        Collections.shuffle(remaining, this.random);

        List<FloorNode> combatNodes = this.placeCombatRooms(remaining);
        int rewardPlaced = this.placeRewardRooms(remaining, combatNodes);
        this.fillEmptyRooms(remaining);

        // check if all interest points are placed correctly
        // TODO: bad practice to return boolean as this func is a command
        return combatNodes.size() == this.combatCount && rewardPlaced == this.rewardCount;
    }

    private List<FloorNode> getAllNonRootNodes() {
        return this.visitedNodes.stream().filter(n -> n != this.root).collect(ArrayList::new, ArrayList::add,
                ArrayList::addAll);
    }

    private List<FloorNode> placeCombatRooms(List<FloorNode> remaining) {
        List<FloorNode> combatNodes = new ArrayList<>();
        Iterator<FloorNode> it = remaining.iterator();

        while (it.hasNext() && combatNodes.size() < this.combatCount) {
            FloorNode node = it.next();
            if (!node.equals(this.bossNode)) {
                node.setRoom(new Room(RoomType.COMBAT));
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
                node.setRoom(new Room(RoomType.REWARD));
                placed++;
            }
        }
        return placed;
    }

    private void fillEmptyRooms(List<FloorNode> remaining) {
        for (FloorNode node : remaining) {
            if (node.getRoom() == null && !node.equals(this.bossNode)) {
                node.setRoom(new Room(RoomType.EMPTY));
            }
        }
    }

    private int getNodeDepth(FloorNode node) {
        return this.depthPerNode.get(node);
    }

    private void setNodeDepth(FloorNode node, int depth) {
        this.depthPerNode.put(node, depth);
    }

    private static String nodeKey(int col, int row) {
        return String.format("(%d, %d)", col, row);
    }

    private static boolean inBounds(int col, int row) {
        return col >= 0 && col < GRID_SIZE && row >= 0 && row < GRID_SIZE;
    }
}
