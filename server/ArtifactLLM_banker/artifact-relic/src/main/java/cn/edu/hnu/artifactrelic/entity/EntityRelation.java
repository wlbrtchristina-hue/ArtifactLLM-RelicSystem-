package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("entity_relations")
public class EntityRelation {
    @TableId(value = "relation_id", type = IdType.AUTO)
    private Long relationId;

    @TableField("relation_name")
    private String relationName;  // 如: 出土于, 属于, 同时代

    @TableField("source_type")
    private String sourceType;    // RELIC(文物) 或 TYPE(类型)

    @TableField("source_id")
    private Long sourceId;        // 文物ID或类型ID

    @TableField("target_string")
    private String targetString;  // 目标字符串

    @TableField("relation_description")
    private String relationDescription;

    @TableField("created_by")
    private Integer createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField(value = "is_deleted")
    private Boolean deleted = false;

    // 源实体类型枚举
    public enum SourceType {
        RELIC("RELIC", "文物"),
        TYPE("TYPE", "类型");

        final String code;
        private final String desc;

        SourceType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public String getCode() { return code; }
        public String getDesc() { return desc; }
    }
}