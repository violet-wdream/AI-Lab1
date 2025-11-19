package core.solver.queue;

/**
 * Frontier�ӿ�
 */
public interface Frontier {
    /**
     * ȡ��Frontier�еĵ�һ��Ԫ��
     * @return
     */
    Node poll();

    /**
     * ���Frontier
     */
    void clear();

    /**
     * Frontier��Ԫ�صĸ���
     * @return
     */
    int size();

    /**
     * Frontier�Ƿ�Ϊ��
     *
     */
    boolean isEmpty();

    /**
     * Frontier���Ƿ��н��node
     * @param node
     * @return
     */
    boolean contains(Node node);

    /***
     * ��Frontier�в�����node��
     * �������Ľ�㲻��frontier�У���ֱ�Ӳ���
     * ���Frontier���Ѿ�������ͬ״̬��������㣬������������֮�в��õġ�
     * @param node Ҫ����Ľ��
     * @return
     */
    boolean offer(Node node);

    /**
     * 返回在最近一次搜索或自上次 reset 后观察到的最大 Frontier 大小。
     */
    int getMaxSize();

    /**
     * 将内部的最大值统计重置为当前大小（或 0）。在每次新问题开始前调用。
     */
    void resetMaxSize();
}
