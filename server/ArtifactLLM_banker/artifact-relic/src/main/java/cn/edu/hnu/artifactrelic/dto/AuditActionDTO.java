package cn.edu.hnu.artifactrelic.dto;

import lombok.Data;

@Data
public class AuditActionDTO {
    private Long auditorId;      // 审核人ID
    private String comment;      // 审核意见（可选）
    private String rejectReason; // 拒绝原因（仅拒绝时使用）
}