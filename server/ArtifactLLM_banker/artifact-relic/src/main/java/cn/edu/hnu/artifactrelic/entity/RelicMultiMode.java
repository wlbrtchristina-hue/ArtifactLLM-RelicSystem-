package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

// RelicMultiMode.java
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("relic_multi_mode")
public class RelicMultiMode {
    @TableId(value = "resource_id", type = IdType.AUTO)
    private Integer resourceId;

    @TableField("relics_id")
    private Integer relicsId;

    @TableField("resource_type")
    private String resourceType;

    @TableField("resource_content")
    private String resourceContent;

    @TableField("created_by")
    private Integer createdBy;

    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}