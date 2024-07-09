import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

class Cell {
    int x, y;
    int weight;

    Cell(int x, int y, int weight) {
        this.x = x;
        this.y = y;
        this.weight = weight;
    }
}

class CellPanel extends JPanel {
    private int cellSize;
    private List<Cell> path;
    private int[][] grid;

    CellPanel(int[][] grid, List<Cell> path, int cellSize) {
        this.grid = grid;
        this.path = path;
        this.cellSize = cellSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                int cellWeight = grid[i][j];
                g.setColor(Color.WHITE);
                if (pathContains(i, j)) {
                    g.setColor(Color.GREEN);
                }
                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
                g.drawString(String.valueOf(cellWeight), j * cellSize + 10, i * cellSize + 20);
            }
        }
    }

    private boolean pathContains(int x, int y) {
        for (Cell cell : path) {
            if (cell.x == x && cell.y == y) {
                return true;
            }
        }
        return false;
    }
}

class GridFrame extends JFrame {
    GridFrame(int[][] grid, List<Cell> path, int cellSize) {
        CellPanel panel = new CellPanel(grid, path, cellSize);
        add(panel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(grid[0].length * cellSize, grid.length * cellSize);
        setLocationRelativeTo(null);
    }
}

public class PathfindingVisualizer {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of rows and columns in the grid: ");
        int rows = scanner.nextInt();
        int cols = scanner.nextInt();

        int[][] grid = new int[rows][cols];

        System.out.println("Enter the weights of the grid cells:");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = scanner.nextInt();
            }
        }

        System.out.print("Enter the starting and goal positions (x, y): ");
        int startX = scanner.nextInt();
        int startY = scanner.nextInt();
        int goalX = scanner.nextInt();
        int goalY = scanner.nextInt();

        System.out.print("Choose the algorithm to use:\n1. A* Algorithm\n2. AO* Algorithm\n3. Hill Climbing Algorithm\n4. Breadth-First Search (BFS)\nEnter your choice (1/2/3/4): ");
        int choice = scanner.nextInt();

        List<Cell> path = new ArrayList<>();

        switch (choice) {
            case 1:
                path = findMaxWeightPathAStar(grid, startX, startY, goalX, goalY);
                break;
            case 2:
                path = findMaxWeightPathAOStar(grid, startX, startY, goalX, goalY);
                break;
            case 3:
                path = findMaxWeightPathHillClimbing(grid, startX, startY, goalX, goalY);
                break;
            case 4:
                path = findMaxWeightPathBFS(grid, startX, startY, goalX, goalY);
                break;
            default:
                System.out.println("Invalid choice. Exiting...");
                System.exit(1);
        }

        if (path.isEmpty()) {
            System.out.println("No path found to the goal.");
        } else {
            List<Cell> finalPath = path;
            SwingUtilities.invokeLater(() -> {
                GridFrame frame = new GridFrame(grid, finalPath, 50); // Adjust cell size as needed
                frame.setVisible(true);
            });
        }
    }

    private static List<Cell> findMaxWeightPathAStar(int[][] grid, int startX, int startY, int goalX, int goalY) {
        int rows = grid.length;
        int cols = grid[0].length;
        PriorityQueue<Cell> pq = new PriorityQueue<>(Comparator.comparingInt(cell -> cell.weight));
        Set<Cell> visited = new HashSet<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Cell startCell = new Cell(startX, startY, grid[startX][startY]);
        pq.offer(startCell);

        while (!pq.isEmpty()) {
            Cell current = pq.poll();
            visited.add(current);

            if (current.x == goalX && current.y == goalY) {
                // Found the goal, backtrack to get the path
                List<Cell> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = parent.get(current);
                }
                Collections.reverse(path);
                return path;
            }

            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};

            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {
                    Cell neighbor = new Cell(newX, newY, current.weight + grid[newX][newY]);

                    if (!visited.contains(neighbor)) {
                        pq.offer(neighbor);
                        parent.put(neighbor, current);
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private static List<Cell> findMaxWeightPathAOStar(int[][] grid, int startX, int startY, int goalX, int goalY) {
        // Implement AO* algorithm here
        return new ArrayList<>(); // Replace with actual implementation
    }

    private static List<Cell> findMaxWeightPathHillClimbing(int[][] grid, int startX, int startY, int goalX, int goalY) {
        int rows = grid.length;
        int cols = grid[0].length;
        List<Cell> path = new ArrayList<>();
        path.add(new Cell(startX, startY, grid[startX][startY]));

        while (startX != goalX || startY != goalY) {
            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};
            int maxWeight = -1;
            int nextX = -1;
            int nextY = -1;

            for (int i = 0; i < 4; i++) {
                int newX = startX + dx[i];
                int newY = startY + dy[i];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {
                    int weight = grid[newX][newY];

                    if (weight > maxWeight) {
                        maxWeight = weight;
                        nextX = newX;
                        nextY = newY;
                    }
                }
            }

            if (nextX == -1 || nextY == -1) {
                break; // No valid move
            }

            path.add(new Cell(nextX, nextY, grid[nextX][nextY]));
            startX = nextX;
            startY = nextY;
        }

        return path;
    }

    private static List<Cell> findMaxWeightPathBFS(int[][] grid, int startX, int startY, int goalX, int goalY) {
        int rows = grid.length;
        int cols = grid[0].length;
        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Cell startCell = new Cell(startX, startY, grid[startX][startY]);
        queue.offer(startCell);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            if (current.x == goalX && current.y == goalY) {
                // Found the goal, backtrack to get the path
                List<Cell> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = parent.get(current);
                }
                Collections.reverse(path);
                return path;
            }

            int[] dx = {-1, 1, 0, 0};
            int[] dy = {0, 0, -1, 1};

            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];

                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols) {
                    Cell neighbor = new Cell(newX, newY, current.weight + grid[newX][newY]);

                    if (!parent.containsKey(neighbor)) {
                        queue.offer(neighbor);
                        parent.put(neighbor, current);
                    }
                }
            }
        }

        return new ArrayList<>();
    }
}