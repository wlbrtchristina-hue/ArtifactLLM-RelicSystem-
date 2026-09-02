package cn.edu.hnu.artifactrelic.service;

import cn.edu.hnu.artifactrelic.vo.StatisticsVO;

import java.util.List;

public interface StatisticsService {
    /**
     * 统计文物年代分布
     * @return List<StatisticsVO>
     */
    List<StatisticsVO> getEraStatistics();

    /**
     * 统计文物材质分布
     * @return List<StatisticsVO>
     */
    List<StatisticsVO> getMaterialStatistics();

    /**
     * 统计文物入库趋势（按月）
     * @return List<StatisticsVO>
     */
    List<StatisticsVO> getTrendStatistics();
}
