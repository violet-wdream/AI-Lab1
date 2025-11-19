# AI-Lab1 – N-Puzzle Search Experiments

2025 秋《人工智能导论》实验一：N-Puzzle 搜索算法对比与可视化。

## ✅ 快速运行

### IntelliJ IDEA 运行配置

仓库中已经包含 `.idea/runConfigurations` 目录，克隆后导入 IDEA 即可看到两个现成的 Application 配置：

| 配置名称 | 参数 | 说明 |
| --- | --- | --- |
| `SearchTester – All Stages` | `resources/problems.txt NPUZZLE 0 stud.runner.PuzzleFeeder` | 从 `resources/problems.txt` 读取问题，依次运行三个阶段（A*、IDA*、Disjoint Pattern）。|
| `SearchTester – GUI Demo` | `resources/problems.txt NPUZZLE 1 stud.runner.PuzzleFeeder gui` | 运行阶段 1 并开启 `Path_Player` GUI，可视化单条解路径。|

> 如果 IDEA 没有自动加载配置，可在 “Run/Debug Configurations” 面板选择 `Import Configuration`，直接选取 `.idea/runConfigurations/*.xml`。

### 命令行运行

在项目根目录执行：

```powershell
cd D:\IDEs\Idea\IntellijIdea\Projects\npuzzle-master
java -cp "src;lib/jsfml.jar" core.runner.SearchTester resources/problems.txt NPUZZLE 0 stud.runner.PuzzleFeeder
```

参数含义：

- `resources/problems.txt`：测试用例文件，可替换为 `resources/problems2.txt` 等。
- `NPUZZLE`：问题类型。
- `0`：阶段（0 表示全部阶段，1/2/3 对应单个阶段）。
- `stud.runner.PuzzleFeeder`：自定义 Feeder。
- 末尾可追加 `gui` 开启可视化。

## 📦 项目结构

- `src/core`：框架提供的通用搜索组件。
- `src/stud`：学生实现（frontier、heuristic、feeder、IDA* 等）。
- `resources/`：示例问题、GUI 贴图等资源。
- `scripts/`：实验数据分析脚本、样例 CSV。
- `analysis/visualization_report.md`：解路径可视化实现的结构化说明。

## 📊 实验数据分析

执行搜索后会在 `results/experiment_results.csv` 生成指标记录（时间、节点数、最大 Frontier 等）。

运行脚本生成对比图：

```powershell
python scripts/analyze_results.py results/experiment_results.csv --out-dir analysis_results --show
```

输出：

- `analysis_results/algorithm_comparison.png`
- `analysis_results/ida_heuristic_comparison.png`

## 🔧 依赖与环境

- Java 17+（可用 IDEA 的内置 JDK）。
- JSFML：已随仓库提供 `lib/jsfml.jar`。
- Python 3.10+（仅在使用分析脚本时需要）。

首次克隆后建议执行：

```powershell
pip install pandas matplotlib
```

## 📝 资料

- `analysis/visualization_report.md`：GUI 渲染流程及扩展建议。
- `scripts/3HeuristicIDAstar.csv`、`scripts/Astar+IDAstar.csv`：早期实验样例。

欢迎在此基础上扩展启发式、优化 Frontier 或撰写实验报告。
