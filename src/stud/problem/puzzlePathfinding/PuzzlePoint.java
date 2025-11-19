package stud.problem.puzzlePathfinding;

/**
 * PuzzlePoint类表示一个三元组(x, y, value)，用于存储当前子拼图状态中的位置(x, y)及其对应的值value。
 */
public class PuzzlePoint {

    // 行列坐标和位将牌值
    private int row; // 行坐标
    private int col; // 列坐标
    private int val; // 该位置的值

    /**
     * 构造函数，用于创建一个新的PuzzlePoint对象。
     * @param row 行坐标
     * @param col 列坐标
     * @param val 该位置的值
     */
    public PuzzlePoint(int row, int col, int val) {
        this.row = row;
        this.col = col;
        this.val = val;
    }

    /**
     * 复制构造函数，用于创建一个新的PuzzlePoint对象，其属性与给定的PuzzlePoint对象相同。
     * @param p 要复制的PuzzlePoint对象
     */
    public PuzzlePoint(PuzzlePoint p) {
        this.row = p.row;
        this.col = p.col;
        this.val = p.val;
    }

    /**
     * 获取行坐标。 
     * @return 行坐标
     */
    public int getRow() {
        return row;
    }

    /**
     * 设置行坐标。
     * @param row 要设置的行坐标
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * 获取列坐标。
     * @return 列坐标
     */
    public int getCol() {
        return col;
    }

    /**
     * 设置列坐标。
     * @param col 要设置的列坐标
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * 获取该位置的值。
     * @return 该位置的值
     */
    public int getVal() {
        return val;
    }

    /**
     * 设置该位置的值。
     * @param val 要设置的值
     */
    public void setVal(int val) {
        this.val = val;
    }
}
