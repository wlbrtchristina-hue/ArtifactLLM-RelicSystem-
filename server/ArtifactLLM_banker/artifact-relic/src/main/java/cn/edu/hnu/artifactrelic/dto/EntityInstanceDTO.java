package cn.edu.hnu.artifactrelic.dto;

import lombok.Data;
import java.util.Map;

@Data
public class EntityInstanceDTO {
    private Long id;
    private Long modelId;
    private Long entityDefId;
    private String name;
    private Map<String, Object> data;
}
