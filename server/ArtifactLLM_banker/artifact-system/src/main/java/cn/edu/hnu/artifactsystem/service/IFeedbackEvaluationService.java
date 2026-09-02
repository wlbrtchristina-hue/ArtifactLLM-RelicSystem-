package cn.edu.hnu.artifactsystem.service;

import cn.edu.hnu.artifactsystem.entity.FeedbackEvaluation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 反馈评价服务接口
 */
public interface IFeedbackEvaluationService extends IService<FeedbackEvaluation> {

    /**
     * 提交反馈评价
     *
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @param rating 评分(1-5)
     * @param comment 评价内容
     * @return 是否提交成功
     */
    boolean submitEvaluation(Long feedbackId, Long userId, Integer rating, String comment);

    /**
     * 根据反馈ID获取评价列表
     *
     * @param feedbackId 反馈ID
     * @return 评价列表
     */
    List<FeedbackEvaluation> getEvaluationsByFeedbackId(Long feedbackId);

    /**
     * 根据用户ID获取评价列表
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 评价分页列表
     */
    Page<FeedbackEvaluation> getEvaluationsByUserId(Long userId, Page<FeedbackEvaluation> page);

    /**
     * 检查用户是否已评价该反馈
     *
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @return 是否已评价
     */
    boolean hasUserEvaluated(Long feedbackId, Long userId);

    /**
     * 获取反馈的平均评分
     *
     * @param feedbackId 反馈ID
     * @return 平均评分
     */
    Double getAverageRating(Long feedbackId);

    /**
     * 获取反馈的评分统计
     *
     * @param feedbackId 反馈ID
     * @return 评分统计(1-5星的数量)
     */
    Map<Integer, Long> getRatingStatistics(Long feedbackId);

    /**
     * 删除评价
     *
     * @param evaluationId 评价ID
     * @param userId 用户ID(用于权限验证)
     * @return 是否删除成功
     */
    boolean deleteEvaluation(Long evaluationId, Long userId);

    /**
     * 更新评价
     *
     * @param evaluationId 评价ID
     * @param userId 用户ID(用于权限验证)
     * @param rating 新评分
     * @param comment 新评价内容
     * @return 是否更新成功
     */
    boolean updateEvaluation(Long evaluationId, Long userId, Integer rating, String comment);
}