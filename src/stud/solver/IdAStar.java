package stud.solver;

import core.problem.Problem;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * 简洁且可用的 IDA* 实现（迭代加深 A*）。
 * 该实现使用外部提供的 Frontier（通常为栈式 Frontier），
 * 每轮以当前阈值 cutoff 展开状态，生成子节点并根据 f=g+h 与 cutoff 比较决定是否入栈。
 * 同时维护 nodesGenerated 与 nodesExpanded 统计。
 */
public class IdAStar extends AbstractSearcher {
    private final Predictor predictor;
    private int cutoff;
    private int newCutoff;
    private final int maxIteratorDepth = 1 << 20; // 防止无限循环的上限

    public IdAStar(Frontier frontier, Predictor predictor) {
        super(frontier);
        this.predictor = predictor;
    }

    @Override
    public Deque<Node> search(Problem problem) {
        if (!problem.solvable()) return null;

        Node root = problem.root(predictor);
        cutoff = root.evaluation();

        while (cutoff < maxIteratorDepth) {
            frontier.clear();
            frontier.offer(root);
            newCutoff = Integer.MAX_VALUE;

            // reset counters for this iteration
            nodesExpanded = 0;
            nodesGenerated = 0;

            Set<Object> visited = new HashSet<>();

            while (!frontier.isEmpty()) {
                Node node = frontier.poll();
                // avoid re-expanding same state in this iteration
                if (!visited.add(node.getState())) continue;

                // goal test
                if (problem.goal(node.getState())) {
                    return generatePath(node);
                }

                nodesExpanded++;

                for (Node child : problem.childNodes(node, predictor)) {
                    nodesGenerated++;
                    int f = child.evaluation();
                    if (f <= cutoff) {
                        frontier.offer(child);
                    } else {
                        if (f < newCutoff) newCutoff = f;
                    }
                }
            }

            // next iteration threshold
            if (newCutoff == Integer.MAX_VALUE) break; // no more nodes
            cutoff = newCutoff;
        }
        return null;
    }

    @Override
    public long nodesExpanded() {
        return nodesExpanded;
    }

    @Override
    public long nodesGenerated() {
        return nodesGenerated;
    }
}



