package cn.edu.hnu.artifactsystem.controller;

import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactsystem.dto.FeedbackDTO;
import cn.edu.hnu.artifactsystem.dto.FeedbackQueryDTO;
import cn.edu.hnu.artifactsystem.service.IFeedbackService;
import cn.edu.hnu.artifactsystem.vo.FeedbackVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 反馈控制器
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Resource
    private IFeedbackService feedbackService;

    /**
     * 提交反馈
     */
    @PostMapping("/submit")
    public Result<Long> submitFeedback(@RequestBody FeedbackDTO feedbackDTO) {
        Long feedbackId = feedbackService.submitFeedback(feedbackDTO);
        return Result.success(feedbackId);
    }

    /**
     * 分页查询反馈列表
     */
    @GetMapping("/list")
    public Result<IPage<FeedbackVO>> getFeedbackList(FeedbackQueryDTO queryDTO) {
        IPage<FeedbackVO> page = feedbackService.getFeedbackPage(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据ID获取反馈详情
     */
    @GetMapping("/{id}")
    public Result<FeedbackVO> getFeedbackById(@PathVariable Long id) {
        FeedbackVO feedback = feedbackService.getFeedbackById(id);
        return Result.success(feedback);
    }

    /**
     * 处理反馈
     */
    @PutMapping("/process/{id}")
    public Result<Boolean> processFeedback(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String processResult,
            @RequestParam Long processedBy) {
        boolean success = feedbackService.processFeedback(id, status, processResult, processedBy);
        return Result.success(success);
    }

    /**
     * 批量更新反馈状态
     */
    @PutMapping("/batch/status")
    public Result<Integer> batchUpdateStatus(
            @RequestBody List<Long> ids,
            @RequestParam Integer status,
            @RequestParam Long processedBy) {
        int count = feedbackService.batchUpdateStatus(ids, status, processedBy);
        return Result.success(count);
    }

    /**
     * 获取反馈统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getFeedbackStatistics() {
        Map<String, Object> statistics = feedbackService.getFeedbackStatistics();
        return Result.success(statistics);
    }

    /**
     * 删除反馈
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteFeedback(@PathVariable Long id) {
        boolean success = feedbackService.deleteFeedback(id);
        return Result.success(success);
    }

    /**
     * 获取用户的反馈列表
     */
    @GetMapping("/user/{userId}")
    public Result<IPage<FeedbackVO>> getFeedbackByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<FeedbackVO> page = feedbackService.getFeedbackByUserId(userId, pageNum, pageSize);
        return Result.success(page);
    }
}