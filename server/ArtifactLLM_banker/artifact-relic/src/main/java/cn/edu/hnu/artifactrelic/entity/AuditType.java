package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("audit_type")
public class AuditType {

    @TableId(value = "audit_type_id", type = IdType.AUTO)
    private Long auditTypeId;

    @TableField("audit_type_name")
    String auditTypeName;

    @TableField("audit_content_fields")
    private String auditContentFields;

}