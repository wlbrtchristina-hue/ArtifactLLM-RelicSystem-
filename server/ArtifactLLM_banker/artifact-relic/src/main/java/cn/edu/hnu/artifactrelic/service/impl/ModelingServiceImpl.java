package cn.edu.hnu.artifactrelic.service.impl;

import cn.edu.hnu.artifactrelic.dto.EntityInstanceDTO;
import cn.edu.hnu.artifactrelic.dto.ModelDefDTO;
import cn.edu.hnu.artifactrelic.entity.*;
import cn.edu.hnu.artifactrelic.mapper.*;
import cn.edu.hnu.artifactrelic.service.IModelingService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ModelingServiceImpl implements IModelingService {

    @Autowired
    private MetaModelMapper metaModelMapper;
    @Autowired
    private MetaEntityDefMapper metaEntityDefMapper;
    @Autowired
    private MetaAttrDefMapper metaAttrDefMapper;
    @Autowired
    private MetaRelationDefMapper metaRelationDefMapper;
    @Autowired
    private InstanceDataMapper instanceDataMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveModelDef(ModelDefDTO dto, Long userId) {
        if (dto == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("模型名称不能为空");
        }
        MetaModel model = new MetaModel();
        model.setName(dto.getName());
        model.setDescription(dto.getDescription());
        model.setCreatorId(userId);
        model.setStatus(1); // Auto publish for now
        metaModelMapper.insert(model);

        Map<String, Long> entityIdMap = new HashMap<>(); // Frontend UUID -> DB ID
        Set<String> entityNameSet = new HashSet<>();

        if (dto.getEntities() != null) {
            for (ModelDefDTO.EntityDefDTO entityDto : dto.getEntities()) {
                if (entityDto.getName() == null || entityDto.getName().trim().isEmpty()) {
                    throw new IllegalArgumentException("实体名称不能为空");
                }
                if (!entityNameSet.add(entityDto.getName().trim())) {
                    throw new IllegalArgumentException("实体名称重复: " + entityDto.getName());
                }
                MetaEntityDef entity = new MetaEntityDef();
                entity.setModelId(model.getId());
                entity.setName(entityDto.getName());
                entity.setCode(entityDto.getCode());
                entity.setDescription(entityDto.getDescription());
                entity.setXPos(entityDto.getX());
                entity.setYPos(entityDto.getY());
                metaEntityDefMapper.insert(entity);
                entityIdMap.put(entityDto.getId(), entity.getId());

                Set<String> attrNameSet = new HashSet<>();
                if (entityDto.getAttributes() != null) {
                    for (ModelDefDTO.AttrDefDTO attrDto : entityDto.getAttributes()) {
                        if (attrDto.getName() == null || attrDto.getName().trim().isEmpty()) {
                            throw new IllegalArgumentException("属性名称不能为空");
                        }
                        String attrName = attrDto.getName().trim();
                        if (!attrNameSet.add(attrName)) {
                            throw new IllegalArgumentException("属性名称重复: " + attrName);
                        }
                        MetaAttrDef attr = new MetaAttrDef();
                        attr.setEntityDefId(entity.getId());
                        attr.setName(attrDto.getName());
                        attr.setCode(attrDto.getCode());
                        attr.setType(attrDto.getType());
                        attr.setRequired(attrDto.getRequired());
                        attr.setDescription(attrDto.getDescription());
                        attr.setOptions(attrDto.getOptions());
                        metaAttrDefMapper.insert(attr);
                    }
                }
            }
        }

        Set<String> relSet = new HashSet<>();
        if (dto.getRelations() != null) {
            for (ModelDefDTO.RelationDefDTO relDto : dto.getRelations()) {
                if (relDto.getName() == null || relDto.getName().trim().isEmpty()) {
                    throw new IllegalArgumentException("关系名称不能为空");
                }
                MetaRelationDef rel = new MetaRelationDef();
                rel.setModelId(model.getId());
                rel.setName(relDto.getName());
                rel.setType(relDto.getType());
                rel.setDescription(relDto.getDescription());
                
                Long sourceId = entityIdMap.get(relDto.getSourceId());
                Long targetId = entityIdMap.get(relDto.getTargetId());
                
                if (sourceId != null && targetId != null) {
                    if (sourceId.equals(targetId)) {
                        throw new IllegalArgumentException("不能连接实体到自身");
                    }
                    String relKey = sourceId + "|" + targetId + "|" + (rel.getType() == null ? "" : rel.getType());
                    if (!relSet.add(relKey)) {
                        throw new IllegalArgumentException("重复关系: " + rel.getName());
                    }
                    rel.setSourceEntityId(sourceId);
                    rel.setTargetEntityId(targetId);
                    metaRelationDefMapper.insert(rel);
                }
            }
        }

        return model.getId();
    }

    @Override
    public ModelDefDTO getModelDef(Long modelId) {
        MetaModel model = metaModelMapper.selectById(modelId);
        if (model == null) return null;

        ModelDefDTO dto = new ModelDefDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());

        // Fetch Entities
        List<MetaEntityDef> entities = metaEntityDefMapper.selectList(new QueryWrapper<MetaEntityDef>().eq("model_id", modelId));
        dto.setEntities(entities.stream().map(e -> {
            ModelDefDTO.EntityDefDTO eDto = new ModelDefDTO.EntityDefDTO();
            eDto.setId(e.getId().toString());
            eDto.setName(e.getName());
            eDto.setCode(e.getCode());
            eDto.setDescription(e.getDescription());
            eDto.setX(e.getXPos());
            eDto.setY(e.getYPos());
            
            // Fetch Attributes
            List<MetaAttrDef> attrs = metaAttrDefMapper.selectList(new QueryWrapper<MetaAttrDef>().eq("entity_def_id", e.getId()));
            eDto.setAttributes(attrs.stream().map(a -> {
                ModelDefDTO.AttrDefDTO aDto = new ModelDefDTO.AttrDefDTO();
                aDto.setName(a.getName());
                aDto.setCode(a.getCode());
                aDto.setType(a.getType());
                aDto.setRequired(a.getRequired());
                aDto.setDescription(a.getDescription());
                aDto.setOptions(a.getOptions());
                return aDto;
            }).collect(Collectors.toList()));
            return eDto;
        }).collect(Collectors.toList()));

        // Fetch Relations
        List<MetaRelationDef> relations = metaRelationDefMapper.selectList(new QueryWrapper<MetaRelationDef>().eq("model_id", modelId));
        dto.setRelations(relations.stream().map(r -> {
            ModelDefDTO.RelationDefDTO rDto = new ModelDefDTO.RelationDefDTO();
            rDto.setName(r.getName());
            rDto.setType(r.getType());
            rDto.setSourceId(r.getSourceEntityId().toString());
            rDto.setTargetId(r.getTargetEntityId().toString());
            rDto.setDescription(r.getDescription());
            return rDto;
        }).collect(Collectors.toList()));

        return dto;
    }

    @Override
    public List<MetaModel> listModels(Long userId) {
        return metaModelMapper.selectList(new QueryWrapper<MetaModel>().orderByDesc("create_time"));
    }

    @Override
    public Long saveInstance(EntityInstanceDTO dto, Long userId) {
        InstanceData data = new InstanceData();
        data.setId(dto.getId());
        data.setModelId(dto.getModelId());
        data.setEntityDefId(dto.getEntityDefId());
        data.setName(dto.getName());
        data.setDataJson(JSON.toJSONString(dto.getData()));
        data.setCreatorId(userId);
        
        if (data.getId() == null) {
            instanceDataMapper.insert(data);
        } else {
            instanceDataMapper.updateById(data);
        }
        return data.getId();
    }

    @Override
    public List<InstanceData> listInstances(Long modelId, Long entityDefId) {
        QueryWrapper<InstanceData> query = new QueryWrapper<>();
        if (modelId != null) query.eq("model_id", modelId);
        if (entityDefId != null) query.eq("entity_def_id", entityDefId);
        query.orderByDesc("create_time");
        return instanceDataMapper.selectList(query);
    }
}
