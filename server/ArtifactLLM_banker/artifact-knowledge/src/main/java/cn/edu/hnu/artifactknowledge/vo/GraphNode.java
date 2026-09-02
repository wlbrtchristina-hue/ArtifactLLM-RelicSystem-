package cn.edu.hnu.artifactknowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphNode {
    private String id;
    private String name;
    private int category; // 0: main node, 1: property, 2: value (simplified)
    private double symbolSize;
    private String value;
}
