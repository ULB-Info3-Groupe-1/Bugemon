package ulb.models.tower.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import ulb.common.RoomType;
import ulb.models.tower.FloorMap;
import ulb.models.tower.room.Room;
import ulb.models.utils.Position;

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

    public FloorMapFactory(int seed) {
        this.seed = seed;
    }

    public FloorMap create(int floor) {
        for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
            Optional<FloorMap> floormap = this.tryCreate(floor, i);

            if (floormap.isPresent()) {
                return floormap.orElseThrow();
            }
        }

        throw new IllegalStateException("Failed to create FloorMap after " + MAX_GENERATION_ATTEMPTS + " attempts");
    }

    private Optional<FloorMap> tryCreate(int floor, int attempt) {
        // seed to generate n-th floor = game_seed + floor + attempt (varies per retry)
        Random random = new Random(this.seed + floor + attempt);

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
            int x = CENTER + dir[0];
            int y = CENTER + dir[1];

            boolean branchStartPosInBounds = inBounds(x, y);
            boolean branchStartPosAvailable = !nodeCoords.containsKey(nodeKey(x, y));

            if (!branchStartPosInBounds || !branchStartPosAvailable) {
                continue;
            }

            growBranch(x, y, dir, 1, startKey, nodeCoords, nodeDepths, childrenOf, parentOf, random);

            numBranchesGenerated++;
        }

        if (numBranchesGenerated < MIN_BRANCHES) {
            return Optional.empty();
        }

        String bossKey = deepestNode(nodeDepths);
        Map<String, RoomType> types = assignTypes(nodeCoords, childrenOf, parentOf, startKey, bossKey, random);
        return Optional.of(buildMap(nodeCoords, childrenOf, types, startKey));
    }

    private static void growBranch(int x, int y, int[] lastDir, int depth, String parentKey,
            Map<String, int[]> nodeCoords, Map<String, Integer> nodeDepths, Map<String, List<String>> childrenOf,
            Map<String, String> parentOf, Random random) {

        for (int d = depth; d <= MAX_DEPTH; d++) {
            String key = nodeKey(x, y);
            nodeCoords.put(key, new int[] { x, y }); // current node here
            nodeDepths.put(key, d); // current node at depth d
            childrenOf.put(key, new ArrayList<>()); // current node has no children atm
            parentOf.put(key, parentKey);
            childrenOf.get(parentKey).add(key); // add current as child of parent
            parentKey = key;

            List<int[]> candidates = validNextDirs(x, y, lastDir, nodeCoords);
            if (candidates.isEmpty()) {
                break;
            }

            int[] currentDir = lastDir;

            // true if currentDir is a candidate
            boolean straightFree = candidates.stream()
                    .anyMatch(dir -> dir[0] == currentDir[0] && dir[1] == currentDir[1]);

            // Prefer continuing in the same direction when possible.
            // This was not asked by the client but is a cool feature.
            int[] chosen = (straightFree && random.nextDouble() < BIAS_SAME_DIRECTION_PROB) ? currentDir
                    : candidates.get(random.nextInt(candidates.size()));

            x = x + chosen[0];
            y = y + chosen[1];
            lastDir = chosen;
        }
    }

    private static FloorMap buildMap(Map<String, int[]> nodeCoords, Map<String, List<String>> childrenOf,
            Map<String, RoomType> types, String startKey) {

        Map<String, Room> byKey = new HashMap<>();
        Map<Room, Position> positions = new HashMap<>();
        for (String key : nodeCoords.keySet()) {
            RoomType type = types.getOrDefault(key, RoomType.EMPTY);
            Room room = new Room(type);
            byKey.put(key, room);
            int[] coords = nodeCoords.get(key);
            positions.put(room, new Position(coords[0], coords[1]));
        }

        Map<Room, List<Room>> neighbors = new HashMap<>();
        for (Room room : byKey.values()) {
            neighbors.put(room, new ArrayList<>());
        }
        for (Map.Entry<String, List<String>> entry : childrenOf.entrySet()) {
            Room parent = byKey.get(entry.getKey());
            if (parent == null) {
                continue;
            }
            for (String childKey : entry.getValue()) {
                Room child = byKey.get(childKey);
                if (child == null) {
                    continue;
                }
                neighbors.get(parent).add(child);
                neighbors.get(child).add(parent);
            }
        }

        List<Room> rooms = new ArrayList<>(byKey.values());
        return new FloorMap(rooms, positions, neighbors, byKey.get(startKey));
    }

    private static List<int[]> validNextDirs(int x, int y, int[] lastDir, Map<String, int[]> nodeCoords) {
        List<int[]> result = new ArrayList<>();
        for (int[] d : DIRECTIONS) {
            if (d[0] == -lastDir[0] && d[1] == -lastDir[1]) {
                continue;
            }
            int nextCol = x + d[0];
            int nextRow = y + d[1];

            boolean inBounds = inBounds(nextCol, nextRow);
            boolean overlapping = nodeCoords.containsKey(nodeKey(nextCol, nextRow));

            if (inBounds && !overlapping) {
                result.add(d);
            }
        }
        return result;
    }

    private static String deepestNode(Map<String, Integer> nodeDepths) {
        return nodeDepths.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("no node given"));
    }

    /** Assign a RoomType to each room */
    private static Map<String, RoomType> assignTypes(Map<String, int[]> nodeCoords,
            Map<String, List<String>> childrenOf, Map<String, String> parentOf, String startKey, String bossKey,
            Random random) {

        Map<String, RoomType> types = new HashMap<>();
        types.put(startKey, RoomType.START);
        types.put(bossKey, RoomType.BOSS);

        // rooms we still have to assign a type to
        List<String> candidates = nodeCoords.keySet().stream().filter(k -> !k.equals(startKey) && !k.equals(bossKey))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

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
     * node corresponding to key and the
     * corresponding to startKey.
     */
    private static boolean hasCombatAncestor(String key, String startKey, Map<String, String> parentOf,
            Map<String, RoomType> types) {
        String current = parentOf.get(key);
        while (current != null && !current.equals(startKey)) {
            if (types.get(current) == RoomType.COMBAT) {
                return true;
            }
            current = parentOf.get(current);
        }
        return false;
    }

    private static String nodeKey(int x, int y) {
        return String.format("(%d, %d)", x, y);
    }

    private static boolean inBounds(int x, int y) {
        return x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE;
    }
}
