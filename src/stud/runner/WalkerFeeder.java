package stud.runner;

import core.problem.Problem;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.problem.pathfinding.GridType;
import stud.problem.pathfinding.PathFinding;
import stud.problem.pathfinding.Position;
import stud.queue.ListFrontier;

import java.util.ArrayList;

/**
 * 寻路问题的EngineFeeder，起名为WalkerFeeder
 * 同学们可以参考编写自己的PuzzleFeeder
 */
public class WalkerFeeder extends EngineFeeder {
    @Override
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {
        // 首行为地图规模
        int size = Integer.parseInt(problemLines.get(0));
        // 读取地图信息（接下来的 size 行）
        GridType[][] map = getMap(problemLines, size);

        ArrayList<Problem> problems = new ArrayList<>();
        int lineNo = size + 1;
        while (lineNo < problemLines.size()){
            // 解析问题行并设置地图
            PathFinding problem = getPathFinding(problemLines.get(lineNo), size);
            problem.setGrids(map);
            problems.add(problem);
            lineNo++;
        }
        return problems;
    }

    /**
     * 生成寻路问题的一个实例
     * @param problemLine
     * @param size
     * @return
     */
    private PathFinding getPathFinding(String problemLine, int size) {
        String[] cells = problemLine.split(" ");
        // 初始位置
        int row = Integer.parseInt(cells[0]);
        int col = Integer.parseInt(cells[1]);
        Position initialState = new Position(row, col);
        // 目标位置
        row = Integer.parseInt(cells[2]);
        col = Integer.parseInt(cells[3]);
        Position goal = new Position(row, col);
        return new PathFinding(initialState, goal, size);
    }

    /**
     *
     * @param problemLines
     * @param size
     * @return
     */
    private GridType[][] getMap(ArrayList<String> problemLines, int size) {
        GridType[][] map = new GridType[size][size];
        for (int i = 0; i < size; i++){
            String[] cells = problemLines.get(i + 1).split(" ");
            for (int j = 0; j < size; j++){
                int cellType = Integer.parseInt(cells[j]);
                map[i][j] = GridType.values()[cellType];
            }
        }
        return map;
    }


    @Override
    public Frontier getFrontier(EvaluationType type) {
        return new ListFrontier(Node.evaluator(type));
    }

    /**
     * 获得对状态进行估值的Predictor
     *
     * @param type 估值函数的类型
     * @return  估值函数
     */
    @Override
    public Predictor getPredictor(HeuristicType type) {
        return Position.predictor(type);
    }

}
