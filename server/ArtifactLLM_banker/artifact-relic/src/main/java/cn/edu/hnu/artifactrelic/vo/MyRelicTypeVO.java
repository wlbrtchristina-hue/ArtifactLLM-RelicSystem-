package cn.edu.hnu.artifactrelic.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyRelicTypeVO {
    private Long id;               // 文物类型ID
    private String typeName;       // 类型名称
    private String description;    // 类型描述
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;  // 创建时间
    private String auditStatus;    // 审核状态（固定为"已通过"）
}