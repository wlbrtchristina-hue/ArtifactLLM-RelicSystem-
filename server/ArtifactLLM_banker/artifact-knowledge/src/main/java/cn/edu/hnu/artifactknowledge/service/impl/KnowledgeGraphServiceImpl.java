package cn.edu.hnu.artifactknowledge.service.impl;

import cn.edu.hnu.artifactknowledge.connector.Neo4jConnector;
import cn.edu.hnu.artifactknowledge.service.IKnowledgeGraphService;
import cn.edu.hnu.artifactknowledge.vo.GraphLink;
import cn.edu.hnu.artifactknowledge.vo.GraphNode;
import cn.edu.hnu.artifactknowledge.vo.GraphVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@Service
public class KnowledgeGraphServiceImpl implements IKnowledgeGraphService {

    @Autowired
    private Neo4jConnector neo4jConnector;

    @Override
    public void importArtifacts(String directoryPath) {
        log.info("Starting artifact import from: {}", directoryPath);
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".json"))
                 .forEach(this::processJsonFile);
        } catch (IOException e) {
            log.error("Error reading files from directory: {}", directoryPath, e);
            throw new RuntimeException("Import failed", e);
        }
        log.info("Import completed.");
    }

    private void processJsonFile(Path filePath) {
        try {
            String content = Files.readString(filePath);
            JSONObject json = JSON.parseObject(content);
            importArtifactData(json);
        } catch (Exception e) {
            log.error("Failed to process file: {}", filePath, e);
        }
    }

    private void importArtifactData(JSONObject json) {
        String uuid = json.getString("uuid");
        JSONObject info = json.getJSONObject("artifact_info");
        if (uuid == null || info == null) return;

        String name = info.getString("文物名");
        String relicNo = info.getString("文物号");
        String category = info.getString("分类");
        String era = info.getString("年代");
        String detailUrl = json.getString("detail_url");
        
        // 1. Merge Artifact Node
        String createArtifactCypher = """
            MERGE (a:Artifact {uuid: $uuid})
            SET a.name = $name,
                a.relicNo = $relicNo,
                a.detailUrl = $detailUrl
            """;
        Map<String, Object> artifactParams = new HashMap<>();
        artifactParams.put("uuid", uuid);
        artifactParams.put("name", name);
        artifactParams.put("relicNo", relicNo);
        artifactParams.put("detailUrl", detailUrl);
        neo4jConnector.execute(createArtifactCypher, artifactParams);

        // 2. Merge Category Node and Relationship
        if (category != null && !category.isBlank()) {
            String categoryCypher = """
                MATCH (a:Artifact {uuid: $uuid})
                MERGE (c:Category {name: $category})
                MERGE (a)-[:BELONGS_TO]->(c)
                """;
            neo4jConnector.execute(categoryCypher, Map.of("uuid", uuid, "category", category));
        }

        // 3. Merge Era Node and Relationship
        if (era != null && !era.isBlank()) {
            String eraCypher = """
                MATCH (a:Artifact {uuid: $uuid})
                MERGE (e:Era {name: $era})
                MERGE (a)-[:FROM_ERA]->(e)
                """;
            neo4jConnector.execute(eraCypher, Map.of("uuid", uuid, "era", era));
        }

        // 4. Handle Colors
        JSONArray colors = info.getJSONArray("颜色");
        if (colors != null) {
            for (int i = 0; i < colors.size(); i++) {
                JSONObject color = colors.getJSONObject(i);
                String code = color.getString("code");
                String background = color.getString("background");
                if (code != null) {
                    String colorCypher = """
                        MATCH (a:Artifact {uuid: $uuid})
                        MERGE (col:Color {code: $code})
                        SET col.background = $background
                        MERGE (a)-[:HAS_COLOR]->(col)
                        """;
                    neo4jConnector.execute(colorCypher, Map.of("uuid", uuid, "code", code, "background", background != null ? background : ""));
                }
            }
        }

        // 5. Handle Same Category Artifacts (Recommendations)
        JSONArray sameCategory = json.getJSONArray("same_category_artifacts");
        if (sameCategory != null) {
            for (int i = 0; i < sameCategory.size(); i++) {
                JSONObject other = sameCategory.getJSONObject(i);
                String otherUuid = other.getString("uuid");
                String otherName = other.getString("name");
                
                if (otherUuid != null) {
                    String relationCypher = """
                        MATCH (a:Artifact {uuid: $uuid})
                        MERGE (o:Artifact {uuid: $otherUuid})
                        ON CREATE SET o.name = $otherName
                        MERGE (a)-[:RELATED_TO]->(o)
                        """;
                    neo4jConnector.execute(relationCypher, Map.of("uuid", uuid, "otherUuid", otherUuid, "otherName", otherName != null ? otherName : ""));
                }
            }
        }
    }

    @Override
    public String query(String cypher) {
        List<Map<String, Object>> rows = neo4jConnector.query(cypher);
        List<Map<String, Object>> normalized = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                out.put(entry.getKey(), normalizeValue(entry.getValue()));
            }
            normalized.add(out);
        }
        return JSONArray.toJSONString(normalized);
    }

    @Override
    public GraphVO getRelicGraph(String relicName) {
        String cypher = """
                MATCH (a:Artifact)
                WHERE toString(coalesce(a.name,'')) = $relicName
                WITH collect(a)[0] AS a
                OPTIONAL MATCH (a)-[r1:FROM_ERA]->(e:Era)
                RETURN a AS n, r1 AS r, e AS m
                UNION
                MATCH (a:Artifact)
                WHERE toString(coalesce(a.name,'')) = $relicName
                WITH collect(a)[0] AS a
                OPTIONAL MATCH (a)-[r2:BELONGS_TO]->(c:Category)
                RETURN a AS n, r2 AS r, c AS m
                """;

        List<Map<String, Object>> rows = neo4jConnector.query(cypher, Map.of("relicName", relicName));

        Map<String, GraphNode> nodeMap = new HashMap<>();
        List<GraphLink> links = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            Object nObj = row.get("n");
            Object mObj = row.get("m");
            Object rObj = row.get("r");
            if (!(nObj instanceof Node) || !(mObj instanceof Node) || !(rObj instanceof Relationship)) {
                continue;
            }

            Node n = (Node) nObj;
            Node m = (Node) mObj;
            Relationship r = (Relationship) rObj;

            GraphNode nNode = toGraphNode(n, relicName);
            GraphNode mNode = toGraphNode(m, relicName);

            nodeMap.putIfAbsent(nNode.getId(), nNode);
            nodeMap.putIfAbsent(mNode.getId(), mNode);

            GraphLink link = new GraphLink();
            link.setSource(nNode.getId());
            link.setTarget(mNode.getId());
            link.setValue(r.type());
            links.add(link);
        }

        return new GraphVO(new ArrayList<>(nodeMap.values()), links);
    }

    private GraphNode toGraphNode(Node node, String relicName) {
        String id = node.elementId();
        String name = extractName(node);

        boolean isMain = false;
        if (relicName != null && !relicName.isBlank()) {
            for (String key : List.of("name", "relicName", "label", "title")) {
                if (node.containsKey(key) && node.get(key) != null) {
                    String v = String.valueOf(node.get(key).asObject());
                    if (v.equals(relicName)) {
                        isMain = true;
                        break;
                    }
                }
            }
        }

        GraphNode graphNode = new GraphNode();
        graphNode.setId(id);
        graphNode.setName(name);
        graphNode.setCategory(isMain ? 0 : 1);
        graphNode.setSymbolSize(isMain ? 20.0 : 10.0);
        graphNode.setValue(name);
        return graphNode;
    }

    private String extractName(Node node) {
        for (String key : List.of("name", "label", "title")) {
            if (node.containsKey(key) && node.get(key) != null) {
                Value v = node.get(key);
                if (!v.isNull()) {
                    return String.valueOf(v.asObject());
                }
            }
        }
        if (!node.labels().iterator().hasNext()) {
            return node.elementId();
        }
        return node.labels().iterator().next() + ":" + node.elementId();
    }

    private Object normalizeValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Node) {
            Node node = (Node) value;
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("elementId", node.elementId());
            List<String> labels = new ArrayList<>();
            node.labels().forEach(labels::add);
            out.put("labels", labels);
            out.put("properties", node.asMap());
            return out;
        }
        if (value instanceof Relationship) {
            Relationship rel = (Relationship) value;
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("elementId", rel.elementId());
            out.put("type", rel.type());
            out.put("startNodeId", rel.startNodeId());
            out.put("endNodeId", rel.endNodeId());
            out.put("properties", rel.asMap());
            return out;
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                out.put(String.valueOf(entry.getKey()), normalizeValue(entry.getValue()));
            }
            return out;
        }
        if (value instanceof Iterable) {
            List<Object> out = new ArrayList<>();
            for (Object v : (Iterable<?>) value) {
                out.add(normalizeValue(v));
            }
            return out;
        }
        if (value instanceof Object[]) {
            List<Object> out = new ArrayList<>();
            for (Object v : (Object[]) value) {
                out.add(normalizeValue(v));
            }
            return out;
        }
        if (value instanceof Value) {
            Value v = (Value) value;
            if (v.isNull()) {
                return null;
            }
            return normalizeValue(v.asObject());
        }
        return value;
    }
}
