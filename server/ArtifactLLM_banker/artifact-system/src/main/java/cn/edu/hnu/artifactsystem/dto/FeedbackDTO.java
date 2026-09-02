package cn.edu.hnu.artifactsystem.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 反馈数据传输对象
 */
@Data
public class FeedbackDTO {
    /**
     * 反馈ID
     */
    private Long id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 反馈类型：0-建议，1-问题，2-功能需求
     */
    @NotNull(message = "反馈类型不能为空")
    private Integer feedbackType;

    /**
     * 反馈标题
     */
    @NotBlank(message = "反馈标题不能为空")
    private String feedbackTitle;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    private String feedbackContent;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 状态：0-待处理，1-处理中，2-已解决，3-已关闭
     */
    private Integer status;

    /**
     * 处理人ID
     */
    private Long processedBy;

    /**
     * 处理结果
     */
    private String processResult;
}