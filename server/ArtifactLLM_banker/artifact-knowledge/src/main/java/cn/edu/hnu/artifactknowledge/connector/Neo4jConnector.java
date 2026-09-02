package cn.edu.hnu.artifactknowledge.connector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class Neo4jConnector {

    @Autowired
    private Neo4jClient neo4jClient;

    public List<Map<String, Object>> query(String cypher) {
        return query(cypher, Collections.emptyMap());
    }

    public List<Map<String, Object>> query(String cypher, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return (List<Map<String, Object>>) neo4jClient.query(cypher).fetch().all();
        }
        return (List<Map<String, Object>>) neo4jClient.query(cypher).bindAll(params).fetch().all();
    }

    public void execute(String cypher, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            neo4jClient.query(cypher).run();
        } else {
            neo4jClient.query(cypher).bindAll(params).run();
        }
    }
}

