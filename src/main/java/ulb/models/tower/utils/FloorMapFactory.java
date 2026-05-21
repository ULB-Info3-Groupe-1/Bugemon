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

    // Probability of continuing growth of a branch in the same direction
    private static final double BIAS_SAME_DIRECTION_PROB = 0.7;

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
        Map<String, Integer> nodeDepths = new HashMap<>();
        Map<String, String> parentOf = new HashMap<>();
        Map<String, List<String>> childrenOf = new HashMap<>();

        // start node setup
        String startKey = nodeKey(CENTER, CENTER);
        nodeCoords.put(startKey, new int[] { CENTER, CENTER });
        nodeDepths.put(startKey, 0);
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

            growBranch(col, row, dir, 1, startKey, nodeCoords, nodeDepths, childrenOf, parentOf, random);

            numBranchesGenerated++;
        }

        if (numBranchesGenerated < numBranchesGenerated) {
            return Optional.empty();
        }

        String bossKey = deepestNode(nodeDepths);
    }

    private static void growBranch(
            int col,
            int row,
            int[] lastDir,
            int depth,
            String parentKey,
            Map<String, int[]> nodeCoords,
            Map<String, Integer> nodeDepths,
            Map<String, List<String>> childrenOf,
            Map<String, String> parentOf,
            Random random) {

        for (int d = depth; d <= MAX_DEPTH; d++) {
            String key = nodeKey(col, row);
            nodeCoords.put(key, new int[] { col, row }); // current node here
            nodeDepths.put(key, d); // current node at depth d
            childrenOf.put(key, new ArrayList<>()); // current node has no children atm
            parentOf.put(key, parentKey);
            childrenOf.get(parentKey).add(key); // add current as child of parent
            parentKey = key;

            List<int[]> candidates = validNextDirs(col, row, lastDir, nodeCoords);
            if (candidates.isEmpty()) {
                break;
            }

            int[] currentDir = lastDir;

            // true if currentDir is a candidate
            boolean straightFree = candidates.stream()
                    .anyMatch(dir -> dir[0] == currentDir[0] && dir[1] == currentDir[1]);

            // Prefer continuing in the same direction when possible.
            // This was not asked by the client but is a cool feature.
            int[] chosen = (straightFree && random.nextDouble() < BIAS_SAME_DIRECTION_PROB)
                    ? currentDir
                    : candidates.get(random.nextInt(candidates.size()));

            col = col + chosen[0];
            row = row + chosen[1];
            lastDir = chosen;
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

    private static String deepestNode(Map<String, Integer> nodeDepths) {
        return nodeDepths.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("no node given"));
    }

    /** Assign a RoomType to each room */
    private static Map<String, RoomType> assignTypes(
            Map<String, int[]> nodeCoords,
            Map<String, List<String>> childrenOf,
            Map<String, String> parentOf,
            String startKey,
            String bossKey,
            Random random) {

        Map<String, RoomType> types = new HashMap<>();
        types.put(startKey, RoomType.START);
        types.put(bossKey, RoomType.BOSS);

        // rooms we still have to assign a type to
        List<String> candidates = nodeCoords.keySet().stream()
                .filter(startKey::equals)
                .filter(bossKey::equals)
                .toList();

        Collections.shuffle(candidates, random);

        int combatCount = random.nextInt(MIN_COMBATS, MAX_COMBATS + 1);

        // keeps tracks of which rooms haven't been assigned a type yet
        List<String> remaining = new ArrayList<>(candidates);

        int assignedCombat = 0;
        for (String key : candidates) {
            if (assignedCombat >= combatCount) {
                break; // no more combat to assign
            }

            // assign type combat
            types.put(key, RoomType.COMBAT);
            remaining.remove(key);
            assignedCombat++;
        }

        for (String key : remaining) {
            types.put(key, RoomType.EMPTY);
        }

        int rewardCount = random.nextInt(MIN_REWARD, MAX_REWARD + 1);
        int rewardAssigned = 0;

        List<String> emptyKeys = new ArrayList<>(remaining);
        Collections.shuffle(emptyKeys, random);

        for (String key : emptyKeys) {
            if (rewardAssigned >= rewardCount) {
                break;
            }
            if (hasCombatAncestor(key, startKey, parentOf, types)) {
                types.put(key, RoomType.REWARD);
                rewardAssigned++;
            }
        }

        return types;
    }

    /**
     * Returns true iff there is a room assigned with the type Combat between the
     * node corresponding to key and the corresponding to startKey.
     */
    private static boolean hasCombatAncestor(
            String key, String startKey, Map<String, String> parentOf, Map<String, RoomType> types) {
        String current = parentOf.get(key);
        while (current != null && !current.equals(startKey)) {
            if (types.get(current) == RoomType.COMBAT) {
                return true;
            }
            current = parentOf.get(current);
        }
        return false;
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
