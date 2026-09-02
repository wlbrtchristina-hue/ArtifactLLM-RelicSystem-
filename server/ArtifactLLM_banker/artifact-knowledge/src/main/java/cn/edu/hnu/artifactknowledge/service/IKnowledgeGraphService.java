package cn.edu.hnu.artifactknowledge.service;

import cn.edu.hnu.artifactknowledge.vo.GraphVO;

public interface IKnowledgeGraphService {
    String query(String cypher);

    GraphVO getRelicGraph(String relicName);

    void importArtifacts(String directoryPath);
}

