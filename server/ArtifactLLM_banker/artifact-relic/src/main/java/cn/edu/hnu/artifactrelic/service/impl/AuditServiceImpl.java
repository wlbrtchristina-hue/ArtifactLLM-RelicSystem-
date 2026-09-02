package cn.edu.hnu.artifactrelic.service.impl;

import cn.edu.hnu.artifactrelic.dto.AuditActionDTO;
import cn.edu.hnu.artifactrelic.entity.*;
import cn.edu.hnu.artifactrelic.mapper.*;
import cn.edu.hnu.artifactrelic.service.AuditService;
import cn.edu.hnu.artifactrelic.vo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuditServiceImpl extends ServiceImpl<AuditMapper, Audit> implements AuditService {
    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private CulturalRelicsMapper culturalRelicsMapper;

    @Autowired
    private RelicMultiModeMapper relicMultiModeMapper;

    @Autowired
    private RelicsTypeMapper relicsTypeMapper;

    @Autowired
    private EntityRelationMapper entityRelationMapper;

    //根据审核状态查询审核
    @Override
    public List<AuditVO> getAuditsByStatus(String status) {
        // 验证状态
        if (!isValidStatus(status)) {
            throw new RuntimeException("无效的审核状态: " + status);
        }

        // 查询审核记录
        List<AuditVO> audits = auditMapper.selectByStatus(status);

        // 处理实体名称
        for (AuditVO audit : audits) {
            String entityName = extractEntityName((String) audit.getAuditData(), audit.getAuditTypeId());
            audit.setEntityName(entityName);
        }

        return audits;
    }

    //验证审核状态是否有效
    private boolean isValidStatus(String status) {
        return "pending".equals(status) || "approved".equals(status) || "rejected".equals(status);
    }

    //根据id查询审核详情
    @Override
    public AuditVO getAuditDetail(Long auditId) {
        if (auditId == null || auditId <= 0) {
            throw new RuntimeException("无效的审核ID");
        }

        AuditVO audit = auditMapper.selectAuditDetailById(auditId);
        if (audit == null) {
            throw new RuntimeException("审核记录不存在");
        }

        // 处理实体名称
        String entityName = extractEntityName(audit.getAuditDataJson(), audit.getAuditTypeId());
        audit.setEntityName(entityName);

        // 处理审核数据JSON
        if (StringUtils.hasText(audit.getAuditDataJson())) {
            try {
                Object auditData = JSON.parse(audit.getAuditDataJson());
                audit.setAuditData(auditData);
            } catch (Exception e) {
                // JSON解析失败，返回原始字符串
                audit.setAuditData(audit.getAuditDataJson());
            }
        }

        return audit;
    }

    //通过审核
    @Override
    @Transactional
    public void approveAudit(Long auditId, AuditActionDTO actionDTO) {
        // 1. 查询审核记录
        Audit audit = auditMapper.selectById(auditId);
        if (audit == null) {
            throw new RuntimeException("审核记录不存在");
        }

        if (!"pending".equals(audit.getAuditStatus())) {
            throw new RuntimeException("审核记录状态不是待审核");
        }

        // 2. 根据审核类型执行相应的业务逻辑
        executeAuditAction(audit);

        // 3. 更新审核记录状态
        audit.setAuditStatus("approved");
        audit.setAuditorId(actionDTO.getAuditorId());
        audit.setAuditTime(LocalDateTime.now());
        audit.setRejectReason(null);
        audit.setUpdatedAt(LocalDateTime.now());

        auditMapper.updateById(audit);
    }

    //拒绝审核
    @Override
    @Transactional
    public void rejectAudit(Long auditId, AuditActionDTO actionDTO) {
        if (!StringUtils.hasText(actionDTO.getRejectReason())) {
            throw new RuntimeException("拒绝原因不能为空");
        }

        Audit audit = auditMapper.selectById(auditId);
        if (audit == null) {
            throw new RuntimeException("审核记录不存在");
        }

        if (!"pending".equals(audit.getAuditStatus())) {
            throw new RuntimeException("审核记录状态不是待审核");
        }

        audit.setAuditStatus("rejected");
        audit.setAuditorId(actionDTO.getAuditorId());
        audit.setAuditTime(LocalDateTime.now());
        audit.setRejectReason(actionDTO.getRejectReason());
        audit.setUpdatedAt(LocalDateTime.now());

        auditMapper.updateById(audit);
    }

    /**
     * 根据固定格式的JSON提取实体名称
     */
    private String extractEntityName(String auditData, Long auditTypeId) {
        if (!StringUtils.hasText(auditData) || auditTypeId == null) {
            return "未知实体";
        }

        try {
            JSONObject json = JSONObject.parseObject(auditData);

            // 根据审核类型ID提取名称
            switch (auditTypeId.intValue()) {
                case 4: // 创建文物类型
                    return extractCreateTypeName(json);

                case 5: // 修改文物类型
                    return extractUpdateTypeName(json);

                case 6: // 删除文物类型
                    return extractDeleteTypeName(json);

                case 7: // 创建文物实体
                    return extractCreateRelicName(json);

                case 8: // 修改文物实体
                    return extractUpdateRelicName(json);

                case 9: // 删除文物实体
                    return extractDeleteRelicName(json);

                default:
                    return "未知实体类型";
            }
        } catch (Exception e) {
            return "数据解析错误";
        }
    }

    /**
     * 提取创建文物类型的实体名称
     */
    private String extractCreateTypeName(JSONObject json) {
        String typeName = json.getString("typeName");
        return typeName != null ? typeName : "新文物类型";
    }

    /**
     * 提取修改文物类型的实体名称
     */
    private String extractUpdateTypeName(JSONObject json) {
        // 优先从originalData中获取类型名称
        JSONObject originalData = json.getJSONObject("originalData");
        if (originalData != null) {
            String originalName = originalData.getString("typeName");
            if (originalName != null) {
                return originalName;
            }
        }

        // 其次从modifiedData中获取
        JSONObject modifiedData = json.getJSONObject("modifiedData");
        if (modifiedData != null) {
            String modifiedName = modifiedData.getString("typeName");
            if (modifiedName != null) {
                return modifiedName;
            }
        }

        // 最后从根节点获取
        return json.getString("typeName") != null ? json.getString("typeName") : "文物类型";
    }

    /**
     * 提取删除文物类型的实体名称
     */
    private String extractDeleteTypeName(JSONObject json) {
        String typeName = json.getString("typeName");
        return typeName != null ? typeName : "待删除类型";
    }

    /**
     * 提取创建文物实体的实体名称
     */
    private String extractCreateRelicName(JSONObject json) {
        String relicsName = json.getString("relicsName");
        return relicsName != null ? relicsName : "新文物";
    }

    /**
     * 提取修改文物实体的实体名称
     */
     private String extractUpdateRelicName(JSONObject json) {
     // 优先从originalData中获取文物名称
     JSONObject originalData = json.getJSONObject("originalData");
     if (originalData != null) {
     String originalName = originalData.getString("relicsName");
     if (originalName != null) {
     return originalName;
     }
     }

     // 其次从modifiedData中获取
     JSONObject modifiedData = json.getJSONObject("modifiedData");
     if (modifiedData != null) {
     String modifiedName = modifiedData.getString("relicsName");
     if (modifiedName != null) {
     return modifiedName;
     }
     }

     // 最后尝试从relicId推断
     Long relicId = json.getLong("relicId");
     if (relicId != null) {
     return "文物ID:" + relicId;
     }

     return "文物";
     }

     /**
     * 提取删除文物实体的实体名称
     */
    private String extractDeleteRelicName(JSONObject json) {
        String relicsName = json.getString("relicsName");
        if (relicsName != null) {
            return relicsName;
        }

        // 尝试从relicId推断
        Long relicId = json.getLong("relicId");
        if (relicId != null) {
            return "文物ID:" + relicId;
        }

        return "待删除文物";
    }

    /**
     * 根据审核类型执行相应的业务操作
     */
    private void executeAuditAction(Audit audit) {
        Long auditTypeId = audit.getAuditTypeId();
        String auditData = audit.getAuditData();

        if (!StringUtils.hasText(auditData)) {
            throw new RuntimeException("审核数据为空");
        }

        try {
            JSONObject jsonData = JSON.parseObject(auditData);

            switch (auditTypeId.intValue()) {
                case 4: // 创建文物类型
                    createRelicType(jsonData, audit.getCreatedBy());
                    // 获取新创建的类型ID
                    List<RelicsType> types = relicsTypeMapper.selectList(null);
                    if (!types.isEmpty()) {
                        Long newTypeId = Long.valueOf(types.get(types.size() - 1).getRelicsTypeId());
                        processRelicTypeRelations(newTypeId, jsonData, audit.getCreatedBy());
                    }
                    break;

                case 5: // 修改文物类型
                    updateRelicType(jsonData);
                    Long typeId = getLongValue(jsonData, "typeId");
                    if (typeId != null) {
                        processRelicTypeRelations(typeId, jsonData, audit.getCreatedBy());
                    }
                    break;

                case 6: // 删除文物类型
                    deleteRelicType(jsonData);
                    // 删除操作通常不需要处理新增关系
                    break;

                case 7: // 创建文物实体
                    createCulturalRelic(jsonData, audit.getCreatedBy());
                    // 获取新创建的文物ID
                    List<CulturalRelics> relics = culturalRelicsMapper.selectList(null);
                    if (!relics.isEmpty()) {
                        Long newRelicId = (long) relics.get(relics.size() - 1).getRelicsId();
                        processCulturalRelicRelations(newRelicId, jsonData, audit.getCreatedBy());
                    }
                    break;

                case 8: // 修改文物实体
                    updateCulturalRelic(jsonData); // 这个方法内部已经调用了图片处理
                    Long relicId = getLongValue(jsonData, "relicId");
                    if (relicId != null) {
                        processCulturalRelicRelations(relicId, jsonData, audit.getCreatedBy());
                    }
                    break;

                case 9: // 删除文物实体
                    deleteCulturalRelic(jsonData);
                    // 删除操作通常不需要处理新增关系
                    break;

                default:
                    throw new RuntimeException("不支持的审核类型: " + auditTypeId);
            }
        } catch (Exception e) {
            throw new RuntimeException("执行审核操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建文物类型
     */
    private void createRelicType(JSONObject jsonData, Long createdBy) {
        try {
            log.info("开始创建文物类型，数据: {}", jsonData.toJSONString());

            RelicsType relicsType = new RelicsType();
            relicsType.setTypeName(getStringValue(jsonData, "typeName"));
            relicsType.setDescription(getStringValue(jsonData, "description"));

            // 处理attributes字段 - 确保存储为JSON数组格式
            if (jsonData.containsKey("attributes")) {
                Object attributes = jsonData.get("attributes");
                log.info("原始attributes类型: {}, 值: {}",
                        attributes.getClass().getSimpleName(), attributes);

                if (attributes instanceof JSONArray) {
                    // 如果是JSONArray，直接转换为字符串（不转义）
                    relicsType.setTypeFields(attributes.toString());
                    log.info("存储为JSON数组格式");
                } else if (attributes instanceof String) {
                    // 如果是字符串，尝试解析为JSON
                    try {
                        JSONArray parsedArray = JSON.parseArray((String) attributes);
                        relicsType.setTypeFields(parsedArray.toString());
                        log.info("字符串解析为JSON数组格式");
                    } catch (Exception e) {
                        // 如果解析失败，直接存储字符串
                        relicsType.setTypeFields((String) attributes);
                        log.warn("属性字段解析失败，直接存储字符串");
                    }
                } else {
                    // 其他类型，转换为JSON字符串
                    relicsType.setTypeFields(JSON.toJSONString(attributes));
                    log.info("其他类型转换为JSON字符串");
                }
            }

            relicsType.setCreatedBy(Math.toIntExact(createdBy != null ? createdBy : 1L));
            relicsType.setCreatedAt(LocalDateTime.now());
            relicsType.setIsDeleted(false);

            relicsTypeMapper.insert(relicsType);
            log.info("创建文物类型成功，ID: {}", relicsType.getRelicsTypeId());

        } catch (Exception e) {
            log.error("创建文物类型失败", e);
            throw new RuntimeException("创建文物类型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 修改文物类型
     */
    private void updateRelicType(JSONObject jsonData) {
        Long typeId = getLongValue(jsonData, "typeId");
        if (typeId == null) {
            throw new RuntimeException("类型ID不能为空");
        }

        RelicsType existingType = relicsTypeMapper.selectById(typeId);
        if (existingType == null) {
            throw new RuntimeException("文物类型不存在");
        }

        JSONObject modifiedData = jsonData.getJSONObject("modifiedData");
        if (modifiedData != null) {
            if (modifiedData.containsKey("typeName")) {
                existingType.setTypeName(getStringValue(modifiedData, "typeName"));
            }
            if (modifiedData.containsKey("description")) {
                existingType.setDescription(getStringValue(modifiedData, "description"));
            }
            if (modifiedData.containsKey("attributes")) {
                Object attributes = modifiedData.get("attributes");
                if (attributes instanceof JSONArray) {
                    existingType.setTypeFields(attributes.toString());
                } else if (attributes instanceof String) {
                    existingType.setTypeFields((String) attributes);
                } else {
                    existingType.setTypeFields(JSON.toJSONString(attributes));
                }
            }
        }

        existingType.setUpdatedAt(LocalDateTime.now());
        relicsTypeMapper.updateById(existingType);
    }

    /**
     * 删除文物类型（连带删除属于该类型的文物）
     */
    private void deleteRelicType(JSONObject jsonData) {
        Long typeId = getLongValue(jsonData, "typeId");
        if (typeId == null) {
            throw new RuntimeException("类型ID不能为空");
        }

        // 1. 查询该类型下的所有文物
        List<CulturalRelics> relics = culturalRelicsMapper.selectByTypeId(typeId);

        // 2. 遍历删除每个文物（连带删除其相关资源）
        for (CulturalRelics relic : relics) {
            deleteRelicAndRelatedData(Long.valueOf(relic.getRelicsId()));
        }

        // 3. 逻辑删除文物类型
        RelicsType relicsType = relicsTypeMapper.selectById(typeId);
        if (relicsType != null) {
            relicsType.setIsDeleted(true);
            relicsType.setUpdatedAt(LocalDateTime.now());
            relicsTypeMapper.updateById(relicsType);
        }
    }

    /**
     * 删除文物及其相关数据
     */
    private void deleteRelicAndRelatedData(Long relicId) {
        if (relicId == null) return;

        // 1. 逻辑删除文物
        CulturalRelics relic = culturalRelicsMapper.selectById(relicId);
        if (relic != null) {
            relic.setIsDeleted(true);
            relic.setUpdatedAt(LocalDateTime.now());
            culturalRelicsMapper.updateById(relic);
        }

        // 2. 逻辑删除文物的多模态资源
        relicMultiModeMapper.logicalDeleteByRelicId(relicId);

        // 3. 逻辑删除文物的关系数据
        entityRelationMapper.logicalDeleteByRelicId(relicId);
    }

    /**
     * 创建文物实体
     */
    private void createCulturalRelic(JSONObject jsonData, Long createdBy) {
        CulturalRelics culturalRelic = new CulturalRelics();
        culturalRelic.setRelicsName(getStringValue(jsonData, "relicsName"));
        culturalRelic.setEra(getStringValue(jsonData, "era"));
        culturalRelic.setMaterial(getStringValue(jsonData, "material"));
        culturalRelic.setRelicsTypeId(Math.toIntExact(getLongValue(jsonData, "relicsTypeId")));
        culturalRelic.setDiscoverySite(getStringValue(jsonData, "discoverySite"));
        culturalRelic.setCurrentLocation(getStringValue(jsonData, "currentLocation"));
        culturalRelic.setDescription(getStringValue(jsonData, "description"));

        // 处理customFields - 如果是JSONObject，转换为JSON字符串
        if (jsonData.containsKey("customFields")) {
            Object customFields = jsonData.get("customFields");
            if (customFields instanceof JSONObject) {
                culturalRelic.setCustomFields(customFields.toString());
            } else if (customFields instanceof String) {
                culturalRelic.setCustomFields((String) customFields);
            } else {
                culturalRelic.setCustomFields(JSON.toJSONString(customFields));
            }
        }

        culturalRelic.setCreatedBy(Math.toIntExact(createdBy));
        culturalRelic.setCreatedAt(LocalDateTime.now());
        culturalRelic.setIsDeleted(false);

        culturalRelicsMapper.insert(culturalRelic);

        // 处理图片资源
        if (jsonData.containsKey("images")) {
            Object imagesObj = jsonData.get("images");
            if (imagesObj instanceof JSONArray) {
                JSONArray images = (JSONArray) imagesObj;
                for (int i = 0; i < images.size(); i++) {
                    String imageUrl = images.getString(i);
                    if (StringUtils.hasText(imageUrl)) {
                        RelicMultiMode multiMode = new RelicMultiMode();
                        multiMode.setRelicsId(culturalRelic.getRelicsId());
                        multiMode.setResourceType("image");
                        multiMode.setResourceContent(imageUrl);
                        multiMode.setCreatedBy(Math.toIntExact(createdBy));
                        multiMode.setCreatedAt(LocalDateTime.now());
                        multiMode.setIsDeleted(false);

                        relicMultiModeMapper.insert(multiMode);
                    }
                }
            }
        }
    }

    /**
     * 修改文物实体 - 增加图片处理
     */
    private void updateCulturalRelic(JSONObject jsonData) {
        Long relicId = getLongValue(jsonData, "relicId");
        if (relicId == null) {
            throw new RuntimeException("文物ID不能为空");
        }

        CulturalRelics existingRelic = culturalRelicsMapper.selectById(relicId);
        if (existingRelic == null) {
            throw new RuntimeException("文物不存在");
        }

        JSONObject modifiedData = jsonData.getJSONObject("modifiedData");
        if (modifiedData != null) {
            if (modifiedData.containsKey("relicsName")) {
                existingRelic.setRelicsName(getStringValue(modifiedData, "relicsName"));
            }
            if (modifiedData.containsKey("era")) {
                existingRelic.setEra(getStringValue(modifiedData, "era"));
            }
            if (modifiedData.containsKey("material")) {
                existingRelic.setMaterial(getStringValue(modifiedData, "material"));
            }
            if (modifiedData.containsKey("discoverySite")) {
                existingRelic.setDiscoverySite(getStringValue(modifiedData, "discoverySite"));
            }
            if (modifiedData.containsKey("currentLocation")) {
                existingRelic.setCurrentLocation(getStringValue(modifiedData, "currentLocation"));
            }
            if (modifiedData.containsKey("description")) {
                existingRelic.setDescription(getStringValue(modifiedData, "description"));
            }
            if (modifiedData.containsKey("customFields")) {
                Object customFields = modifiedData.get("customFields");
                if (customFields instanceof JSONObject) {
                    existingRelic.setCustomFields(customFields.toString());
                } else if (customFields instanceof String) {
                    existingRelic.setCustomFields((String) customFields);
                } else {
                    existingRelic.setCustomFields(JSON.toJSONString(customFields));
                }
            }
        }

        existingRelic.setUpdatedAt(LocalDateTime.now());
        culturalRelicsMapper.updateById(existingRelic);

        // 处理图片更新
        processImagesForUpdate(relicId, jsonData, existingRelic.getCreatedBy());
    }

    /**
     * 处理文物实体的图片更新
     */
    private void processImagesForUpdate(Long relicId, JSONObject jsonData, Integer createdBy) {
        if (!jsonData.containsKey("images")) {
            return;
        }

        try {
            JSONObject imagesData = jsonData.getJSONObject("images");
            if (imagesData == null) {
                return;
            }

            log.info("处理文物图片更新，文物ID: {}", relicId);

            // 处理新增图片
            if (imagesData.containsKey("added")) {
                JSONArray addedImages = imagesData.getJSONArray("added");
                if (addedImages != null && !addedImages.isEmpty()) {
                    for (int i = 0; i < addedImages.size(); i++) {
                        String imageUrl = addedImages.getString(i);
                        if (StringUtils.hasText(imageUrl)) {
                            RelicMultiMode multiMode = new RelicMultiMode();
                            multiMode.setRelicsId(relicId.intValue());
                            multiMode.setResourceType("image");
                            multiMode.setResourceContent(imageUrl);
                            multiMode.setCreatedBy(createdBy != null ? createdBy : 1);
                            multiMode.setCreatedAt(LocalDateTime.now());
                            multiMode.setIsDeleted(false);

                            try {
                                relicMultiModeMapper.insert(multiMode);
                                log.info("新增文物图片成功，文物ID: {}, 图片URL: {}", relicId, imageUrl);
                            } catch (Exception e) {
                                log.error("新增文物图片失败，文物ID: {}, 图片URL: {}", relicId, imageUrl, e);
                            }
                        }
                    }
                }
            }

            // 处理删除图片
            if (imagesData.containsKey("deleted")) {
                JSONArray deletedImages = imagesData.getJSONArray("deleted");
                if (deletedImages != null && !deletedImages.isEmpty()) {
                    for (int i = 0; i < deletedImages.size(); i++) {
                        String imageUrl = deletedImages.getString(i);
                        if (StringUtils.hasText(imageUrl)) {
                            try {
                                // 根据图片URL逻辑删除
                                int result = relicMultiModeMapper.logicalDeleteByUrl(relicId, imageUrl);
                                if (result > 0) {
                                    log.info("删除文物图片成功，文物ID: {}, 图片URL: {}", relicId, imageUrl);
                                } else {
                                    log.warn("未找到要删除的文物图片，文物ID: {}, 图片URL: {}", relicId, imageUrl);
                                }
                            } catch (Exception e) {
                                log.error("删除文物图片失败，文物ID: {}, 图片URL: {}", relicId, imageUrl, e);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("处理文物图片更新失败，文物ID: {}", relicId, e);
            throw new RuntimeException("处理文物图片更新失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文物实体
     */
    private void deleteCulturalRelic(JSONObject jsonData) {
        Long relicId = getLongValue(jsonData, "relicId");
        if (relicId == null) {
            throw new RuntimeException("文物ID不能为空");
        }

        CulturalRelics culturalRelic = culturalRelicsMapper.selectById(relicId);
        if (culturalRelic != null) {
            culturalRelic.setIsDeleted(true);
            culturalRelic.setUpdatedAt(LocalDateTime.now());
            culturalRelicsMapper.updateById(culturalRelic);

            // 逻辑删除相关资源
            relicMultiModeMapper.logicalDeleteByRelicId(relicId);

            // 逻辑删除相关关系
            entityRelationMapper.logicalDeleteByRelicId(relicId);
        }
    }

    /**
     * 处理文物类型的关系操作
     */
    private void processRelicTypeRelations(Long typeId, JSONObject jsonData, Long createdBy) {
        if (!jsonData.containsKey("relations")) {
            return;
        }

        try {
            JSONArray relations = jsonData.getJSONArray("relations");
            if (relations == null || relations.isEmpty()) {
                return;
            }

            log.info("处理文物类型关系，类型ID: {}, 关系数量: {}", typeId, relations.size());

            for (int i = 0; i < relations.size(); i++) {
                JSONObject relation = relations.getJSONObject(i);
                String operation = relation.getString("operation");

                if (!StringUtils.hasText(operation)) {
                    log.warn("关系操作类型为空，跳过处理");
                    continue;
                }

                switch (operation.toUpperCase()) {
                    case "INSERT":
                        insertRelicTypeRelation(typeId, relation, createdBy);
                        break;
                    case "ALTER":
                        alterRelicTypeRelation(relation, createdBy);
                        break;
                    case "DELETE":
                        deleteRelicTypeRelation(relation);
                        break;
                    default:
                        log.warn("不支持的关系操作类型: {}", operation);
                }
            }
        } catch (Exception e) {
            log.error("处理文物类型关系失败，类型ID: {}", typeId, e);
            throw new RuntimeException("处理文物类型关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 新增文物类型关系
     */
    private void insertRelicTypeRelation(Long typeId, JSONObject relation, Long createdBy) {
        try {
            EntityRelation entityRelation = new EntityRelation();
            entityRelation.setRelationName(getStringValue(relation, "relationName"));
            entityRelation.setSourceType("TYPE");
            entityRelation.setSourceId(typeId);
            entityRelation.setTargetString(getStringValue(relation, "targetString"));
            entityRelation.setRelationDescription(getStringValue(relation, "relationDescription"));
            entityRelation.setCreatedBy(Math.toIntExact(createdBy));
            entityRelation.setCreatedAt(LocalDateTime.now());
            entityRelation.setDeleted(false);

            entityRelationMapper.insert(entityRelation);
            log.info("新增文物类型关系成功，类型ID: {}, 关系名称: {}", typeId, entityRelation.getRelationName());
        } catch (Exception e) {
            log.error("新增文物类型关系失败，类型ID: {}", typeId, e);
            throw new RuntimeException("新增文物类型关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 修改文物类型关系
     */
    private void alterRelicTypeRelation(JSONObject relation, Long createdBy) {
        try {
            Long relationId = getLongValue(relation, "relationId");
            if (relationId == null) {
                throw new RuntimeException("修改关系时，relationId不能为空");
            }

            EntityRelation existingRelation = entityRelationMapper.selectById(relationId);
            if (existingRelation == null) {
                throw new RuntimeException("要修改的关系不存在: " + relationId);
            }

            // 验证关系是否属于文物类型
            if (!"TYPE".equals(existingRelation.getSourceType())) {
                throw new RuntimeException("关系不属于文物类型: " + relationId);
            }

            if (relation.containsKey("relationName")) {
                existingRelation.setRelationName(getStringValue(relation, "relationName"));
            }
            if (relation.containsKey("targetString")) {
                existingRelation.setTargetString(getStringValue(relation, "targetString"));
            }
            if (relation.containsKey("relationDescription")) {
                existingRelation.setRelationDescription(getStringValue(relation, "relationDescription"));
            }

            existingRelation.setUpdatedAt(LocalDateTime.now());
            entityRelationMapper.updateById(existingRelation);
            log.info("修改文物类型关系成功，关系ID: {}", relationId);
        } catch (Exception e) {
            log.error("修改文物类型关系失败", e);
            throw new RuntimeException("修改文物类型关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文物类型关系
     */
    private void deleteRelicTypeRelation(JSONObject relation) {
        try {
            Long relationId = getLongValue(relation, "relationId");
            if (relationId == null) {
                throw new RuntimeException("删除关系时，relationId不能为空");
            }

            EntityRelation existingRelation = entityRelationMapper.selectById(relationId);
            if (existingRelation != null) {
                // 验证关系是否属于文物类型
                if (!"TYPE".equals(existingRelation.getSourceType())) {
                    throw new RuntimeException("要删除的关系不属于文物类型: " + relationId);
                }

                existingRelation.setDeleted(true);
                existingRelation.setUpdatedAt(LocalDateTime.now());
                entityRelationMapper.updateById(existingRelation);
                log.info("删除文物类型关系成功，关系ID: {}", relationId);
            } else {
                log.warn("要删除的关系不存在: {}", relationId);
            }
        } catch (Exception e) {
            log.error("删除文物类型关系失败", e);
            throw new RuntimeException("删除文物类型关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理文物实体的关系操作
     */
    private void processCulturalRelicRelations(Long relicId, JSONObject jsonData, Long createdBy) {
        if (!jsonData.containsKey("relations")) {
            return;
        }

        try {
            JSONArray relations = jsonData.getJSONArray("relations");
            if (relations == null || relations.isEmpty()) {
                return;
            }

            log.info("处理文物实体关系，文物ID: {}, 关系数量: {}", relicId, relations.size());

            for (int i = 0; i < relations.size(); i++) {
                JSONObject relation = relations.getJSONObject(i);
                String operation = relation.getString("operation");

                if (!StringUtils.hasText(operation)) {
                    log.warn("关系操作类型为空，跳过处理");
                    continue;
                }

                switch (operation.toUpperCase()) {
                    case "INSERT":
                        insertCulturalRelicRelation(relicId, relation, createdBy);
                        break;
                    case "ALTER":
                        alterCulturalRelicRelation(relation, createdBy);
                        break;
                    case "DELETE":
                        deleteCulturalRelicRelation(relation);
                        break;
                    default:
                        log.warn("不支持的关系操作类型: {}", operation);
                }
            }
        } catch (Exception e) {
            log.error("处理文物实体关系失败，文物ID: {}", relicId, e);
            throw new RuntimeException("处理文物实体关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 新增文物实体关系
     */
    private void insertCulturalRelicRelation(Long relicId, JSONObject relation, Long createdBy) {
        try {
            EntityRelation entityRelation = new EntityRelation();
            entityRelation.setRelationName(getStringValue(relation, "relationName"));
            entityRelation.setSourceType("RELIC");
            entityRelation.setSourceId(relicId);
            entityRelation.setTargetString(getStringValue(relation, "targetString"));
            entityRelation.setRelationDescription(getStringValue(relation, "relationDescription"));
            entityRelation.setCreatedBy(Math.toIntExact(createdBy));
            entityRelation.setCreatedAt(LocalDateTime.now());
            entityRelation.setDeleted(false);

            entityRelationMapper.insert(entityRelation);
            log.info("新增文物实体关系成功，文物ID: {}, 关系名称: {}", relicId, entityRelation.getRelationName());
        } catch (Exception e) {
            log.error("新增文物实体关系失败，文物ID: {}", relicId, e);
            throw new RuntimeException("新增文物实体关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 修改文物实体关系
     */
    private void alterCulturalRelicRelation(JSONObject relation, Long createdBy) {
        try {
            Long relationId = getLongValue(relation, "relationId");
            if (relationId == null) {
                throw new RuntimeException("修改关系时，relationId不能为空");
            }

            EntityRelation existingRelation = entityRelationMapper.selectById(relationId);
            if (existingRelation == null) {
                throw new RuntimeException("要修改的关系不存在: " + relationId);
            }

            // 验证关系是否属于文物实体
            if (!"RELIC".equals(existingRelation.getSourceType())) {
                throw new RuntimeException("关系不属于文物实体: " + relationId);
            }

            if (relation.containsKey("relationName")) {
                existingRelation.setRelationName(getStringValue(relation, "relationName"));
            }
            if (relation.containsKey("targetString")) {
                existingRelation.setTargetString(getStringValue(relation, "targetString"));
            }
            if (relation.containsKey("relationDescription")) {
                existingRelation.setRelationDescription(getStringValue(relation, "relationDescription"));
            }

            existingRelation.setUpdatedAt(LocalDateTime.now());
            entityRelationMapper.updateById(existingRelation);
            log.info("修改文物实体关系成功，关系ID: {}", relationId);
        } catch (Exception e) {
            log.error("修改文物实体关系失败", e);
            throw new RuntimeException("修改文物实体关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除文物实体关系
     */
    private void deleteCulturalRelicRelation(JSONObject relation) {
        try {
            Long relationId = getLongValue(relation, "relationId");
            if (relationId == null) {
                throw new RuntimeException("删除关系时，relationId不能为空");
            }

            EntityRelation existingRelation = entityRelationMapper.selectById(relationId);
            if (existingRelation != null) {
                // 验证关系是否属于文物实体
                if (!"RELIC".equals(existingRelation.getSourceType())) {
                    throw new RuntimeException("要删除的关系不属于文物实体: " + relationId);
                }

                existingRelation.setDeleted(true);
                entityRelationMapper.updateById(existingRelation);
                log.info("删除文物实体关系成功，关系ID: {}", relationId);
            } else {
                log.warn("要删除的关系不存在: {}", relationId);
            }
        } catch (Exception e) {
            log.error("删除文物实体关系失败", e);
            throw new RuntimeException("删除文物实体关系失败: " + e.getMessage(), e);
        }
    }

    /**
     * 安全获取字符串值
     */
    private String getStringValue(JSONObject json, String key) {
        if (json == null || !json.containsKey(key)) {
            return null;
        }
        Object value = json.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 安全获取Long值
     */
    private Long getLongValue(JSONObject json, String key) {
        if (json == null || !json.containsKey(key)) {
            return null;
        }
        Object value = json.get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    //创建审核
    @Override
    public void createAudit(String auditData, Long auditTypeId)
    {
            // 2. 获取当前用户ID（从认证信息中获取） TODO
            //Long createdBy = getCurrentUserId();
            Long createdBy = 1L;

            // 3. 创建审核记录
            Audit audit = new Audit();
            audit.setAuditStatus("pending");
            audit.setAuditTypeId(auditTypeId);
            audit.setAuditData(auditData); // 直接存储整个请求体
            audit.setCreatedBy(createdBy);
            audit.setCreatedAt(LocalDateTime.now());
            audit.setUpdatedAt(LocalDateTime.now());

            // 4. 插入数据库
            auditMapper.insert(audit);
    }

    //获取我的文物类型列表
    @Override
    public List<MyRelicTypeVO> getMyRelicTypes(Long userId)
    {
        try {
            log.info("获取用户{}的文物类型", userId);

            // 1. 查询该用户创建的所有文物类型
            QueryWrapper<RelicsType> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("created_by", userId)
                    .eq("is_deleted", 0)  // 只查询未删除的
                    .orderByDesc("created_at");  // 按创建时间倒序

            List<RelicsType> relicsTypes = relicsTypeMapper.selectList(queryWrapper);

            // 2. 转换为VO列表
            List<MyRelicTypeVO> result = relicsTypes.stream()
                    .map(this::convertToVO)
                    .collect(Collectors.toList());

            log.info("获取到{}个文物类型", result.size());
            return result;

        } catch (Exception e) {
            log.error("获取用户文物类型失败，用户ID: {}", userId, e);
            throw new RuntimeException("获取文物类型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将RelicsType实体转换为VO
     */
    private MyRelicTypeVO convertToVO(RelicsType relicsType) {
        MyRelicTypeVO vo = new MyRelicTypeVO();
        vo.setId(Long.valueOf(relicsType.getRelicsTypeId()));
        vo.setTypeName(relicsType.getTypeName());
        vo.setDescription(relicsType.getDescription());
        vo.setCreateTime(relicsType.getCreatedAt());
        vo.setAuditStatus("已通过");  // 固定为"已通过"
        return vo;
    }

    @Override
    public List<MyRelicVO> getMyRelics(Long userId) {
        try {
            log.info("获取用户{}的文物", userId);

            // 1. 查询该用户创建的所有文物
            QueryWrapper<CulturalRelics> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("created_by", userId)
                    .eq("is_deleted", 0)  // 只查询未删除的
                    .orderByDesc("created_at");  // 按创建时间倒序

            List<CulturalRelics> culturalRelics = culturalRelicsMapper.selectList(queryWrapper);

            if (culturalRelics.isEmpty()) {
                return List.of();  // 返回空列表
            }

            // 2. 获取所有相关文物类型ID
            List<Integer> typeIds = culturalRelics.stream()
                    .map(CulturalRelics::getRelicsTypeId)
                    .distinct()
                    .collect(Collectors.toList());

            // 3. 批量查询文物类型名称
            Map<Integer, String> typeNameMap = getTypeNameMap(typeIds);

            // 4. 转换为VO列表
            List<MyRelicVO> result = culturalRelics.stream()
                    .map(relic -> convertToVO(relic, typeNameMap))
                    .collect(Collectors.toList());

            log.info("获取到{}个文物", result.size());
            return result;

        } catch (Exception e) {
            log.error("获取用户文物失败，用户ID: {}", userId, e);
            throw new RuntimeException("获取文物失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量获取文物类型名称
     */
    private Map<Integer, String> getTypeNameMap(List<Integer> typeIds) {
        if (typeIds.isEmpty()) {
            return new HashMap<>();
        }

        QueryWrapper<RelicsType> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("relics_type_id", typeIds)
                .select("relics_type_id", "type_name");

        List<RelicsType> types = relicsTypeMapper.selectList(queryWrapper);

        return types.stream()
                .collect(Collectors.toMap(
                        RelicsType::getRelicsTypeId,
                        RelicsType::getTypeName,
                        (v1, v2) -> v1
                ));
    }

    /**
     * 将CulturalRelics实体转换为VO
     */
    private MyRelicVO convertToVO(CulturalRelics relic, Map<Integer, String> typeNameMap) {
        MyRelicVO vo = new MyRelicVO();
        vo.setId(relic.getRelicsId().longValue());
        vo.setRelicsName(relic.getRelicsName());
        vo.setTypeName(typeNameMap.getOrDefault(relic.getRelicsTypeId(), "未知类型"));
        vo.setEra(relic.getEra());
        vo.setMaterial(relic.getMaterial());
        vo.setCreateTime(relic.getCreatedAt());
        return vo;
    }

    //获取文物类型详情
    @Override
    public RelicTypeDetailVO getRelicTypeDetail(Long relicTypeId) {
        try {
            log.info("查询文物类型详情，类型ID: {}", relicTypeId);

            // 1. 查询文物类型
            RelicsType relicsType = relicsTypeMapper.selectById(relicTypeId);
            if (relicsType == null) {
                throw new RuntimeException("文物类型不存在");
            }

            if (relicsType.getIsDeleted() == true) {
                throw new RuntimeException("文物类型已被删除");
            }

            // 2. 构建返回VO
            RelicTypeDetailVO vo = new RelicTypeDetailVO();
            vo.setRelicTypeId(Long.valueOf(relicsType.getRelicsTypeId()));
            vo.setTypeName(relicsType.getTypeName());
            vo.setDescription(relicsType.getDescription());

            // 3. 解析type_fields字段
            String typeFields = relicsType.getTypeFields();
            if (StringUtils.hasText(typeFields)) {
                vo.setAttributes(parseAttributes(typeFields));
            } else {
                vo.setAttributes(new ArrayList<>());
            }

            log.info("文物类型详情查询成功，类型ID: {}", relicTypeId);
            return vo;

        } catch (Exception e) {
            log.error("查询文物类型详情失败，类型ID: {}", relicTypeId, e);
            throw new RuntimeException("查询文物类型详情失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析属性字段 - 专门处理当前数据库中的转义格式
     */
    private List<AttributeVO> parseAttributes(String typeFields) {
        try {
            log.info("开始解析属性字段: {}", typeFields);

            if (!StringUtils.hasText(typeFields)) {
                return new ArrayList<>();
            }

            List<AttributeVO> attributes = new ArrayList<>();

            // 情况1：如果是双重转义的JSON字符串（当前数据库中的格式）
            if (typeFields.startsWith("\"[") && typeFields.endsWith("]\"") && typeFields.contains("\\\"")) {
                log.info("检测到双重转义JSON格式，进行特殊处理");
                attributes = parseDoubleEscapedJson(typeFields);
            }
            // 情况2：如果是正常的JSON数组格式
            else if (typeFields.startsWith("[") && typeFields.endsWith("]")) {
                log.info("检测到标准JSON数组格式");
                attributes = parseStandardJsonArray(typeFields);
            }
            // 情况3：其他格式尝试解析
            else {
                log.info("尝试解析其他格式");
                attributes = tryParseOtherFormats(typeFields);
            }

            log.info("解析完成，共解析出{}个属性", attributes.size());
            return attributes;

        } catch (Exception e) {
            log.error("解析属性字段失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 解析双重转义的JSON字符串（当前数据库中的格式）
     * 格式示例: "[{\"name\":\"铭文\",\"type\":\"text\",...}]"
     */
    private List<AttributeVO> parseDoubleEscapedJson(String doubleEscapedJson) {
        try {
            log.info("处理双重转义JSON: {}", doubleEscapedJson);

            // 1. 先去除最外层的引号
            String unquoted = doubleEscapedJson.substring(1, doubleEscapedJson.length() - 1);
            log.info("去除外层引号后: {}", unquoted);

            // 2. 处理转义字符
            String unescaped = unquoted.replace("\\\"", "\"");
            log.info("处理转义字符后: {}", unescaped);

            // 3. 解析为JSON数组
            return parseStandardJsonArray(unescaped);

        } catch (Exception e) {
            log.error("解析双重转义JSON失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 解析标准JSON数组格式
     */
    private List<AttributeVO> parseStandardJsonArray(String jsonArray) {
        try {
            JSONArray attributesArray = JSON.parseArray(jsonArray);
            List<AttributeVO> attributes = new ArrayList<>();

            for (int i = 0; i < attributesArray.size(); i++) {
                JSONObject attrObj = attributesArray.getJSONObject(i);
                AttributeVO attribute = new AttributeVO();

                attribute.setName(attrObj.getString("name"));
                attribute.setType(attrObj.getString("type"));
                attribute.setDescription(attrObj.getString("description"));

                // 处理required字段
                Object requiredObj = attrObj.get("required");
                if (requiredObj instanceof Boolean) {
                    attribute.setRequired((Boolean) requiredObj);
                } else if (requiredObj instanceof String) {
                    attribute.setRequired(Boolean.parseBoolean((String) requiredObj));
                } else if (requiredObj instanceof Number) {
                    attribute.setRequired(((Number) requiredObj).intValue() == 1);
                } else {
                    attribute.setRequired(false);
                }

                attributes.add(attribute);
                log.debug("解析属性: name={}, type={}, required={}",
                        attribute.getName(), attribute.getType(), attribute.getRequired());
            }

            return attributes;

        } catch (Exception e) {
            log.error("解析标准JSON数组失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 尝试解析其他格式
     */
    private List<AttributeVO> tryParseOtherFormats(String typeFields) {
        List<AttributeVO> attributes = new ArrayList<>();

        try {
            // 尝试1：直接解析
            JSONArray array = JSON.parseArray(typeFields);
            if (array != null && !array.isEmpty()) {
                return parseStandardJsonArray(typeFields);
            }
        } catch (Exception e1) {
            log.info("直接解析失败: {}", e1.getMessage());
        }

        try {
            // 尝试2：可能包含额外的转义
            String cleaned = typeFields.replace("\\\"", "\"");
            JSONArray array = JSON.parseArray(cleaned);
            if (array != null && !array.isEmpty()) {
                return parseStandardJsonArray(cleaned);
            }
        } catch (Exception e2) {
            log.info("清理后解析失败: {}", e2.getMessage());
        }

        log.warn("无法解析的属性格式: {}", typeFields);
        return attributes;
    }
}
