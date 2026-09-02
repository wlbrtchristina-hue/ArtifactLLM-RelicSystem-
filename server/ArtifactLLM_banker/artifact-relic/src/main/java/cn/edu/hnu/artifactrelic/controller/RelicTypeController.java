package cn.edu.hnu.artifactrelic.controller;

import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactrelic.dto.AuditActionDTO;
import cn.edu.hnu.artifactrelic.service.AuditService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/relic-type")
@Slf4j
public class RelicTypeController {
    @Autowired
    AuditService auditService;

    //查询文物类型详情
    @GetMapping("/{relicTypeId}")
    public Result getRelicType(@PathVariable Long relicTypeId) {
        return Result.success(auditService.getRelicTypeDetail(relicTypeId));
    }

    //添加文物类型
    @PostMapping
    public Result addRelic(@RequestBody String auditDatas) {
        // 解析JSON
        JSONObject jsonObject = JSON.parseObject(auditDatas);
        JSONArray typeDataArray = jsonObject.getJSONArray("typeData");
        for (int i = 0; i < typeDataArray.size(); i++) {
            JSONObject auditData = typeDataArray.getJSONObject(i);
            // 创建审核
            auditService.createAudit(auditData.toJSONString(), 4L);
        }
        return Result.success(String.format("成功添加%d个文物类型", typeDataArray.size()));
    }

    //修改文物类型
    @PutMapping("/{relicTypeId}")
    public Result alterRelicType(@RequestBody String auditData){
        auditService.createAudit(auditData,5L);
        return Result.success("修改文物类型");
    }


    //删除文物类型
    @DeleteMapping("/{relicTypeId}")
    public Result deleteRelicType(@RequestBody String auditData){
        auditService.createAudit(auditData,6L);
        return Result.success("删除文物类型");
    }
}
