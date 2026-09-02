package cn.edu.hnu.artifactsystem.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈视图对象
 */
@Data
public class FeedbackVO {
    /**
     * 反馈ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 反馈类型：0-建议，1-问题，2-功能需求
     */
    private Integer feedbackType;

    /**
     * 反馈类型名称
     */
    private String feedbackTypeName;

    /**
     * 反馈标题
     */
    private String feedbackTitle;

    /**
     * 反馈内容
     */
    private String feedbackContent;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 提交时间
     */
    private LocalDateTime submittedAt;

    /**
     * 状态：0-待处理，1-处理中，2-已解决，3-已关闭
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 处理人ID
     */
    private Long processedBy;

    /**
     * 处理人姓名
     */
    private String processedByName;

    /**
     * 处理时间
     */
    private LocalDateTime processedAt;

    /**
     * 处理结果
     */
    private String processResult;

    /**
     * 是否已评价
     */
    private Boolean evaluated;
}