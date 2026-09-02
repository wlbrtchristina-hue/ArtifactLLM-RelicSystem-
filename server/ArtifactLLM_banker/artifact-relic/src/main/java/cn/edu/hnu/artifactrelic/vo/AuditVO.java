package cn.edu.hnu.artifactrelic.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditVO {
    private Long auditId;
    private String auditStatus;
    private String auditTypeName;
    private String entityName;

    // 创建者信息
    private String createdByName;
    private Long createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // 审核人信息
    private String auditorName;
    private Long auditorId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;

    // 拒绝原因
    private String rejectReason;

    // 新增：完整的审核数据JSON
    private Object auditData;

    // 内部使用字段，不返回给前端
    @com.fasterxml.jackson.annotation.JsonIgnore
    private String auditDataJson;  // 原始JSON字符串
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Long auditTypeId;
}