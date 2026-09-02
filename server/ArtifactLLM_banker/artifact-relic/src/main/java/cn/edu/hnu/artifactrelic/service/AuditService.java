package cn.edu.hnu.artifactrelic.service;

import cn.edu.hnu.artifactrelic.dto.AuditActionDTO;
import cn.edu.hnu.artifactrelic.entity.Audit;
import cn.edu.hnu.artifactrelic.vo.AuditVO;
import cn.edu.hnu.artifactrelic.vo.MyRelicTypeVO;
import cn.edu.hnu.artifactrelic.vo.MyRelicVO;
import cn.edu.hnu.artifactrelic.vo.RelicTypeDetailVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface AuditService extends IService<Audit> {
    /**
     * 根据审核状态查询审核记录
     * @param status 审核状态: pending, approved, rejected
     * @return 审核记录列表
     */
    List<AuditVO> getAuditsByStatus(String status);


    AuditVO getAuditDetail(Long auditId);

    void approveAudit(Long auditId, AuditActionDTO actionDTO);

    void rejectAudit(Long auditId, AuditActionDTO actionDTO);

    void createAudit(String auditData, Long auditTypeId);

    List<MyRelicTypeVO> getMyRelicTypes(Long userId);

    // 获取我的文物
    List<MyRelicVO> getMyRelics(Long userId);

    //查询文物类型详情
    RelicTypeDetailVO getRelicTypeDetail(Long relicTypeId);
}
