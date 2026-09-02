package cn.edu.hnu.artifactrelic.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MyRelicVO {
    private Long id;                // 文物ID
    private String relicsName;      // 文物名称
    private String typeName;        // 文物类型名称
    private String era;             // 年代
    private String material;        // 材质
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;  // 创建时间
}