package core.runner;

import algs4.util.StopwatchCPU;
import core.problem.Problem;
import core.problem.ProblemType;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Node;
import core.solver.algorithm.heuristic.HeuristicType;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

import static core.solver.algorithm.heuristic.HeuristicType.*;

/**
 * 对学生的搜索算法进行检测的主程序
 * arg0: 问题输入样例      resources/pathfinding.txt
 * arg1: 问题类型         PATHFINDING
 * arg2: 项目的哪个阶段    1
 * arg3: 各小组的Feeder   stud.runner.WalkerFeeder
 */
public final class SearchTester {

    // 是否显示 GUI 可视化（由命令行第5个参数控制，值为 "gui" 开启）
    private static boolean ENABLE_GUI = false;

    public static void main(String[] args) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException, FileNotFoundException {
        //根据args[3]提供的类名生成学生的EngineFeeder对象
        //这里使用的feeder最后换成我们编写的PuzzleFeeder就可以了
        EngineFeeder feeder = (EngineFeeder)
                Class.forName(args[3])
                        .getDeclaredConstructor().newInstance();

    ////从文件读入所有输入样例的文本； args[0]：输入样例文件的相对路径
        // 输入的样例文本在resources中
        Scanner scanner = new Scanner(new File(args[0]));
        // getProblemLines方法我们可以不必理会，我们只需要知道problemLines的结构就行
        // problemLines是一个字符串数组，该数组的每一个元素对应着一个puzzle问题
        // 示例："3 5 0 8 4 2 1 7 3 6 1 2 3 4 5 6 7 8 0"
        // 其中的某个元素可能是这样的，元素中的第一个字符代表着问题的size，这个示例问题就是3*3的，第二个数字到第十个表示着初始状态，后面表示目标状态
        ArrayList<String> problemLines = getProblemLines(scanner);

        //feeder从输入样例文本获取寻路问题的所有实例
        //将每一个字符串转换为具体的问题实例，得到一个包含所有问题的数组，之后将这个数组遍历，并解决每一个问题就可以了
        //feeder.getProblems是待实现的
        ArrayList<Problem> problems1 = feeder.getProblems(problemLines);
        //problem在astar中被清空，需要复制一份
        ArrayList<Problem> problems2 = feeder.getProblems(problemLines);
        ArrayList<Problem> problems3 = feeder.getProblems(problemLines);
        ArrayList<Problem> problems4 = feeder.getProblems(problemLines);

    ////问题实例读入到ArrayList中

        //当前问题的类型 args[1]    寻路问题，数字推盘，野人传教士过河等
        ProblemType type = ProblemType.valueOf(args[1]);
        //任务第几阶段 args[2]：可选，1/2/3 指定单个阶段；传 0 或不传 则运行所有阶段
        int requestedStage = 0; // 0 表示全部阶段
        if (args.length > 2) {
            try {
                requestedStage = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                // 非数字参数则当成 0（全部）处理
                requestedStage = 0;
            }
        }

        // 可选参数：args[4] = "gui" 会开启 Path_Player 可视化（默认关闭，适合批量实验时禁用）
        if (args.length > 4 && "gui".equalsIgnoreCase(args[4])) {
            ENABLE_GUI = true;
        }

        //根据问题类型和当前阶段，获取所有启发函数的类型
        //寻路问题分别使用Grid距离和Euclid距离作为启发函数
        //可以看一下getHeuristicTypes函数，第一阶段会向数组中添加MANHATTAN，先注释掉了其中一个
        int step1 = 1; // 第一阶段标识
        ArrayList<HeuristicType> heuristics = getHeuristicTypes(type, step1);

        //第一阶段astar
        if (requestedStage == 0 || requestedStage == 1) {
            System.out.println("第一阶段：astar\n");
            for (HeuristicType heuristicType : heuristics) {
                //solveProblems方法根据不同启发函数生成不同的searcher
                //从Feeder获取所使用的搜索引擎（AStar，IDAStar等）
                // 这里要关注solveProblems方法和feeder.getAstar方法，后面需要使用getIdaStar
                solveProblems(problems1, feeder.getAStar(heuristicType), heuristicType, "A*");
                problems1 = feeder.getProblems(problemLines);
                System.out.println();
            }
        }



        //第二阶段idastar
        if (requestedStage == 0 || requestedStage == 2) {
            System.out.println("第二阶段：idastar\n");
            int step2 = 2;
            ArrayList<HeuristicType> heuristics2 = getHeuristicTypes(type, step2);
            //idastar
            for (HeuristicType heuristicType : heuristics2) {
                solveProblems(problems2, feeder.getIdaStar(heuristicType), heuristicType, "IDA*");
                System.out.println();
            }
        }



        //第三阶段，使用使用disjoint pattern和模式数据库
        if (requestedStage == 0 || requestedStage == 3) {
            System.out.println("第三阶段：disjoint pattern\n");
            int step3 = 3;
            ArrayList<HeuristicType> heuristics3 = getHeuristicTypes(type, step3);

            //astar
            for (HeuristicType heuristicType : heuristics3) {
                solveProblems(problems3, feeder.getAStar(heuristicType), heuristicType, "A*");
                System.out.println();
            }
            //idastar
            for (HeuristicType heuristicType : heuristics3) {
                solveProblems(problems4, feeder.getIdaStar(heuristicType), heuristicType, "IDA*");
                System.out.println();
            }
        }

    }

    /**
     * 根据问题类型和当前阶段，获取所有启发函数的类型
     * @param type
     * @param step
     * @return
     */
    private static ArrayList<HeuristicType> getHeuristicTypes(ProblemType type, int step) {
        //求解当前问题在当前阶段可用的启发函数类型列表
        ArrayList<HeuristicType> heuristics = new ArrayList<>();
        //根据不同的问题类型，进行不同的测试
        if (type == ProblemType.PATHFINDING) {
            heuristics.add(PF_GRID);
            heuristics.add(PF_EUCLID);
        }
        else {
            //NPuzzle问题的第一阶段，使用不在位将牌和曼哈顿距离 A*， 只能跑3*3的，4*4会内存溢出
            if (step == 1) {
                heuristics.add(MISPLACED);
                heuristics.add(MANHATTAN);
            }
            //IDA* 4*4最后一个完全逆序样例跑不了，内存溢出（18G以上）
            else if(step == 2)
            {
                heuristics.add(MISPLACED);
                heuristics.add(MANHATTAN);
            }
            //NPuzzle问题的第三阶段，使用Disjoint Pattern A* IDA*， 可以轻松跑完跑所有样例
            else if (step == 3){
                heuristics.add(DISJOINT_PATTERN);
            }
        }
        return heuristics;
    }

    /**
     * 使用给定的searcher，求解问题集合中的所有问题，同时使用解检测器对求得的解进行检测
     * @param problems     问题集合
     * @param searcher     searcher
     * @param heuristicType 使用哪种启发函数？
     */
    private static void solveProblems(ArrayList<Problem> problems, AbstractSearcher searcher, HeuristicType heuristicType, String algorithmName) {
        for (Problem problem : problems) {
            // 先输出并检查问题的可解性
            boolean solvable = problem.solvable();
            System.out.println(STR."问题可解性：\{solvable}");
            if (!solvable) {
                System.out.println(STR."算法：\{algorithmName}，不可解，跳过搜索");
                continue;
            }
            // 使用AStar/IDA*引擎求解问题
            // 在每个问题开始前重置 frontier 的最大值统计
            searcher.resetFrontierMax();

            StopwatchCPU timer1 = new StopwatchCPU();
            // search是具体解决问题的方法
            Deque<Node> path = searcher.search(problem);
            double time1 = timer1.elapsedTime();

            if (path == null) {
                String timeStrNull = String.format(java.util.Locale.US, "%.5f", time1);
                System.out.println(STR."算法：\{algorithmName}，No Solution，执行了\{timeStrNull}s，共生成了\{searcher.nodesGenerated()}个结点，扩展了\{searcher.nodesExpanded()}个结点");
                // 写入 CSV（追加）
                writeCsvLine(algorithmName, heuristicType, time1, searcher.nodesGenerated(), searcher.nodesExpanded(), searcher.getFrontierMaxSize(), 0);
                continue;
            }
            String timeStr = String.format(java.util.Locale.US, "%.5f", time1);
            String result = String.format(java.util.Locale.US,
            "Algorithm: %s\nDuring %s s. we explore %d nodes, the length of path is %d\n Close this window to continue...\n",
            algorithmName, timeStr, searcher.nodesExpanded(), path.size());

            System.out.println(STR."算法：\{algorithmName}，启发函数：\{heuristicType}，解路径长度：\{path.size()}，执行了\{timeStr}s，共生成了\{searcher.nodesGenerated()}个结点，扩展了\{searcher.nodesExpanded()}个结点");
            // 在控制台打印解路径（按顺序），不修改原有 path 对象的使用
            System.out.println("Solution path (states from start to goal):");
            ArrayDeque<Node> copy = new ArrayDeque<>(path);
            for (Node nState : copy) {
                // 每个 state 实现了 draw()，直接调用以得到文本输出
                nState.getState().draw();
                System.out.println();
            }

//            用于路径解路径可视化（仅在 ENABLE_GUI 为 true 时启用）
            if (ENABLE_GUI) {
                Path_Player n = new Path_Player();
                n.setImage("resources/kobe.png");
                n.init();
                n.display(path,result);
                // 最后将时间写入文件
                n.writeResultToFile(result, "path_states.txt");
            }
            // 写入 CSV（追加）
            writeCsvLine(algorithmName, heuristicType, time1, searcher.nodesGenerated(), searcher.nodesExpanded(), searcher.getFrontierMaxSize(), path.size());
        }
    }

        private static void writeCsvLine(String algorithmName, HeuristicType heuristicType, double time, long nodesGenerated, long nodesExpanded, int maxFrontier, int solutionLen) {
            File dir = new File("results");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, "experiment_results.csv");
            boolean writeHeader = !file.exists();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                if (writeHeader) {
                    bw.write("algorithm,heuristic,time_s,nodes_generated,nodes_expanded,max_frontier,solution_len\n");
                }
                String line = String.format(java.util.Locale.US, "%s,%s,%.5f,%d,%d,%d,%d\n",
                        algorithmName, heuristicType.name(), time, nodesGenerated, nodesExpanded, maxFrontier, solutionLen);
                bw.write(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    /**
     * 从文件读入问题实例的字符串，放入字符串数组里
     * @param scanner
     * @return
     */
    public static ArrayList<String> getProblemLines(Scanner scanner) {
        ArrayList<String> lines = new ArrayList<>();
        while (scanner.hasNext()){
            lines.add(scanner.nextLine());
        }
        return lines;
    }
}