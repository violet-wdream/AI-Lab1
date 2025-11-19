package stud.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.ArrayDeque;
import java.util.Stack;

public class StackFrontier extends ArrayDeque<Node> implements Frontier {
    private int maxSize = 0;

    @Override
    public Node poll() {
        Node n = super.pop();
        return n;
    }

    @Override
    public boolean contains(Node node) {
        return super.contains(node);
    }

    @Override
    public boolean offer(Node node) {
        super.push(node);
        if (this.size() > maxSize) maxSize = this.size();
        return true;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public void resetMaxSize() {
        this.maxSize = this.size();
    }
}
