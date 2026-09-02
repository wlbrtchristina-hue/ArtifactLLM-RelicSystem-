package cn.edu.hnu.artifactrelic.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

// RelicBasicVO.java - 文物基础信息VO
@Data
public class RelicsBasicVO {
    private Integer relicsId;
    private String relicsName;
    private String era;
    private String material;
    private String discoverySite;
    private String typeName;
    private List<String> images;
    private LocalDateTime createdAt;
    private String relevance;
}
