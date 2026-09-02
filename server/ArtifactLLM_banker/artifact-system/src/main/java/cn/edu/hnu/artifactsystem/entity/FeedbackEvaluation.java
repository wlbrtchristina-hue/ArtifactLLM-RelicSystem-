package cn.edu.hnu.artifactsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 反馈评价实体类
 */
@Data
@TableName("feedback_evaluation")
public class FeedbackEvaluation {
    /**
     * 评价ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 评分：1-5分
     */
    private Integer rating;

    /**
     * 设置评分，验证评分范围
     * @param rating 评分值
     * @throws IllegalArgumentException 当评分不在1-5范围内时抛出异常
     */
    public void setRating(Integer rating) {
        if (rating != null && (rating < 1 || rating > 5)) {
            throw new IllegalArgumentException("评分必须在1-5之间");
        }
        this.rating = rating;
    }

    /**
     * 评价内容
     */
    private String comment;

    /**
     * 评价时间
     */
    private LocalDateTime evaluationTime;
}