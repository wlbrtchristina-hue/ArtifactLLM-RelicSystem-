package cn.edu.hnu.artifactrelic.vo;

import lombok.Data;

@Data
public class AttributeVO {
    private String name;        // 属性名称
    private String type;        // 属性类型
    private String description; // 属性描述
    private Boolean required;   // 是否必填
}