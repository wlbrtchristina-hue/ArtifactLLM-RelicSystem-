package cn.edu.hnu.artifactrelic.service;

import cn.edu.hnu.artifactrelic.dto.EntityInstanceDTO;
import cn.edu.hnu.artifactrelic.dto.ModelDefDTO;
import cn.edu.hnu.artifactrelic.entity.InstanceData;
import cn.edu.hnu.artifactrelic.entity.MetaModel;

import java.util.List;

public interface IModelingService {
    Long saveModelDef(ModelDefDTO dto, Long userId);
    ModelDefDTO getModelDef(Long modelId);
    List<MetaModel> listModels(Long userId);
    Long saveInstance(EntityInstanceDTO dto, Long userId);
    List<InstanceData> listInstances(Long modelId, Long entityDefId);
}
