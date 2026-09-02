package cn.edu.hnu.artifactrelic.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.el.parser.BooleanNode;

import java.time.LocalDateTime;

// RelicsType.java
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("relics_type")
public class RelicsType {
    @TableId(value = "relics_type_id", type = IdType.AUTO)
    private Integer relicsTypeId;

    @TableField("type_name")
    private String typeName;

    private String description;

    @TableField("created_by")
    private Integer createdBy;

    @TableField(value = "type_fields",typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String typeFields;

    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}