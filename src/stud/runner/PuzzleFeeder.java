package stud.runner;

import core.problem.Problem;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.problem.puzzlePathfinding.PathFinding;
import stud.problem.puzzlePathfinding.Position;
import stud.queue.PuzzleFrontier;

import java.util.ArrayList;

public class PuzzleFeeder extends EngineFeeder {


    /**
     * 将输入的字符串行转换为 Problem 列表。
     * 每一行表示一个拼图问题的字符串描述，返回对应的 Problem 实例列表。
     * @param problemLines 输入的字符串行列表
     */
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines){
        // 使用 getPathFinding 将每行解析为 PathFinding 实例
        ArrayList<Problem> problems = new ArrayList<>();
        int lineNo = 0;
        while (lineNo < problemLines.size()){
            // 解析并生成问题实例
            PathFinding problem = getPathFinding(problemLines.get(lineNo));
            // 添加到问题列表
            problems.add(problem);
            lineNo++;
        }

        return problems;
    }

    /**
     * 返回合适的 Frontier 实现（由 PuzzleFrontier 提供）
     */
    public Frontier getFrontier(EvaluationType type){
        return new PuzzleFrontier(Node.evaluator(type));
    }

    /**
     * 根据 HeuristicType 返回对应的 Predictor，实现委托给 Position。
     */
    public Predictor getPredictor(HeuristicType type){
        return Position.getPredictor(type);
    }

    /**
     * 将单行字符串解析为 PathFinding 实例（初始状态 + 目标状态）
     */
    private PathFinding getPathFinding(String problemLine){
        String[] cells = problemLine.split(" ");
        int size = Integer.parseInt(cells[0]);
        int[][] initiall = new int[size][size];
        int[][] goall = new int[size][size];
        for(int i=0;i<size*size;++i){
            initiall[i/size][i%size] = Integer.parseInt(cells[i+1]);
            goall[i/size][i%size] = Integer.parseInt(cells[i+size*size+1]);
        }
        Position initialState = new Position(initiall,size);
        Position goal = new Position(goall,size);

        return new PathFinding(initialState, goal, size);
    }
}
