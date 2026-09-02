package cn.edu.hnu.artifactrelic.controller;

import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactrelic.dto.RelicQueryDTO;
import cn.edu.hnu.artifactrelic.service.AuditService;
import cn.edu.hnu.artifactrelic.service.CulturalRelicsService;
import cn.edu.hnu.artifactrelic.service.RelicQueryService;
import cn.edu.hnu.artifactrelic.vo.RelicsBasicVO;
import cn.edu.hnu.artifactrelic.vo.RelicsDetailVO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/relics")
@Slf4j
public class CulturalRelicController {
    @Autowired
    private CulturalRelicsService culturalRelicsService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private RelicQueryService relicQueryService;
    //筛选获取文物的简单信息
    @GetMapping
    public Result getRelics(
           @RequestParam(required = false) String era,
           @RequestParam(required = false) String material,
           @RequestParam(required = false) String typeName,
           @RequestParam(required = false) String discoverySite,
           @RequestParam(defaultValue = "1") Integer pageNum,
           @RequestParam(defaultValue = "10") Integer pageSize) {
        // 构建查询条件DTO
        RelicQueryDTO queryDTO = new RelicQueryDTO();
        String eraVal = era == null ? null : era.trim();
        String materialVal = material == null ? null : material.trim();
        String typeVal = typeName == null ? null : typeName.trim();
        String siteVal = discoverySite == null ? null : discoverySite.trim();
        queryDTO.setEra(eraVal);
        queryDTO.setMaterial(materialVal);
        queryDTO.setTypeName(typeVal);
        queryDTO.setDiscoverySite(siteVal);

        //分页对象
        Page<RelicsBasicVO> page = new Page<>(pageNum, pageSize);

        // 执行查询
        Page<RelicsBasicVO> result = culturalRelicsService.getRelicsByCondition(queryDTO, page);
        return Result.success(result);
    }

    @GetMapping("/search")
    public Result search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "true") boolean semantic,
            @RequestParam(defaultValue = "false") boolean related,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<RelicsBasicVO> page = new Page<>(pageNum, pageSize);
        if (related) {
            List<RelicsBasicVO> primary = culturalRelicsService.findByNameExact(q);
            if (primary.isEmpty()) {
                return Result.success(new ArrayList<>());
            }
            RelicsBasicVO base = primary.get(0);
            Page<RelicsBasicVO> rel = culturalRelicsService.findRelatedByPrimary(base, page);
            List<RelicsBasicVO> primaryResults = primary;
            List<RelicsBasicVO> relatedRecords = rel.getRecords();
            long total = rel.getTotal();
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("primaryResults", primaryResults);
            Page<RelicsBasicVO> relatedPage = new Page<>(pageNum, pageSize, total);
            relatedPage.setRecords(relatedRecords);
            data.put("relatedResults", relatedPage);
            return Result.success(data);
        }
        if (semantic) {
            Page<RelicsBasicVO> results = culturalRelicsService.searchSemantic(q, page);
            return Result.success(results);
        }
        List<RelicsBasicVO> exactList = culturalRelicsService.findByNameExact(q);
        Page<RelicsBasicVO> results = new Page<>(pageNum, pageSize, exactList.size());
        int start = Math.max(0, (pageNum - 1) * pageSize);
        int end = Math.min(exactList.size(), start + pageSize);
        results.setRecords(exactList.subList(start, end));
        return Result.success(results);
    }
    //查询文物详情
    @GetMapping("/{relicsId}")
    public Result getRelicDetail(@PathVariable Integer relicsId) {
        try {
            if (relicsId == null || relicsId <= 0) {
                return Result.error("文物ID参数错误");
            }

            RelicsDetailVO detail = culturalRelicsService.getRelicDetail(relicsId);
            return Result.success(detail);

        } catch (Exception e){
            log.error("查询文物详情异常，文物ID: {}", relicsId, e);
            return Result.error("系统错误，请稍后重试");
        }
    }


    //添加文物
    @PostMapping
    public Result addRelic(@RequestBody String auditData) {
        auditService.createAudit(auditData,7L);
        return Result.success("创建文物");
    }

    //修改文物
    @PutMapping("/{relicsId}")
    public Result alterRelic(@RequestBody String auditData){
        auditService.createAudit(auditData,8L);
        return Result.success("修改文物");
    }

    //删除文物
    @DeleteMapping("/{relicsId}")
    public Result deleteRelic(@RequestBody String auditData)
    {
        auditService.createAudit(auditData,9L);
        return Result.success("删除文物");
    }

    //批量导入文物
    @PostMapping("/batch")
    public Result batchAddRelic(@RequestBody String auditDatas){
        // 一行代码转换：JSON -> List<String>
        List<String> auditDataList = JSON.parseObject(auditDatas)
                .getJSONArray("relics")
                .stream()
                .map(JSON::toJSONString)
                .toList();
        // 遍历创建审核
        for (String auditData : auditDataList) {
            auditService.createAudit(auditData,7L);
        }
        return Result.success("批量导入文件");
    }

    //展示年代列表
    @GetMapping("/eras")
    public Result eraList(){
        List<String> eras = relicQueryService.getAllEras();
        return Result.success(eras);
    }
    //展示材质列表
    @GetMapping("/materials")
    public Result materialList(){
        List<String> materials = relicQueryService.getAllMaterials();
        return Result.success(materials);
    }
    //展示类型列表
    @GetMapping("/types")
    public Result typeList(){
        List<String> types = relicQueryService.getAllTypes();
        return Result.success(types);
    }
    //展示出土地列表
    @GetMapping("/discovery-sites")
    public Result siteList(){
        List<String> discoverySites = relicQueryService.getAllSites();
        return Result.success(discoverySites);
    }
}
