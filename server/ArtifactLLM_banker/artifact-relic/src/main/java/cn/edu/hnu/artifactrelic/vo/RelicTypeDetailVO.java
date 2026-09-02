package cn.edu.hnu.artifactrelic.vo;

import lombok.Data;
import java.util.List;

@Data
public class RelicTypeDetailVO {
    private Long relicTypeId;       // 文物类型ID
    private String typeName;        // 文物类型名称
    private String description;     // 类型描述
    private List<AttributeVO> attributes;  // 属性列表
}
