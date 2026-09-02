package cn.edu.hnu.artifactrelic.service.impl;

import cn.edu.hnu.artifactrelic.entity.CulturalRelics;
import cn.edu.hnu.artifactrelic.mapper.CulturalRelicsMapper;
import cn.edu.hnu.artifactrelic.service.StatisticsService;
import cn.edu.hnu.artifactrelic.vo.StatisticsVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private CulturalRelicsMapper culturalRelicsMapper;

    @Override
    public List<StatisticsVO> getEraStatistics() {
        QueryWrapper<CulturalRelics> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("era as name", "count(*) as value")
                .groupBy("era")
                .isNotNull("era");
        
        List<Map<String, Object>> resultMaps = culturalRelicsMapper.selectMaps(queryWrapper);
        return mapToStatisticsVO(resultMaps);
    }

    @Override
    public List<StatisticsVO> getMaterialStatistics() {
        return new ArrayList<>();
    }

    @Override
    public List<StatisticsVO> getTrendStatistics() {
        QueryWrapper<CulturalRelics> queryWrapper = new QueryWrapper<>();
        // MySQL format
        queryWrapper.select("DATE_FORMAT(created_at, '%Y-%m') as name", "count(*) as value")
                .groupBy("name")
                .orderByAsc("name");

        List<Map<String, Object>> resultMaps = culturalRelicsMapper.selectMaps(queryWrapper);
        return mapToStatisticsVO(resultMaps);
    }

    private List<StatisticsVO> mapToStatisticsVO(List<Map<String, Object>> resultMaps) {
        List<StatisticsVO> voList = new ArrayList<>();
        if (resultMaps != null) {
            for (Map<String, Object> map : resultMaps) {
                String name = (String) map.get("name");
                // value might be Long or Integer depending on driver
                Number value = (Number) map.get("value");
                if (name != null) {
                    voList.add(new StatisticsVO(name, value != null ? value.longValue() : 0L));
                }
            }
        }
        return voList;
    }
}
