package cn.edu.hnu.artifactrelic.controller;

import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactrelic.dto.AuditActionDTO;
import cn.edu.hnu.artifactrelic.mapper.AuditMapper;
import cn.edu.hnu.artifactrelic.service.AuditService;
import cn.edu.hnu.artifactrelic.vo.AuditVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@Slf4j
public class AuditController {
    @Autowired
    private AuditService auditService;

    //根据状态查询审核记录
    @GetMapping("/status/{status}")
     public Result getAuditsByStatus(@PathVariable String status) {
     List<AuditVO> audits = auditService.getAuditsByStatus(status);
     return Result.success(audits);
     }


    /**
     * 根据审核ID查询审核详情
     */
    @GetMapping("/{auditId}")
    public Result getAuditDetail(@PathVariable Long auditId) {
        return Result.success(auditService.getAuditDetail(auditId));
    }

    /**
     * 审核通过
     */
    @PutMapping("/{auditId}/approve")
    public Result approveAudit(@PathVariable Long auditId, @RequestBody AuditActionDTO actionDTO) {
        auditService.approveAudit(auditId, actionDTO);
        return Result.success("审核通过");
    }

    /**
     * 审核拒绝
     */
    @PutMapping("/{auditId}/reject")
    public Result rejectAudit(@PathVariable Long auditId, @RequestBody AuditActionDTO actionDTO) {
        auditService.rejectAudit(auditId, actionDTO);
        return Result.success("审核不通过");
    }

}
