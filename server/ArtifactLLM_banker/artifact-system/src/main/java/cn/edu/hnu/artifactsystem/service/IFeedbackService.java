package cn.edu.hnu.artifactsystem.service;

import cn.edu.hnu.artifactsystem.dto.FeedbackDTO;
import cn.edu.hnu.artifactsystem.dto.FeedbackQueryDTO;
import cn.edu.hnu.artifactsystem.entity.Feedback;
import cn.edu.hnu.artifactsystem.vo.FeedbackVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 反馈服务接口
 */
public interface IFeedbackService extends IService<Feedback> {
    
    /**
     * 提交反馈
     * 
     * @param feedbackDTO 反馈信息
     * @return 反馈ID
     */
    Long submitFeedback(FeedbackDTO feedbackDTO);
    
    /**
     * 分页查询反馈列表
     * 
     * @param queryDTO 查询条件
     * @return 反馈列表
     */
    IPage<FeedbackVO> getFeedbackPage(FeedbackQueryDTO queryDTO);
    
    /**
     * 根据ID获取反馈详情
     * 
     * @param id 反馈ID
     * @return 反馈详情
     */
    FeedbackVO getFeedbackById(Long id);
    
    /**
     * 处理反馈
     * 
     * @param id 反馈ID
     * @param status 状态
     * @param processResult 处理结果
     * @param processedBy 处理人ID
     * @return 是否成功
     */
    boolean processFeedback(Long id, Integer status, String processResult, Long processedBy);
    
    /**
     * 批量更新反馈状态
     * 
     * @param ids 反馈ID列表
     * @param status 新状态
     * @param processedBy 处理人ID
     * @return 更新数量
     */
    int batchUpdateStatus(List<Long> ids, Integer status, Long processedBy);
    
    /**
     * 获取反馈统计数据
     * 
     * @return 统计数据
     */
    Map<String, Object> getFeedbackStatistics();
    
    /**
     * 删除反馈
     * 
     * @param id 反馈ID
     * @return 是否成功
     */
    boolean deleteFeedback(Long id);
    
    /**
     * 获取用户的反馈列表
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 反馈列表
     */
    IPage<FeedbackVO> getFeedbackByUserId(Long userId, Integer pageNum, Integer pageSize);
}