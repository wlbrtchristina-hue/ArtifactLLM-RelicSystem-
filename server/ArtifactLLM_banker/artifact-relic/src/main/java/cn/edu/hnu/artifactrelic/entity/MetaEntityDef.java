package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("meta_entity_def")
public class MetaEntityDef {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long modelId;
    private String name;
    private String code;
    private String description;
    private Integer xPos;
    private Integer yPos;
    private LocalDateTime createTime;
}
