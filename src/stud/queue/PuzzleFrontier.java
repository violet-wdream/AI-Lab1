package stud.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.*;

public class PuzzleFrontier extends AbstractQueue<Node> implements Frontier {

    // 优先队列，用于维护节点的优先级
    private final PriorityQueue<Node> frontier;
    // 哈希表，用于快速查找节点
    private final Map<Object, Node> nodeMap;
    // 追踪在一次搜索中的最大 frontier 大小
    private int maxSize = 0;

    /**
     * @describe 构造函数，用于确定优先队列采用的比较器
     * @param evaluator 比较器，用于决定优先队列采用何种方式进行排序，在Node类中实现了
     */
    public PuzzleFrontier(Comparator<Node> evaluator) {
        // 初始化优先队列，使用传入的比较器
        frontier = new PriorityQueue<>(evaluator);
        // 初始化哈希表
        nodeMap = new HashMap<>();
    }

    /**
     * 取出Frontier中的第一个元素
     * @return 返回优先队列中优先级最高的节点，若为空返回null
     */
    public Node poll() {
        Node node = frontier.poll();  // 从优先队列中取出节点
        if (node != null) {
            nodeMap.remove(node.getState());  // 从哈希表中移除节点
        }
        return node;
    }

    @Override
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public void resetMaxSize() {
        this.maxSize = frontier.size();
    }

    @Override
    public Node peek() {
        return frontier.peek();
    }

    /**
     * 清空Frontier
     */
    public void clear() {
        frontier.clear();  // 清空优先队列
        nodeMap.clear();   // 清空哈希表
    }

    @Override
    public Iterator<Node> iterator() {
        return frontier.iterator();
    }

    /**
     * Frontier中元素的个数
     * @return 返回当前节点数量
     */
    public int size() {
        return frontier.size();  // 返回优先队列的大小
    }

    /**
     * Frontier是否为空
     * @return 如果为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return frontier.isEmpty();  // 判断优先队列是否为空
    }

    /**
     * Frontier中是否含有结点node
     * @param node 要检查的节点
     * @return 如果存在返回true，否则返回false
     */
    public boolean contains(Node node) {
        // 检查节点是否在哈希表中
        return nodeMap.containsKey(node.getState());
    }

    /**
     * 在Frontier中插入结点node。
     * 如果插入的结点不在frontier中，则直接插入
     * 如果Frontier中已经存在相同状态的其他结点，则舍弃掉二者之中不好的。
     * @param node 要插入的结点
     * @return 如果成功插入返回true，否则返回false
     */
    public boolean offer(Node node) {
        Object state = node.getState();
        Node existingNode = nodeMap.get(state);

        if (existingNode != null) {
            // 如果已有相同状态的节点，比较评估值
            if (existingNode.evaluation() > node.evaluation()) {
                // 替换为更优的节点
                frontier.remove(existingNode);  // 从优先队列中移除旧节点
                frontier.offer(node);           // 插入新节点
                nodeMap.put(state, node);       // 更新哈希表
                return true;
            }
            // 旧节点更优，舍弃新节点
            return false;
        }

        // 节点不存在，直接插入
        frontier.offer(node);  // 将节点插入优先队列
        nodeMap.put(state, node);  // 在哈希表中记录节点
        // 更新最大值
        if (frontier.size() > maxSize) maxSize = frontier.size();
        return true;
    }
}
