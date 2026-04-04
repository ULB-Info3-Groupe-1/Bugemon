package ulb.models.trainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import ulb.models.utils.Vec2;

/** Handles smooth interpolated movement of a trainer between two map positions. */
public class TrainerWalk {

    private Vec2 initPos;
    private Vec2 endPos;
    private Vec2 pos;
    private float walkProgress; // 0 to 1
    private boolean isMoving = false;
    private final float duration = 0.5f;
    private List<Vec2> plannedPath;
    private int nextWaypointIndex;

    // Constructors

    public TrainerWalk(Vec2 startPos) {
        this.pos = new Vec2(startPos.getX(), startPos.getY());
    }

    /**
     * Initiates movement towards a new position only if all traversed matrix cells are walkable and a valid path
     * exists.
     *
     * A cell is walkable when its value is non-zero. Value {@code 0} means blocked. When a path exists, movement is
     * executed segment by segment between adjacent cells, while each segment still uses linear interpolation.
     *
     * @param newPos
     *            (Vec2) the target position to walk to.
     * @param matrix
     *            (int[][]) matrix describing walkability, where {@code 0} means non-walkable.
     * @return (boolean) true when movement starts, false when blocked or ignored.
     */
    public boolean moveToIfWalkable(Vec2 newPos, int[][] matrix) {
        if (this.isMoving) {
            return false;
        }

        List<Vec2> path = this.findPath(this.pos, newPos, matrix);
        if (path == null || path.size() < 2) {
            return false;
        }

        this.plannedPath = path;
        this.nextWaypointIndex = 1;
        this.startSegment(this.plannedPath.get(this.nextWaypointIndex));
        return true;
    }

    /**
     * Advances the walk animation. Must be called every frame.
     *
     * @param deltaTime
     *            elapsed time since last call, in seconds
     */
    public void update(float deltaTime) {
        if (!this.isMoving) {
            return;
        }

        this.walkProgress += deltaTime / this.duration;

        if (this.walkProgress >= 1f) {
            this.pos = new Vec2(this.endPos.getX(), this.endPos.getY());
            if (this.hasNextWaypoint()) {
                this.nextWaypointIndex++;
                this.startSegment(this.plannedPath.get(this.nextWaypointIndex));
            } else {
                this.walkProgress = 1f;
                this.isMoving = false;
                this.clearPath();
                this.onArrival();
            }
        } else {
            this.pos = Vec2.linearInterpolation(this.initPos, this.endPos, this.walkProgress);
        }
    }

    private void onArrival() {
        // TODO : notifier la salle visitée pour la griser
        // ex : map.markVisited(pos);
    }

    /**
     * Starts a new interpolation segment from the current position to the given target waypoint.
     *
     * @param target
     *            (Vec2) the next waypoint to reach.
     */
    private void startSegment(Vec2 target) {
        this.initPos = new Vec2(this.pos.getX(), this.pos.getY());
        this.endPos = new Vec2(target.getX(), target.getY());
        this.walkProgress = 0f;
        this.isMoving = true;
    }

    /**
     * Indicates whether at least one waypoint remains after the current one.
     *
     * @return (boolean) true when another segment must be started, false otherwise.
     */
    private boolean hasNextWaypoint() {
        return this.plannedPath != null && this.nextWaypointIndex + 1 < this.plannedPath.size();
    }

    /**
     * Clears the currently planned path and resets waypoint traversal state.
     */
    private void clearPath() {
        this.plannedPath = null;
        this.nextWaypointIndex = 0;
    }

    /**
     * Finds a walkable path between two positions using Breadth-First Search on a 4-neighbor grid (right, left, down,
     * up).
     *
     * Cells with value {@code 0} are treated as blocked. Any non-zero value is treated as walkable.
     *
     * @param start
     *            (Vec2) the starting position.
     * @param end
     *            (Vec2) the destination position.
     * @param matrix
     *            (int[][]) walkability matrix.
     * @return (List<Vec2>) ordered path from start to end (both included), or {@code null} if no path exists.
     */
    private List<Vec2> findPath(Vec2 start, Vec2 end, int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return null;
        }

        int startX = Math.round(start.getX());
        int startY = Math.round(start.getY());
        int endX = Math.round(end.getX());
        int endY = Math.round(end.getY());

        if (!this.isCellWalkable(startX, startY, matrix) || !this.isCellWalkable(endX, endY, matrix)) {
            return null;
        }

        if (startX == endX && startY == endY) {
            return List.of(new Vec2(startX, startY));
        }

        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        String startKey = key(startX, startY);
        String endKey = key(endX, endY);

        queue.offer(new int[]{startX, startY});
        visited.add(startKey);

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentX = current[0];
            int currentY = current[1];

            if (currentX == endX && currentY == endY) {
                return this.rebuildPath(parent, startKey, endKey);
            }

            for (int[] direction : directions) {
                int nx = currentX + direction[0];
                int ny = currentY + direction[1];
                String neighborKey = key(nx, ny);

                if (!visited.contains(neighborKey) && this.isCellWalkable(nx, ny, matrix)) {
                    visited.add(neighborKey);
                    parent.put(neighborKey, key(currentX, currentY));
                    queue.offer(new int[]{nx, ny});
                }
            }
        }

        return null;
    }

    /**
     * Reconstructs the ordered path from BFS parent links.
     *
     * @param parent
     *            (Map<String, String>) parent relation for each visited node.
     * @param startKey
     *            (String) key of the start node.
     * @param endKey
     *            (String) key of the destination node.
     * @return (List<Vec2>) ordered path from start to end, or {@code null} when reconstruction fails.
     */
    private List<Vec2> rebuildPath(Map<String, String> parent, String startKey, String endKey) {
        List<Vec2> reversed = new ArrayList<>();
        String current = endKey;

        while (current != null) {
            int[] coords = parseKey(current);
            reversed.add(new Vec2(coords[0], coords[1]));
            if (current.equals(startKey)) {
                break;
            }
            current = parent.get(current);
        }

        if (reversed.isEmpty() || !key(Math.round(reversed.get(reversed.size() - 1).getX()),
                Math.round(reversed.get(reversed.size() - 1).getY())).equals(startKey)) {
            return null;
        }

        Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Encodes grid coordinates as a stable String key.
     *
     * @param x
     *            (int) x coordinate.
     * @param y
     *            (int) y coordinate.
     * @return (String) encoded key in the form "x,y".
     */
    private static String key(int x, int y) {
        return x + "," + y;
    }

    /**
     * Decodes a coordinate key produced by {@link #key(int, int)}.
     *
     * @param value
     *            (String) key in the form "x,y".
     * @return (int[]) array of size 2 containing x then y.
     */
    private static int[] parseKey(String value) {
        String[] parts = value.split(",");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    /**
     * Checks whether a matrix cell can be traversed.
     *
     * Out-of-bounds cells are treated as non-walkable.
     *
     * @param x
     *            (int) x coordinate.
     * @param y
     *            (int) y coordinate.
     * @param matrix
     *            (int[][]) walkability matrix.
     * @return (boolean) true when coordinates are inside bounds and cell value is non-zero.
     */
    private boolean isCellWalkable(int x, int y, int[][] matrix) {
        if (y < 0 || y >= matrix.length || x < 0 || x >= matrix[y].length) {
            return false;
        }
        return matrix[y][x] != 0;
    }

    /**
     * Checks if the trainer is currently moving towards a target position.
     *
     * @return (boolean) true if the trainer is in the process of walking, false otherwise.
     */
    public boolean isMoving() {
        return this.isMoving;
    }

    public Vec2 getPosition() {
        return this.pos;
    }
}
