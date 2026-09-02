package cn.edu.hnu.artifactsystem.controller;

import cn.edu.hnu.artifactsystem.entity.FeedbackEvaluation;
import cn.edu.hnu.artifactsystem.service.IFeedbackEvaluationService;
import cn.edu.hnu.artifactcommon.result.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 反馈评价控制器
 */
@RestController
@RequestMapping("/api/feedback-evaluation")
public class FeedbackEvaluationController {

    @Resource
    private IFeedbackEvaluationService feedbackEvaluationService;

    /**
     * 提交反馈评价
     */
    @PostMapping("/submit")
    public Result<Boolean> submitEvaluation(@RequestParam Long feedbackId,
                                          @RequestParam Long userId,
                                          @RequestParam Integer rating,
                                          @RequestParam(required = false) String comment) {
        try {
            boolean result = feedbackEvaluationService.submitEvaluation(feedbackId, userId, rating, comment);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据反馈ID获取评价列表
     */
    @GetMapping("/feedback/{feedbackId}")
    public Result<List<FeedbackEvaluation>> getEvaluationsByFeedbackId(@PathVariable Long feedbackId) {
        try {
            List<FeedbackEvaluation> evaluations = feedbackEvaluationService.getEvaluationsByFeedbackId(feedbackId);
            return Result.success(evaluations);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 根据用户ID获取评价列表（分页）
     */
    @GetMapping("/user/{userId}")
    public Result<Page<FeedbackEvaluation>> getEvaluationsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<FeedbackEvaluation> page = new Page<>(current, size);
            Page<FeedbackEvaluation> result = feedbackEvaluationService.getEvaluationsByUserId(userId, page);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查用户是否已评价该反馈
     */
    @GetMapping("/check")
    public Result<Boolean> hasUserEvaluated(@RequestParam Long feedbackId, @RequestParam Long userId) {
        try {
            boolean hasEvaluated = feedbackEvaluationService.hasUserEvaluated(feedbackId, userId);
            return Result.success(hasEvaluated);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取反馈的平均评分
     */
    @GetMapping("/average-rating/{feedbackId}")
    public Result<Double> getAverageRating(@PathVariable Long feedbackId) {
        try {
            Double averageRating = feedbackEvaluationService.getAverageRating(feedbackId);
            return Result.success(averageRating);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取反馈的评分统计
     */
    @GetMapping("/rating-statistics/{feedbackId}")
    public Result<Map<Integer, Long>> getRatingStatistics(@PathVariable Long feedbackId) {
        try {
            Map<Integer, Long> statistics = feedbackEvaluationService.getRatingStatistics(feedbackId);
            return Result.success(statistics);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{evaluationId}")
    public Result<Boolean> deleteEvaluation(@PathVariable Long evaluationId, @RequestParam Long userId) {
        try {
            boolean result = feedbackEvaluationService.deleteEvaluation(evaluationId, userId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新评价
     */
    @PutMapping("/{evaluationId}")
    public Result<Boolean> updateEvaluation(
            @PathVariable Long evaluationId,
            @RequestParam Long userId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        try {
            boolean result = feedbackEvaluationService.updateEvaluation(evaluationId, userId, rating, comment);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}