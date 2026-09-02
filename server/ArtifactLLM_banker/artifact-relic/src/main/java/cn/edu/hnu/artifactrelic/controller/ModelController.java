package cn.edu.hnu.artifactrelic.controller;

import cn.edu.hnu.artifactcommon.context.UserContext;
import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactrelic.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/model")
public class ModelController {
    @Autowired
    AuditService auditService;

    @GetMapping("/my-relic-type")
    public Result getMyRelicType(){
        Long userId = UserContext.getCurrentUserId();
        return Result.success(auditService.getMyRelicTypes(userId));
    }

    @GetMapping("/my-relics")
    public Result getMyRelics(){
        Long userId = UserContext.getCurrentUserId();
        return Result.success(auditService.getMyRelics(userId));
    }
}
