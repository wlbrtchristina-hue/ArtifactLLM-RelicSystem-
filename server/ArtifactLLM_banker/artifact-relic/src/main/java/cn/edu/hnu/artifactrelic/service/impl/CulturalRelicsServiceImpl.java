package cn.edu.hnu.artifactrelic.service.impl;

import cn.edu.hnu.artifactrelic.dto.RelicQueryDTO;
import cn.edu.hnu.artifactrelic.entity.CulturalRelics;
import cn.edu.hnu.artifactrelic.entity.RelicsType;
import cn.edu.hnu.artifactrelic.mapper.CulturalRelicsMapper;
import cn.edu.hnu.artifactrelic.mapper.RelicsTypeMapper;
import cn.edu.hnu.artifactrelic.service.CulturalRelicsService;
import cn.edu.hnu.artifactrelic.vo.RelicsBasicVO;
import cn.edu.hnu.artifactrelic.vo.RelicsDetailVO;
import cn.edu.hnu.artifactrelic.vo.SimpleRelationVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.domain.Result;
import org.ansj.domain.Term;

import java.util.*;
import java.util.stream.Collectors;

// CulturalRelicsServiceImpl.java
@Service
@Slf4j
public class CulturalRelicsServiceImpl extends ServiceImpl<CulturalRelicsMapper, CulturalRelics> implements CulturalRelicsService {

    @Autowired
    private CulturalRelicsMapper culturalRelicsMapper;

    @Autowired
    private RelicsTypeMapper relicsTypeMapper;
    //获取所有文物简单信息
    @Override
    public Page<RelicsBasicVO> getRelicsByCondition(RelicQueryDTO queryDTO, Page<RelicsBasicVO> page) {
        try {
            // 1. 如果传入了typeName，查询对应的typeId
            if (StringUtils.hasText(queryDTO.getTypeName())) {
                Integer typeId = getTypeIdByName(queryDTO.getTypeName());
                if (typeId == null) {
                    // 如果类型不存在，返回空结果
                    log.warn("文物类型不存在: {}", queryDTO.getTypeName());
                    return new Page<>(page.getCurrent(), page.getSize(), 0);
                }
                queryDTO.setRelicsTypeId(typeId);
            }
            // 1. 执行分页查询
            Page<RelicsBasicVO> relicsPage = culturalRelicsMapper.selectByCondition(page, queryDTO);

            if (relicsPage == null || relicsPage.getRecords().isEmpty()) {
                return relicsPage;
            }

            // 2. 获取所有文物ID
            List<Integer> relicIds = relicsPage.getRecords().stream()
                    .map(RelicsBasicVO::getRelicsId)
                    .collect(Collectors.toList());

            // 3. 批量查询文物图片（优化性能）
            Map<Integer, List<String>> relicImagesMap = getRelicImagesMap(relicIds);

            // 4. 组装结果
            for (RelicsBasicVO relic : relicsPage.getRecords()) {
                List<String> images = relicImagesMap.getOrDefault(relic.getRelicsId(), new ArrayList<>());
                relic.setImages(images);
            }

            return relicsPage;
        } catch (Exception e) {
            log.error("查询文物列表失败，条件: {}", queryDTO, e);
            throw new RuntimeException("查询文物列表失败: " + e.getMessage());
        }
    }

    @Override
    public Map<Integer, List<String>> getRelicImagesMap(List<Integer> relicIds) {
        Map<Integer, List<String>> result = new HashMap<>();

        try {
            // 1. 批量查询文物图片
            List<Map<String, Object>> imageResults = culturalRelicsMapper.getRelicImagesBatch(relicIds);

            for (Map<String, Object> row : imageResults) {
                Integer relicId = (Integer) row.get("relics_id");
                String imageUrls = (String) row.get("images");

                if (relicId != null && StringUtils.hasText(imageUrls)) {
                    // 分割逗号分隔的图片URL字符串
                    List<String> imageList = Arrays.stream(imageUrls.split(","))
                            .filter(url -> StringUtils.hasText(url))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    result.put(relicId, imageList);
                }
            }
        } catch (Exception e) {
            log.error("批量查询文物图片失败", e);
            // 如果批量查询失败，可以降级为单个查询
            for (Integer relicId : relicIds) {
                try {
                    List<String> images = culturalRelicsMapper.getRelicImages(relicId);
                    result.put(relicId, images != null ? images : new ArrayList<>());
                } catch (Exception ex) {
                    log.warn("查询文物图片失败，文物ID: {}", relicId, ex);
                    result.put(relicId, new ArrayList<>());
                }
            }
        }

        return result;
    }

    /**
     * 根据类型名称获取类型ID
     */
    private Integer getTypeIdByName(String typeName) {
        try {
            // 查询文物类型表，根据类型名称获取ID
            RelicsType relicsType = relicsTypeMapper.selectOne(
                    new QueryWrapper<RelicsType>()
                            .eq("type_name", typeName)
                            .eq("is_deleted", 0)
            );
            return relicsType != null ? relicsType.getRelicsTypeId() : null;
        } catch (Exception e) {
            log.error("查询文物类型失败，类型名称: {}", typeName, e);
            return null;
        }
    }

    @Override
    public RelicsDetailVO getRelicDetail(Integer relicsId) throws Exception {
        try {
            // 1. 查询文物基本信息
            RelicsDetailVO detailVO = culturalRelicsMapper.selectRelicDetailById(relicsId);
            if (detailVO == null) {
                throw new Exception("文物不存在或已被删除");
            }

            // 2. 查询文物图片
            List<String> images = culturalRelicsMapper.selectRelicImages(relicsId);
            detailVO.setImages(images);

            // 3. 查询文物关系（知识图谱）
            List<SimpleRelationVO> relations = culturalRelicsMapper.selectRelicRelations(relicsId);
            detailVO.setRelations(relations);

            return detailVO;

        } catch (Exception e) {
            log.error("查询文物详情失败，文物ID: {}", relicsId, e);
            throw new Exception("查询文物详情失败: " + e.getMessage());
        }
    }

    @Override
    public Page<RelicsBasicVO> searchSemantic(String q, Page<RelicsBasicVO> page) {
        String query = q == null ? "" : q.trim();
        List<String> tokens = tokenize(query);
        Page<RelicsBasicVO> relicsPage = culturalRelicsMapper.semanticSearch(page, tokens);
        if (relicsPage == null || relicsPage.getRecords().isEmpty()) {
            return relicsPage;
        }
        List<Integer> relicIds = relicsPage.getRecords().stream().map(RelicsBasicVO::getRelicsId).collect(Collectors.toList());
        Map<Integer, List<String>> relicImagesMap = getRelicImagesMap(relicIds);
        for (RelicsBasicVO relic : relicsPage.getRecords()) {
            List<String> images = relicImagesMap.getOrDefault(relic.getRelicsId(), new ArrayList<>());
            relic.setImages(images);
        }
        return relicsPage;
    }

    @Override
    public List<RelicsBasicVO> findByNameExact(String name) {
        String q = name == null ? "" : name.trim();
        List<RelicsBasicVO> list = culturalRelicsMapper.selectByNameExact(q);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<Integer> relicIds = list.stream().map(RelicsBasicVO::getRelicsId).collect(Collectors.toList());
        Map<Integer, List<String>> relicImagesMap = getRelicImagesMap(relicIds);
        for (RelicsBasicVO relic : list) {
            List<String> images = relicImagesMap.getOrDefault(relic.getRelicsId(), new ArrayList<>());
            relic.setImages(images);
        }
        return list;
    }

    @Override
    public Page<RelicsBasicVO> findRelatedByPrimary(RelicsBasicVO primary, Page<RelicsBasicVO> page) {
        String typeName = primary.getTypeName();
        String era = primary.getEra();
        List<Integer> excludeIds = Collections.singletonList(primary.getRelicsId());
        Page<RelicsBasicVO> relatedPage = culturalRelicsMapper.selectRelatedByTypeOrEra(page, typeName, era, excludeIds);
        if (relatedPage == null || relatedPage.getRecords().isEmpty()) {
            return relatedPage;
        }
        for (RelicsBasicVO r : relatedPage.getRecords()) {
            if (Objects.equals(r.getTypeName(), typeName)) {
                r.setRelevance("同一类型");
            } else if (Objects.equals(r.getEra(), era)) {
                r.setRelevance("同一年代");
            }
        }
        relatedPage.setRecords(relatedPage.getRecords().stream()
                .sorted(Comparator.comparing((RelicsBasicVO v) -> "同一类型".equals(v.getRelevance()) ? 0 : 1))
                .collect(Collectors.toList()));
        List<Integer> relicIds = relatedPage.getRecords().stream().map(RelicsBasicVO::getRelicsId).collect(Collectors.toList());
        Map<Integer, List<String>> relicImagesMap = getRelicImagesMap(relicIds);
        for (RelicsBasicVO relic : relatedPage.getRecords()) {
            List<String> images = relicImagesMap.getOrDefault(relic.getRelicsId(), new ArrayList<>());
            relic.setImages(images);
        }
        return relatedPage;
    }

    private List<String> tokenize(String q) {
        if (!StringUtils.hasText(q)) {
            return Collections.emptyList();
        }
        org.ansj.domain.Result ansjResult = ToAnalysis.parse(q);
        List<String> tokens = ansjResult.getTerms().stream()
                .map(org.ansj.domain.Term::getName)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        if (tokens.isEmpty()) {
            List<String> chars = new ArrayList<>();
            for (int i = 0; i < q.length(); i++) {
                chars.add(String.valueOf(q.charAt(i)));
            }
            return chars;
        }
        return tokens;
    }
}
