package stud.problem.puzzlePathfinding;

import core.problem.Action;

public class Move extends Action {

    public final Direction direction;

    public Move(Direction direction) {
        this.direction = direction ;
    }
    public Direction getDirection() {
        return this.direction;
    }
    /**
     * @describe 输出所采取的行动
     * */
    public void draw() {

    }

    /**
     * @return 返回采取的action的花费
     * */
    public int stepCost(){
        return 1;
    }
}
