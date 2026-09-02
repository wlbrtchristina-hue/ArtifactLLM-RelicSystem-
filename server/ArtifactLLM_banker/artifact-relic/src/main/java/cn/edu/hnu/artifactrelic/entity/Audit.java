// Audit.java
package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;


@Data
@TableName("audit")
public class Audit {
    @TableId(value = "audit_id", type = IdType.AUTO)
    private Long auditId;

    @TableField("audit_status")
    private String auditStatus; // pending, approved, rejected

    @TableField("audit_type_id")
    private Long auditTypeId;

    @TableField("audit_data")
    private String auditData; // JSON格式字符串

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @TableField("auditor_id")
    private Long auditorId;

    @TableField("audit_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime auditTime;

    @TableField("reject_reason")
    private String rejectReason;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}