package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("meta_attr_def")
public class MetaAttrDef {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long entityDefId;
    private String name;
    private String code;
    private String type; // text, number, date, boolean, select, file
    private Boolean required;
    private String description;
    private String options; // JSON string
    private LocalDateTime createTime;
}
