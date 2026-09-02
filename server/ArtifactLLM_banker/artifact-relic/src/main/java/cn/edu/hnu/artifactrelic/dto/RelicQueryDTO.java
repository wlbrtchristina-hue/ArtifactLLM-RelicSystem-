package cn.edu.hnu.artifactrelic.dto;

import lombok.Data;

// RelicQueryDTO.java - 查询参数DTO
@Data
public class RelicQueryDTO {
    private String era;
    private String material;
    private String typeName;
    private Integer relicsTypeId;
    private String discoverySite;
}