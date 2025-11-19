package core.solver.algorithm.heuristic;

public enum HeuristicType {
    // N-puzzle 相关启发类型
    MISPLACED,  // 瓷砖是否在位（错位数）
    MANHATTAN,  // 曼哈顿距离
    BLANK_DISTANCE,
    DISJOINT_PATTERN,

    // PathFinding 启发类型（网格寻路）
    PF_EUCLID,      // 欧几里得距离
    PF_MANHATTAN,   // 网格曼哈顿距离（admissible）
    PF_GRID,        // 网格距离（基于网格代价/地形）

    // 其他启发或元启发
    MC_HARMONY  // Monte Carlo / Harmony 等试验性启发

}
