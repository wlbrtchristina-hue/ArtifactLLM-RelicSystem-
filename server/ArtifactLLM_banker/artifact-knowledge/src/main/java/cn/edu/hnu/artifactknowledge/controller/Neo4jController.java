package cn.edu.hnu.artifactknowledge.controller;

import cn.edu.hnu.artifactcommon.result.Result;
import cn.edu.hnu.artifactknowledge.dto.GraphQueryDTO;
import cn.edu.hnu.artifactknowledge.service.IKnowledgeGraphService;
import cn.edu.hnu.artifactknowledge.service.IKnowledgeGraphHttpService;
import cn.edu.hnu.artifactknowledge.vo.GraphVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/knowledge/gstore", "/knowledge/neo4j", "/api/knowledge/neo4j"})
public class Neo4jController {

    @Autowired
    private IKnowledgeGraphService knowledgeGraphService;

    @Autowired
    private IKnowledgeGraphHttpService knowledgeGraphHttpService;

    @PostMapping("/query")
    public cn.edu.hnu.artifactcommon.result.Result<String> query(@RequestBody GraphQueryDTO queryDTO) {
        String cypher = queryDTO.getCypher();
        if (cypher == null || cypher.isBlank()) {
            cypher = queryDTO.getSparql();
        }
        if (cypher == null || cypher.isBlank()) {
            return cn.edu.hnu.artifactcommon.result.Result.error(400, "cypher 不能为空");
        }
        return cn.edu.hnu.artifactcommon.result.Result.success(knowledgeGraphService.query(cypher));
    }

    @GetMapping("/test")
    public cn.edu.hnu.artifactcommon.result.Result<String> test() {
        String cypher = "MATCH (n) RETURN n LIMIT 1";
        return cn.edu.hnu.artifactcommon.result.Result.success(knowledgeGraphService.query(cypher));
    }

    @GetMapping("/graph")
    public cn.edu.hnu.artifactcommon.result.Result<GraphVO> getGraph(@RequestParam String relicName) {
        return cn.edu.hnu.artifactcommon.result.Result.success(knowledgeGraphService.getRelicGraph(relicName));
    }

    @GetMapping("/httpGraph")
    public cn.edu.hnu.artifactcommon.result.Result<GraphVO> getHttpGraph(@RequestParam String relicName) {
        return cn.edu.hnu.artifactcommon.result.Result.success(knowledgeGraphHttpService.getRelicGraph(relicName));
    }

    @PostMapping("/import")
    public cn.edu.hnu.artifactcommon.result.Result<String> importArtifacts(@RequestParam String path) {
        try {
            knowledgeGraphService.importArtifacts(path);
            return cn.edu.hnu.artifactcommon.result.Result.success("Import started/completed successfully.");
        } catch (Exception e) {
            return cn.edu.hnu.artifactcommon.result.Result.error(500, "Import failed: " + e.getMessage());
        }
    }
}
