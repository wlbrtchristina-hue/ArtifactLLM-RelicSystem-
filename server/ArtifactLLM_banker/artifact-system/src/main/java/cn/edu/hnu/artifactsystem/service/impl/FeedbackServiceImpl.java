package cn.edu.hnu.artifactsystem.service.impl;

import cn.edu.hnu.artifactsystem.dto.FeedbackDTO;
import cn.edu.hnu.artifactsystem.dto.FeedbackQueryDTO;
import cn.edu.hnu.artifactsystem.entity.Feedback;
import cn.edu.hnu.artifactsystem.mapper.FeedbackMapper;
import cn.edu.hnu.artifactsystem.service.IFeedbackService;
import cn.edu.hnu.artifactsystem.vo.FeedbackVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反馈服务实现类
 */
@Service
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements IFeedbackService {

    @Resource
    private FeedbackMapper feedbackMapper;

    @Override
    public Long submitFeedback(FeedbackDTO feedbackDTO) {
        Feedback feedback = new Feedback();
        BeanUtils.copyProperties(feedbackDTO, feedback);
        
        // 设置初始状态为待处理
        feedback.setStatus(0);
        feedback.setSubmittedAt(LocalDateTime.now());
        
        feedbackMapper.insert(feedback);
        return feedback.getId();
    }

    @Override
    public IPage<FeedbackVO> getFeedbackPage(FeedbackQueryDTO queryDTO) {
        Page<FeedbackVO> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return feedbackMapper.selectFeedbackPage(
                page,
                queryDTO.getFeedbackType(),
                queryDTO.getStatus(),
                queryDTO.getUserId(),
                queryDTO.getProcessedBy(),
                queryDTO.getKeyword(),
                queryDTO.getStartTime(),
                queryDTO.getEndTime()
        );
    }

    @Override
    public FeedbackVO getFeedbackById(Long id) {
        return feedbackMapper.selectFeedbackById(id);
    }

    @Override
    public boolean processFeedback(Long id, Integer status, String processResult, Long processedBy) {
        Feedback feedback = feedbackMapper.selectById(id);
        if (feedback == null) {
            return false;
        }
        
        feedback.setStatus(status);
        feedback.setProcessResult(processResult);
        feedback.setProcessedBy(processedBy);
        feedback.setProcessedAt(LocalDateTime.now());
        
        return feedbackMapper.updateById(feedback) > 0;
    }

    @Override
    public int batchUpdateStatus(List<Long> ids, Integer status, Long processedBy) {
        return feedbackMapper.batchUpdateStatus(ids, status, processedBy);
    }

    @Override
    public Map<String, Object> getFeedbackStatistics() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取状态统计
        List<Map<String, Object>> statusStats = feedbackMapper.countByStatus();
        Map<String, Integer> statusMap = new HashMap<>();
        for (Map<String, Object> stat : statusStats) {
            Integer statusValue = (Integer) stat.get("status");
            Long count = (Long) stat.get("count");
            statusMap.put(getStatusName(statusValue), count.intValue());
        }
        result.put("statusStats", statusMap);
        
        // 获取类型统计
        List<Map<String, Object>> typeStats = feedbackMapper.countByType();
        Map<String, Integer> typeMap = new HashMap<>();
        for (Map<String, Object> stat : typeStats) {
            Integer typeValue = (Integer) stat.get("feedback_type");
            Long count = (Long) stat.get("count");
            typeMap.put(getTypeName(typeValue), count.intValue());
        }
        result.put("typeStats", typeMap);
        
        // 获取总数
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        Long totalCount = feedbackMapper.selectCount(queryWrapper);
        result.put("totalCount", totalCount);
        
        return result;
    }

    @Override
    public boolean deleteFeedback(Long id) {
        return feedbackMapper.deleteById(id) > 0;
    }

    @Override
    public IPage<FeedbackVO> getFeedbackByUserId(Long userId, Integer pageNum, Integer pageSize) {
        FeedbackQueryDTO queryDTO = new FeedbackQueryDTO();
        queryDTO.setUserId(userId);
        queryDTO.setPageNum(pageNum);
        queryDTO.setPageSize(pageSize);
        return getFeedbackPage(queryDTO);
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        switch (status) {
            case 0: return "待处理";
            case 1: return "处理中";
            case 2: return "已解决";
            case 3: return "已关闭";
            default: return "未知";
        }
    }

    /**
     * 获取类型名称
     */
    private String getTypeName(Integer type) {
        switch (type) {
            case 0: return "建议";
            case 1: return "问题";
            case 2: return "功能需求";
            default: return "未知";
        }
    }
}