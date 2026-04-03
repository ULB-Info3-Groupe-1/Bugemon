package ulb.models.no_tower.utilis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ulb.models.no_tower.room.EmptyRoom;
import ulb.models.no_tower.room.FloorNode;

public class FloorGenerator {
    private static final int GRID_SIZE = 5;

    private static final int MIN_BRANCHES = 3;
    private static final int MAX_BRANCHES = 4;
    private static final int MAX_DEPTH = 6;

    private static final int MIN_COMBATS = 4;
    private static final int MAX_COMBATS = 6;

    private static final int MIN_BONUS = 2;
    private static final int MAX_BONUS = 3;

    private FloorNode root;

    private Set<String> visitedNode;

    private static final int[][] DIRECTIONS = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
    private Random random;

    public FloorGenerator() {
        this.random = new Random();
        this.generateFloor();
    }

    private List<FloorNode> getAvailableNeighbors(FloorNode node) {
        List<FloorNode> neighbors = new ArrayList<>();
        int x = node.getX();
        int y = node.getY();

        for (int[] dir : DIRECTIONS) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (newX >= 0 && newX < GRID_SIZE && newY >= 0 && newY < GRID_SIZE) {
                String key = newX + "," + newY;
                if (!visitedNode.contains(key)) {
                    neighbors.add(new FloorNode(newX, newY, null, new ArrayList<>(), node, node.getDepth() + 1));
                }
            }
        }
        return neighbors;
    }

    private void generateBranch(FloorNode node) {
        // A bit ambiguous idk if it should be max detph or max node count...
        if (node.getDepth() >= MAX_DEPTH || node.getBranchCount() >= MAX_DEPTH) {
            return;
        }

        List<FloorNode> neighborsNodes = this.getAvailableNeighbors(node);
        Collections.shuffle(neighborsNodes);

        for (FloorNode nextNode : neighborsNodes) {
            if (node.getBranchCount() >= MAX_DEPTH) {
                break;
            }
            if (!visitedNode.contains(nextNode.getX() + "," + nextNode.getY())) {
                visitedNode.add(nextNode.getX() + "," + nextNode.getY());
                node.addChild(nextNode);
                this.generateBranch(nextNode);
            }
        }
    }

    public void generateFloor() {
        this.root = new FloorNode(GRID_SIZE / 2, GRID_SIZE / 2, new EmptyRoom(), new ArrayList<>(), null, 0);
        this.visitedNode = new HashSet<>();
        this.visitedNode.add(this.root.getX() + "," + this.root.getY());

        List<FloorNode> rootNeighbors = this.getAvailableNeighbors(root);
        Collections.shuffle(rootNeighbors);

        int branchCount = random.nextInt(MIN_BRANCHES, MAX_BRANCHES + 1);

        // Adding the base branch
        List<FloorNode> branchStarts = new ArrayList<>();
        for (int i = 0; i < branchCount; i++) {
            FloorNode tmp = rootNeighbors.get(i);
            FloorNode child = new FloorNode(tmp.getX(), tmp.getY(), null, new ArrayList<>(), root, 1);
            this.visitedNode.add(child.getX() + "," + child.getY());
            root.addChild(child);
            branchStarts.add(child);
        }

        // Running DFS + backtrack
        for (FloorNode start : branchStarts) {
            this.generateBranch(start);
        }
    }

    @Override
    public String toString() {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];

        // Getting the root childeren which are the branch
        List<FloorNode> branchNodes = this.root.getChildren();

        int branchCount = 0;
        // For each branch we travel the rooms...
        for (FloorNode branchNode : branchNodes) {
            branchCount++;
            Deque<FloorNode> queue = new LinkedList<>();
            queue.add(branchNode);

            while (!queue.isEmpty()) {
                FloorNode node = queue.poll();

                grid[node.getX()][node.getY()] = branchCount;

                for (FloorNode child : node.getChildren()) {
                    queue.add(child);
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                result.append(grid[i][j]).append(" ");
            }
            result.append(System.lineSeparator());
        }
        return result.toString();
    }

    public static void main(String[] args) {
        // Development test
        FloorGenerator floorGenerator = new FloorGenerator();
        System.out.println("Floor generated successfully!");
        System.out.println("Floor layout:");
        System.out.println(floorGenerator.toString());
    }
}
