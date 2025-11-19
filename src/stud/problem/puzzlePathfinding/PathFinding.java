package stud.problem.puzzlePathfinding;

import core.problem.Action;
import core.problem.Problem;
import core.problem.State;
import core.solver.queue.Node;
import stud.problem.puzzlePathfinding.Direction;
import stud.problem.puzzlePathfinding.Move;
import stud.problem.puzzlePathfinding.Position;

import java.util.Deque;

public class PathFinding extends Problem {

    public PathFinding(State initialState, State goal, int size) {
        super(initialState, goal, size);
    }

    /**
     * @describe 用于判断问题是否可解
     * */
    public boolean solvable() {
        Position.init(size, (Position) goal);
        return ((Position)initialState).parity() == ((Position)goal).parity();
    }


    /**
     * @describe 返回从上一个状态到当前状态的代价，实际上在npuzzle问题中，我们只需要上下左右移动即可，所以每次的花费都为1
     * @param state 当前状态
     * @param action 所采取的action
     * */
    public int stepCost(State state, Action action){
        return 1;
    }

    /**
     * @describe 在状态state上采取action是否可用，比如说可能越界
     * */
    public boolean applicable(State state, Action action) {
        int[] offsets = Direction.offset(((Move)action).getDirection());
        int x = ((Position)state).blank[0]+offsets[0];
        int y = ((Position)state).blank[1]+offsets[1];
        return x >= 0 && x < size &&
                y >= 0 && y < size ;
    }

    /**
     * @describe 解路径的可视化
     * @param path 在达到最终状态之后，从最终的节点一直回溯到初始节点，就能够获得整个路径变化过程
     * */
    public void showSolution(Deque<Node> path){
        System.out.println(111111);
    }
}
