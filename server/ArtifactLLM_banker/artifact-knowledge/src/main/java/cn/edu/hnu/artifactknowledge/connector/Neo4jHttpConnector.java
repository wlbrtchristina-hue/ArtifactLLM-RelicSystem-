package cn.edu.hnu.artifactknowledge.connector;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class Neo4jHttpConnector {

    @Value("${neo4j.http.base-url:http://localhost:7474}")
    private String baseUrl;

    @Value("${neo4j.http.commit-path:/db/neo4j/tx/commit}")
    private String commitPath;

    @Value("${spring.neo4j.authentication.username}")
    private String username;

    @Value("${spring.neo4j.authentication.password}")
    private String password;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, Object>> query(String cypher, Map<String, Object> params) {
        String url = baseUrl + commitPath;
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> stmt = new HashMap<>();
        stmt.put("statement", cypher);
        stmt.put("parameters", params == null ? Map.of() : params);
        payload.put("statements", List.of(stmt));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encoded);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        Map<?, ?> resp = restTemplate.postForObject(url, entity, Map.class);
        if (resp == null) {
            return List.of();
        }
        List<?> results = (List<?>) resp.get("results");
        if (results == null || results.isEmpty()) {
            return List.of();
        }
        Map<?, ?> first = (Map<?, ?>) results.get(0);
        List<?> columns = (List<?>) first.get("columns");
        List<?> data = (List<?>) first.get("data");
        List<Map<String, Object>> out = new ArrayList<>();
        if (columns == null || data == null) {
            return out;
        }
        for (Object d : data) {
            Map<?, ?> rowObj = (Map<?, ?>) d;
            List<?> row = (List<?>) rowObj.get("row");
            Map<String, Object> m = new HashMap<>();
            for (int i = 0; i < columns.size() && i < row.size(); i++) {
                String col = String.valueOf(columns.get(i));
                m.put(col, row.get(i));
            }
            out.add(m);
        }
        return out;
    }
}
