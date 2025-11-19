package stud.problem.puzzlePathfinding;

import java.util.*;

import core.problem.Action;
import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;


import static core.solver.algorithm.heuristic.HeuristicType.MANHATTAN;
import static core.solver.algorithm.heuristic.HeuristicType.MISPLACED;

public class Position extends State {
    public final int[][] state; // 当前状态的棋盘
    public final int size; // 棋盘的边长
    public final int[] blank; // 空白格的位置，使用数组表示 (行, 列)

    public static int[][][] zobrist;
    public static int[][] db;

    public int hash_code = 0;
    /**
     * 使用参数来初始化状态和棋盘的大小，并确定空白格的位置
     * @param state 棋盘的初始状态
     * @param size 棋盘的边长
     */
    public Position(int[][] state, int size) {
        this.size = size; // 初始化棋盘大小
        this.state = new int[size][size]; // 创建棋盘数组
        this.blank = new int[2]; // 初始化空白格位置
        initializeState(state); // 调用方法初始化棋盘状态和空白格位置
    }

    /**
     * 初始化状态和空白格位置
     * @param state 输入的棋盘状态
     */
    
     private void initializeState(int[][] state) {
        for (int row = 0; row < size; row++) {
            System.arraycopy(state[row], 0, this.state[row], 0, size); // 复制输入状态到当前状态
            for (int col = 0; col < size; col++) {
                if (this.state[row][col] == 0) { // 找到空白格
                    blank[0] = row; // 记录行位置
                    blank[1] = col; // 记录列位置
                }
            }
        }
    }

    /**
     * 状态的奇偶性
     * @return 返回状态的奇偶性
     */
    public int parity() {
        int[] flatPuzzle = new int[size * size];
        int inversions = 0;
        int index = 0;
        int zeroRow = -1;

        // 将二维矩阵展平为一维数组，并记录0所在的行
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                flatPuzzle[index] = this.state[i][j];
                if (this.state[i][j] == 0) {
                    zeroRow = i;  // 记录0所在的行
                }
                index++;
            }
        }

        // 计算逆序对数
        for (int i = 0; i < flatPuzzle.length; i++) {
            for (int j = i + 1; j < flatPuzzle.length; j++) {
                if (flatPuzzle[i] > flatPuzzle[j] && flatPuzzle[i] != 0 && flatPuzzle[j] != 0) {
                    inversions++;
                }
            }
        }

        // 如果拼图大小为偶数，还需要考虑空格(0)的行位置
        if (size % 2 == 0) {
            // 如果空格在奇数行（从底部数），需要对逆序数做调整
            if ((size - zeroRow) % 2 == 0) {
                // 0所在行从底部数是偶数行，逆序数加1
                inversions++;
            }
        }

        return inversions % 2;
    }

    @Override
    public void draw() {

        // 最小实现：按行打印棋盘，数字间用空格分隔，0 表示空格
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(state[i][j]);
                if (j < size - 1) System.out.print(" ");
            }
            System.out.println();
        }
    }

    @Override
    public int hashCode() {
        if(hash_code==0)
        {
            int hash = 0;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (state[i][j] != 0)  {
                        hash ^= zobrist[i][j][state[i][j]];
                    }
                }
            }
            hash_code=hash;
        }
        return hash_code;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (obj instanceof Position) {
            Position another = (Position) obj;
            //两个Position对象的state，则认为是相同的
            return hashCode()==another.hashCode();
        }
        return false;
    }

    /**
     * 在当前状态下采取的动作并返回后继状态
     * @param action 在当前状态下采取的动作
     * @return 返回采取该动作后的后继状态
     */
    public State next(Action action) {
        Direction direction = ((Move) action).getDirection(); // 获取动作方向
        int[] movement = Direction.offset(direction); // 获取方向对应的移动偏移量

        // 创建新状态
        Position newState = new Position(this.state, this.size); // 复制当前状态以生成新状态
        // 计算新的空白格位置
        int newRow = newState.blank[0] + movement[0]; // 计算新行位置
        int newCol = newState.blank[1] + movement[1]; // 计算新列位置

        // 交换空白格与目标格的值
        swapPositions(newState, newRow, newCol); // 调用方法交换空白格与目标格的值

        return newState; // 返回新状态
    }


    public static void init(int size, Position goal) {
        // make init idempotent: only initialize once for the same size
        if (zobrist != null && zobrist.length == size) {
            return;
        }
        System.out.print("position init---");
        Random r = new Random();
        zobrist = new int[size][size][size * size];
        for(int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size * size; k++) {
                    zobrist[i][j][k] = r.nextInt();
                }
            }
        }
        System.out.println("init done");
    }
    /**
     * 交换空白格与目标格的值
     * @param nextState 目标状态
     * @param newRow 新的空白格行位置
     * @param newCol 新的空白格列位置
     */
    private void swapPositions(Position nextState, int newRow, int newCol) {
        // 将当前空白格的值替换为目标格的值
        nextState.state[nextState.blank[0]][nextState.blank[1]] = nextState.state[newRow][newCol];
        nextState.state[newRow][newCol] = 0; // 将目标格置为0，表示空白格

        // 更新空白格的位置
        nextState.blank[0] = newRow; // 更新空白格的行位置
        nextState.blank[1] = newCol; // 更新空白格的列位置
    }

    /**
     * @return 返回当前状态下可采取的动作列表
     */
    public Iterable<? extends Action> actions() {
        Collection<Move> possibleMoves = new ArrayList<>(); // 创建可移动列表
        for (Direction direction : Direction.FOUR_DIRECTIONS) { // 遍历四个方向
            possibleMoves.add(new Move(direction)); // 添加可移动的动作
        }
        return possibleMoves; // 返回可移动列表
    }

    //枚举映射，存放不同类型的启发函数
    private static final EnumMap<HeuristicType, Predictor> predictors = new EnumMap<>(HeuristicType.class);
    static{
        predictors.put(MISPLACED,
                (state, goal) -> ((Position)state).misPlaced((Position)goal));
        predictors.put(MANHATTAN,
                (state, goal) -> ((Position)state).manhattan((Position)goal));
        predictors.put(HeuristicType.BLANK_DISTANCE,
            (state, goal) -> ((Position)state).blankDistance((Position)goal));
//        DISJOINT_PATTERN
        predictors.put(HeuristicType.DISJOINT_PATTERN,
                (state, goal) -> ((Position)state).getDisjointPattern((Position)goal));
    }

    /**
     * 空位距离启发：计算当前状态的空位(0)到目标状态空位的曼哈顿距离
     * @param goal 目标状态
     * @return 空位到目标位置的曼哈顿距离
     */
    public int blankDistance(Position goal) {
        int dr = Math.abs(this.blank[0] - goal.blank[0]);
        int dc = Math.abs(this.blank[1] - goal.blank[1]);
        return dr + dc;
    }

    /**
     * 计算当前状态到目标状态的分组错位启发值，用于15-puzzle的三组分解。
     * @param goal 目标状态
     * @return 返回分组启发值（用于A*或IDA*的启发式搜索）
     */
    /**
     * 计算当前状态到目标状态的不相交模式启发值。
     * @param goal 目标状态
     * @return 返回不相交模式启发值
     */
    private int getDisjointPattern(Position goal) {
        int patternCost = 0;

        // 假设我们已经预计算了所有模式的成本，并且它们存储在一个模式数据库中
        // 这里使用 HashMap 来模拟这个数据库
        Map<String, Integer> patternDatabase = new HashMap<>();
        // 示例数据填充，实际上应该是预计算的结果
        // 注意：这里的键值只是示例，实际应该根据你的模式定义来生成正确的键
        patternDatabase.put("1234567", 0); // 模式1-7到目标状态的成本
        patternDatabase.put("891011", 0); // 模式8-11到目标状态的成本
        patternDatabase.put("12131415", 0); // 模式12-15到目标状态的成本

        // 定义模式边界
        int[][] patterns = {{1, 2, 3, 4, 5, 6, 7}, {8, 9, 10, 11}, {12, 13, 14, 15}};

        for (int[] pattern : patterns) {
            StringBuilder patternKey = new StringBuilder();
            for (int value : pattern) {
                // 找到当前模式中的每个值在当前状态中的位置
                int[] position = findValuePosition(value);
                if (position != null) {
                    // 构建模式的键，例如 "1234567"
                    patternKey.append(position[0]).append(position[1]);
                }
            }
            // 查找模式成本
            String key = patternKey.toString();
            if (patternDatabase.containsKey(key)) {
                patternCost += patternDatabase.get(key);
            } else {
                // 如果模式不在数据库中，可以考虑使用其他启发式（如曼哈顿距离）作为替代值
                patternCost += manhattan(goal);
            }
        }

        return patternCost;
    }

    /**
     * 查找指定值在当前状态中的位置。
     * @param value 要查找的值
     * @return 返回值的位置，如果未找到则返回 null
     */
    private int[] findValuePosition(int value) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (state[row][col] == value) {
                    return new int[]{row, col};
                }
            }
        }
        return null;
    }

    public static Predictor getPredictor(HeuristicType type) {
        return predictors.get(type);
    }

    /**
     * 计算当前状态到目标状态的曼哈顿距离
     * @param goal 目标状态
     * @return 返回曼哈顿距离
     */
    public int manhattan(Position goal) {
        int totalDistance = 0; // 初始化总距离
        for (int i = 0; i < this.size; i++) { // 遍历棋盘
            for (int j = 0; j < this.size; j++) {
                int value = this.state[i][j]; // 获取当前格的值
                if (value == 0) continue; // 跳过空白格

                // 找到目标位置
                int[] targetPosition = findTargetPosition(goal, value); // 在目标状态中查找当前值的位置
                if (targetPosition != null) { // 如果找到目标位置
                    // 计算当前格与目标位置之间的曼哈顿距离并累加
                    totalDistance += Math.abs(i - targetPosition[0]) + Math.abs(j - targetPosition[1]);
                }
            }
        }
        return totalDistance; // 返回总曼哈顿距离
    }

    /**
     * 找到目标状态中数字的位置
     * @param goal 目标状态
     * @param value 要查找的值
     * @return 返回找到的目标位置，若未找到则返回 null
     */
    private int[] findTargetPosition(Position goal, int value) {
        for (int row = 0; row < goal.size; row++) { // 遍历目标状态
            for (int col = 0; col < goal.size; col++) {
                if (goal.state[row][col] == value) { // 找到目标值
                    return new int[]{row, col}; // 返回目标位置
                }
            }
        }
        return null; // 如果未找到，返回 null
    }

    /**
     * 不在位的瓷砖数量
     * @param goal 目标状态
     * @return 返回不在位的瓷砖数量
     */
    public int misPlaced(Position goal) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.state[i][j] != goal.state[i][j] && this.state[i][j] != 0) {
                    count++;
                }
            }
        }
        return count;
    }
}





