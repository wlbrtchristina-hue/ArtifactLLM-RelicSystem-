package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("meta_model")
public class MetaModel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String description;
    private Long creatorId;
    private Integer status; // 0-draft, 1-published
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
