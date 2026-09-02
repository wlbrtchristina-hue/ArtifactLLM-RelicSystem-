package cn.edu.hnu.artifactrelic.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// RelicDetailVO.java - 文物详情VO
@Data
public class RelicsDetailVO {
    private Integer relicsId;
    private String relicsName;
    private String era;
    private String material;
    private String discoverySite;
    private String currentLocation;
    private String description;
    private LocalDateTime createdAt;
    // 自定义字段
    private JSONObject customFields;

    // 类型信息
    private Integer relicsTypeId;
    private String typeName;
    private String typeDescription;



    // 图片列表
    private List<String> images = new ArrayList<>();

    // 简化版关系（知识图谱）
    private List<SimpleRelationVO> relations = new ArrayList<>();

    // 创建者信息
    private Integer createdBy;
    private String creatorName;
}