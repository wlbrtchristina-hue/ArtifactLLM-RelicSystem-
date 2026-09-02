package cn.edu.hnu.artifactrelic.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

// CulturalRelics.java
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cultural_relics")
public class CulturalRelics {
    @TableId(value = "relics_id", type = IdType.AUTO)
    private Integer relicsId;

    private String relicsName;
    private String era;
    private String material;

    @TableField("relics_type_id")
    private Integer relicsTypeId;

    @TableField("discovery_site")
    private String discoverySite;
    private String description;
    @TableField("current_location")
    private String currentLocation;

    @TableField("created_by")
    private Integer createdBy;

    @TableField(value = "custom_fields",typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private String customFields;

    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    // 关联的文物类型信息
    @TableField(exist = false)
    private RelicsType relicsType;

    // 图片资源
    @TableField(exist = false)
    private List<String> images;
}