package cn.edu.hnu.artifactknowledge.service;

import cn.edu.hnu.artifactknowledge.vo.GraphVO;

public interface IKnowledgeGraphHttpService {
    GraphVO getRelicGraph(String relicName);
}

