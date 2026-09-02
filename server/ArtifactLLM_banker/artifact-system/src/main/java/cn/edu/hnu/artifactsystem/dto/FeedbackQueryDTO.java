package cn.edu.hnu.artifactsystem.dto;

import lombok.Data;

/**
 * 反馈查询数据传输对象
 */
@Data
public class FeedbackQueryDTO {
    /**
     * 反馈ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 反馈类型：0-建议，1-问题，2-功能需求
     */
    private Integer feedbackType;

    /**
     * 状态：0-待处理，1-处理中，2-已解决，3-已关闭
     */
    private Integer status;

    /**
     * 处理人ID
     */
    private Long processedBy;

    /**
     * 处理人姓名
     */
    private String processedByName;

    /**
     * 关键词搜索（标题和内容）
     */
    private String keyword;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}