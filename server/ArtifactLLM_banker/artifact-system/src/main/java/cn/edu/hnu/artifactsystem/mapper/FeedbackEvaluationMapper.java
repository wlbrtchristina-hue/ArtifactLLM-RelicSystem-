package cn.edu.hnu.artifactsystem.mapper;

import cn.edu.hnu.artifactsystem.entity.FeedbackEvaluation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 反馈评价映射器接口
 */
@Mapper
public interface FeedbackEvaluationMapper extends BaseMapper<FeedbackEvaluation> {
    
    /**
     * 根据反馈ID查询评价列表
     * 
     * @param feedbackId 反馈ID
     * @return 评价列表
     */
    List<FeedbackEvaluation> selectByFeedbackId(@Param("feedbackId") Long feedbackId);
    
    /**
     * 根据用户ID查询评价列表
     * 
     * @param userId 用户ID
     * @return 评价列表
     */
    List<FeedbackEvaluation> selectByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否已评价该反馈
     * 
     * @param feedbackId 反馈ID
     * @param userId 用户ID
     * @return 评价数量
     */
    int countByFeedbackIdAndUserId(@Param("feedbackId") Long feedbackId, @Param("userId") Long userId);
    
    /**
     * 统计反馈评价的平均分
     * 
     * @param feedbackId 反馈ID
     * @return 平均分
     */
    Double avgRatingByFeedbackId(@Param("feedbackId") Long feedbackId);
    
    /**
     * 统计处理人的评价平均分
     * 
     * @param processedBy 处理人ID
     * @return 平均分
     */
    /**
     * 统计反馈评价的评分分布
     *
     * @param feedbackId 反馈ID
     * @return 评分分布统计
     */
    List<Map<String, Object>> getRatingStatistics(@Param("feedbackId") Long feedbackId);

    Map<String, Object> avgRatingByProcessedBy(@Param("processedBy") Long processedBy);
}