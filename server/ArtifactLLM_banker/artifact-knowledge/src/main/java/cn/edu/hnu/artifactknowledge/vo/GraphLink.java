package cn.edu.hnu.artifactknowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphLink {
    private String source;
    private String target;
    private String value; // The relationship name
}