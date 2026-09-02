package cn.edu.hnu.artifactrelic.service.impl;

import cn.edu.hnu.artifactrelic.entity.CulturalRelics;
import cn.edu.hnu.artifactrelic.entity.RelicsType;
import cn.edu.hnu.artifactrelic.mapper.CulturalRelicsMapper;
import cn.edu.hnu.artifactrelic.mapper.RelicsTypeMapper;
import cn.edu.hnu.artifactrelic.service.RelicQueryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import cn.edu.hnu.artifactcommon.utils.RedisUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelicQueryServiceImpl implements RelicQueryService {
    @Autowired
    private CulturalRelicsMapper culturalRelicsMapper;
    @Autowired
    private RelicsTypeMapper relicsTypeMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public List<String> getAllEras() {
        try {
            Object cached = redisUtil.get("artifact:relic:dict:eras");
            if (cached instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> v = (List<String>) cached;
                if (!v.isEmpty()) {
                    return v;
                }
            }
            QueryWrapper<CulturalRelics> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("DISTINCT era")
                    .isNotNull("era")
                    .ne("era", "")
                    .eq("is_deleted", 0)
                    .orderByAsc("era");

            List<CulturalRelics> relics = culturalRelicsMapper.selectList(queryWrapper);

            List<String> eras = relics.stream()
                    .map(CulturalRelics::getEra)
                    .filter(era -> StringUtils.hasText(era))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());

            log.info("查询到{}个年代", eras.size());
            redisUtil.set("artifact:relic:dict:eras", eras, 1800);
            return eras;

        } catch (Exception e) {
            log.error("查询年代列表失败", e);
            throw new RuntimeException("查询年代列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getAllMaterials() {
        return List.of();
    }

    @Override
    public List<String> getAllTypes() {
        try {
            Object cached = redisUtil.get("artifact:relic:dict:types");
            if (cached instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> v = (List<String>) cached;
                if (!v.isEmpty()) {
                    return v;
                }
            }
            QueryWrapper<RelicsType> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("DISTINCT type_name")
                    .isNotNull("type_name")
                    .ne("type_name", "")
                    .eq("is_deleted", 0)
                    .orderByAsc("type_name");

            List<RelicsType> types = relicsTypeMapper.selectList(queryWrapper);

            List<String> typeNames = types.stream()
                    .map(RelicsType::getTypeName)
                    .filter(typeName -> StringUtils.hasText(typeName))
                    .map(String::trim)
                    .distinct()
                    .collect(Collectors.toList());

            log.info("查询到{}个文物类型", typeNames.size());
            redisUtil.set("artifact:relic:dict:types", typeNames, 1800);
            return typeNames;

        } catch (Exception e) {
            log.error("查询文物类型列表失败", e);
            throw new RuntimeException("查询文物类型列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getAllSites() {
        return List.of();
    }
}
