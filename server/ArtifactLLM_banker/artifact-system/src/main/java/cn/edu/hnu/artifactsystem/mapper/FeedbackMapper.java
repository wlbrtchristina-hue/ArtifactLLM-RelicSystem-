package cn.edu.hnu.artifactsystem.mapper;

import cn.edu.hnu.artifactsystem.entity.Feedback;
import cn.edu.hnu.artifactsystem.vo.FeedbackVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 反馈映射器接口
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {
    
    /**
     * 分页查询反馈列表（包含用户名和处理人姓名）
     * 
     * @param page 分页对象
     * @param feedbackType 反馈类型
     * @param status 状态
     * @param userId 用户ID
     * @param processedBy 处理人ID
     * @param keyword 关键词
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 反馈列表
     */
    IPage<FeedbackVO> selectFeedbackPage(Page<FeedbackVO> page,
                                         @Param("feedbackType") Integer feedbackType,
                                         @Param("status") Integer status,
                                         @Param("userId") Long userId,
                                         @Param("processedBy") Long processedBy,
                                         @Param("keyword") String keyword,
                                         @Param("startTime") String startTime,
                                         @Param("endTime") String endTime);
    
    /**
     * 根据ID查询反馈详情（包含用户名和处理人姓名）
     * 
     * @param id 反馈ID
     * @return 反馈详情
     */
    FeedbackVO selectFeedbackById(@Param("id") Long id);
    
    /**
     * 批量更新反馈状态
     * 
     * @param ids 反馈ID列表
     * @param status 新状态
     * @param processedBy 处理人ID
     * @return 更新数量
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, 
                          @Param("status") Integer status, 
                          @Param("processedBy") Long processedBy);
    
    /**
     * 统计各状态反馈数量
     * 
     * @return 统计结果
     */
    List<java.util.Map<String, Object>> countByStatus();
    
    /**
     * 统计各类型反馈数量
     * 
     * @return 统计结果
     */
    List<java.util.Map<String, Object>> countByType();
}