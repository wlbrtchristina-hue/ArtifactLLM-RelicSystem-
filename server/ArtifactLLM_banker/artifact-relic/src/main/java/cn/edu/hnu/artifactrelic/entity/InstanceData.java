package cn.edu.hnu.artifactrelic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("instance_data")
public class InstanceData {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long modelId;
    private Long entityDefId;
    private String name;
    private String dataJson; // JSON string
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
