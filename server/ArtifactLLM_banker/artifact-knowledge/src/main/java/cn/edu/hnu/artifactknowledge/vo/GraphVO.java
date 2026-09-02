package cn.edu.hnu.artifactknowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphVO {
    private List<GraphNode> nodes;
    private List<GraphLink> links;
}