package stud.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * ����BFS�Ķ���
 */
public class QueueFrontier extends ArrayDeque<Node> implements Queue<Node>, Frontier{
    private int maxSize = 0;

    @Override
    public boolean contains(Node node) {
        return super.contains(node);
    }

    @Override
    public boolean offer(Node node) {
        boolean res = super.offer(node);
        if (this.size() > maxSize) maxSize = this.size();
        return res;
    }

    @Override
    public Node poll() {
        return super.poll();
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