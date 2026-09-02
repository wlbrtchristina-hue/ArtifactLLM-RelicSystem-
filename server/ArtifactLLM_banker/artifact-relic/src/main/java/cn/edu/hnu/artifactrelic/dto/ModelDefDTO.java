package cn.edu.hnu.artifactrelic.dto;

import lombok.Data;
import java.util.List;

@Data
public class ModelDefDTO {
    private Long id;
    private String name;
    private String description;
    private List<EntityDefDTO> entities;
    private List<RelationDefDTO> relations;

    @Data
    public static class EntityDefDTO {
        private String id; // Frontend UUID
        private String name;
        private String code;
        private String description;
        private Integer x;
        private Integer y;
        private List<AttrDefDTO> attributes;
    }

    @Data
    public static class AttrDefDTO {
        private String name;
        private String code;
        private String type;
        private Boolean required;
        private String description;
        private String options;
    }

    @Data
    public static class RelationDefDTO {
        private String name;
        private String type;
        private String sourceId;
        private String targetId;
        private String description;
    }
}
