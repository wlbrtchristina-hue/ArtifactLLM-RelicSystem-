package cn.edu.hnu.artifactrelic.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsVO {
    private String name;
    private Long value;
}
