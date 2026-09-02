package cn.edu.hnu.artifactrelic.controller;

import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactrelic.dto.EntityInstanceDTO;
import cn.edu.hnu.artifactrelic.dto.ModelDefDTO;
import cn.edu.hnu.artifactrelic.service.IModelingService;
import cn.edu.hnu.artifactcommon.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modeling")
public class ModelingController {

    @Autowired
    private IModelingService modelingService;

    // Define/Save Model
    @PostMapping("/define")
    public Result saveModelDef(@RequestBody ModelDefDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("User not logged in");
        }
        Long modelId = modelingService.saveModelDef(dto, userId);
        return Result.success(modelId);
    }

    // Get Model Definition
    @GetMapping("/define/{id}")
    public Result getModelDef(@PathVariable Long id) {
        return Result.success(modelingService.getModelDef(id));
    }

    // List Models
    @GetMapping("/list")
    public Result listModels() {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("User not logged in");
        }
        return Result.success(modelingService.listModels(userId));
    }

    // Save Instance
    @PostMapping("/instance")
    public Result saveInstance(@RequestBody EntityInstanceDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.error("User not logged in");
        }
        Long instanceId = modelingService.saveInstance(dto, userId);
        return Result.success(instanceId);
    }

    // List Instances
    @GetMapping("/instance/list")
    public Result listInstances(@RequestParam(required = false) Long modelId, 
                                @RequestParam(required = false) Long entityDefId) {
        return Result.success(modelingService.listInstances(modelId, entityDefId));
    }
}
