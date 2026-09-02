package cn.edu.hnu.artifactsystem.service.impl;

import cn.edu.hnu.artifactsystem.entity.FeedbackEvaluation;
import cn.edu.hnu.artifactsystem.mapper.FeedbackEvaluationMapper;
import cn.edu.hnu.artifactsystem.service.IFeedbackEvaluationService;
import cn.edu.hnu.artifactsystem.service.IFeedbackService;
import cn.edu.hnu.artifactsystem.service.FeedbackNotificationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反馈评价服务实现类
 */
@Service
public class FeedbackEvaluationServiceImpl extends ServiceImpl<FeedbackEvaluationMapper, FeedbackEvaluation> implements IFeedbackEvaluationService {

    @Resource
    private FeedbackEvaluationMapper feedbackEvaluationMapper;

    @Resource
    private IFeedbackService feedbackService;

    @Resource
    private FeedbackNotificationService notificationService;

    @Override
    @Transactional
    public boolean submitEvaluation(Long feedbackId, Long userId, Integer rating, String comment) {
        // 检查用户是否已评价
        if (hasUserEvaluated(feedbackId, userId)) {
            throw new RuntimeException("您已经对该反馈进行了评价");
        }

        // 检查反馈是否存在且已处理
        var feedback = feedbackService.getById(feedbackId);
        if (feedback == null) {
            throw new RuntimeException("反馈不存在");
        }

        if (!"已解决".equals(feedback.getStatus()) && !"已关闭".equals(feedback.getStatus())) {
            throw new RuntimeException("只能对已处理完成的反馈进行评价");
        }

        // 创建评价
        FeedbackEvaluation evaluation = new FeedbackEvaluation();
        evaluation.setFeedbackId(feedbackId);
        evaluation.setUserId(userId);
        evaluation.setRating(rating);
        evaluation.setComment(comment);
        evaluation.setEvaluationTime(LocalDateTime.now());

        boolean result = save(evaluation);

        if (result) {
            // 发送评价通知
            notificationService.sendFeedbackEvaluationNotification(
                feedbackId, rating, comment, feedback.getProcessedBy()
            );
        }

        return result;
    }

    @Override
    public List<FeedbackEvaluation> getEvaluationsByFeedbackId(Long feedbackId) {
        QueryWrapper<FeedbackEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("feedback_id", feedbackId)
                   .orderByDesc("created_at");
        return list(queryWrapper);
    }

    @Override
    public Page<FeedbackEvaluation> getEvaluationsByUserId(Long userId, Page<FeedbackEvaluation> page) {
        QueryWrapper<FeedbackEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("created_at");
        return page(page, queryWrapper);
    }

    @Override
    public boolean hasUserEvaluated(Long feedbackId, Long userId) {
        QueryWrapper<FeedbackEvaluation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("feedback_id", feedbackId)
                   .eq("user_id", userId);
        return count(queryWrapper) > 0;
    }

    @Override
    public Double getAverageRating(Long feedbackId) {
        return feedbackEvaluationMapper.avgRatingByFeedbackId(feedbackId);
    }

    @Override
    public Map<Integer, Long> getRatingStatistics(Long feedbackId) {
        List<Map<String, Object>> ratingStats = feedbackEvaluationMapper.getRatingStatistics(feedbackId);
        
        Map<Integer, Long> result = new HashMap<>();
        // 初始化1-5星的统计
        for (int i = 1; i <= 5; i++) {
            result.put(i, 0L);
        }
        
        // 填充实际统计数据
        for (Map<String, Object> stat : ratingStats) {
            Integer rating = (Integer) stat.get("rating");
            Long count = (Long) stat.get("count");
            result.put(rating, count);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean deleteEvaluation(Long evaluationId, Long userId) {
        FeedbackEvaluation evaluation = getById(evaluationId);
        if (evaluation == null) {
            throw new RuntimeException("评价不存在");
        }
        
        if (!evaluation.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此评价");
        }
        
        return removeById(evaluationId);
    }

    @Override
    @Transactional
    public boolean updateEvaluation(Long evaluationId, Long userId, Integer rating, String comment) {
        FeedbackEvaluation evaluation = getById(evaluationId);
        if (evaluation == null) {
            throw new RuntimeException("评价不存在");
        }
        
        if (!evaluation.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此评价");
        }
        
        evaluation.setRating(rating);
        evaluation.setComment(comment);
        evaluation.setEvaluationTime(LocalDateTime.now());
        
        return updateById(evaluation);
    }
}