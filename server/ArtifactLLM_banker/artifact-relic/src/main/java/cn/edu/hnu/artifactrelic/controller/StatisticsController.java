package cn.edu.hnu.artifactrelic.controller;

import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactrelic.service.StatisticsService;
import cn.edu.hnu.artifactrelic.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取文物年代分布统计
     */
    @GetMapping("/era")
    public Result<List<StatisticsVO>> getEraStatistics() {
        List<StatisticsVO> stats = statisticsService.getEraStatistics();
        return Result.success(stats);
    }

    /**
     * 获取文物材质分布统计
     */
    @GetMapping("/material")
    public Result<List<StatisticsVO>> getMaterialStatistics() {
        List<StatisticsVO> stats = statisticsService.getMaterialStatistics();
        return Result.success(stats);
    }

    /**
     * 获取文物入库趋势统计（按月）
     */
    @GetMapping("/trend")
    public Result<List<StatisticsVO>> getTrendStatistics() {
        List<StatisticsVO> stats = statisticsService.getTrendStatistics();
        return Result.success(stats);
    }
}
