package cn.edu.hnu.artifactknowledge.service.impl;

import cn.edu.hnu.artifactknowledge.connector.Neo4jHttpConnector;
import cn.edu.hnu.artifactknowledge.service.IKnowledgeGraphHttpService;
import cn.edu.hnu.artifactknowledge.vo.GraphLink;
import cn.edu.hnu.artifactknowledge.vo.GraphNode;
import cn.edu.hnu.artifactknowledge.vo.GraphVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KnowledgeGraphHttpServiceImpl implements IKnowledgeGraphHttpService {

    @Autowired
    private Neo4jHttpConnector httpConnector;

    @Override
    public GraphVO getRelicGraph(String relicName) {
        String cypher = ""
                + "MATCH (a:Artifact) "
                + "WHERE toString(coalesce(a.name,'')) = $relicName "
                + "MATCH (a)-[r]-(n) "
                + "WHERE type(r) <> 'HAS_IMAGE' "
                + "AND NOT (n:Artifact AND toString(coalesce(n.name,'')) <> $relicName) "
                + "RETURN elementId(a) AS nId, elementId(n) AS mId, labels(a) AS nLabels, labels(n) AS mLabels, "
                + "a AS nProps, n AS mProps, type(r) AS rType, elementId(a) AS rStart, elementId(n) AS rEnd "
                + "LIMIT 200";

        List<Map<String, Object>> rows = httpConnector.query(cypher, Map.of("relicName", relicName));
        Map<String, GraphNode> nodeMap = new HashMap<>();
        List<GraphLink> links = new ArrayList<>();
        Set<String> linkSet = new HashSet<>();

        for (Map<String, Object> row : rows) {
            String nId = String.valueOf(row.get("nId"));
            String mId = String.valueOf(row.get("mId"));
            Map<String, Object> nProps = castMap(row.get("nProps"));
            Map<String, Object> mProps = castMap(row.get("mProps"));
            String rType = String.valueOf(row.get("rType"));
            String rStart = String.valueOf(row.get("rStart"));
            String rEnd = String.valueOf(row.get("rEnd"));

            GraphNode nNode = toGraphNode(nId, nProps, relicName);
            GraphNode mNode = toGraphNode(mId, mProps, relicName);
            nodeMap.putIfAbsent(nNode.getId(), nNode);
            nodeMap.putIfAbsent(mNode.getId(), mNode);

            String linkKey = rStart + "|" + rEnd + "|" + rType;
            if (!linkSet.contains(linkKey)) {
                GraphLink link = new GraphLink();
                link.setSource(rStart);
                link.setTarget(rEnd);
                link.setValue(rType);
                links.add(link);
                linkSet.add(linkKey);
            }
        }
        return new GraphVO(new ArrayList<>(nodeMap.values()), links);
    }

    private Map<String, Object> castMap(Object v) {
        if (v instanceof Map) {
            Map<?, ?> in = (Map<?, ?>) v;
            Map<String, Object> out = new HashMap<>();
            for (Map.Entry<?, ?> e : in.entrySet()) {
                out.put(String.valueOf(e.getKey()), e.getValue());
            }
            return out;
        }
        return Map.of();
    }

    private GraphNode toGraphNode(String id, Map<String, Object> props, String relicName) {
        String name = extractName(props);
        boolean isMain = false;
        if (relicName != null && !relicName.isBlank() && name != null) {
            isMain = name.equals(relicName);
        }
        GraphNode node = new GraphNode();
        node.setId(id);
        node.setName(name);
        node.setCategory(isMain ? 0 : 1);
        node.setSymbolSize(isMain ? 80.0 : 60.0);
        node.setValue(name);
        return node;
    }

    private String extractName(Map<String, Object> props) {
        for (String key : List.of("name", "relicName", "label", "title")) {
            Object v = props.get(key);
            if (v != null) {
                return String.valueOf(v);
            }
        }
        return null;
    }
}
