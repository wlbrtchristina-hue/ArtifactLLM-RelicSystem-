package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("meta_relation_def")
public class MetaRelationDef {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long modelId;
    private String name;
    private String type; // one-to-many, etc.
    private Long sourceEntityId;
    private Long targetEntityId;
    private String description;
    private LocalDateTime createTime;
}
