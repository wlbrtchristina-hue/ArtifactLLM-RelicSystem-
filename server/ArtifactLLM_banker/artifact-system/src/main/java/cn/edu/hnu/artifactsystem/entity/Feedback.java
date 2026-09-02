package cn.edu.hnu.artifactsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈实体类
 */
@Data
@TableName("feedback")
public class Feedback {
    /**
     * 反馈ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 反馈类型：0-建议，1-问题，2-功能需求
     */
    private Integer feedbackType;

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
     * 处理人ID
     */
    private Long processedBy;

    /**
     * 处理时间
     */
    private LocalDateTime processedAt;

    /**
     * 处理结果
     */
    private String processResult;
}