package cn.edu.hnu.artifactrelic.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleRelationVO {
    private Long relationId;
    private String relationName;
    private String targetString;
    private String relationDescription;
    private LocalDateTime createdAt;

}
