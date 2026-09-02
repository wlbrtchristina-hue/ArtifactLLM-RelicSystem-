package cn.edu.hnu.artifactrelic.mapper;

import cn.edu.hnu.artifactrelic.dto.RelicQueryDTO;
import cn.edu.hnu.artifactrelic.entity.CulturalRelics;

import cn.edu.hnu.artifactrelic.vo.RelicsBasicVO;
import cn.edu.hnu.artifactrelic.vo.RelicsDetailVO;
import cn.edu.hnu.artifactrelic.vo.SimpleRelationVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CulturalRelicsMapper extends BaseMapper<CulturalRelics>{

    /**
     * 条件查询文物（分页）
     * 注意：此方法不包含图片，图片在Service层单独处理
     */
    Page<RelicsBasicVO> selectByCondition(Page<RelicsBasicVO> page, @Param("query") RelicQueryDTO queryDTO);

    /**
     * 批量查询文物的图片
     * 通过文物ID列表，批量获取每个文物对应的图片
     * 这样可以减少数据库查询次数
     */
    @Select("<script>" +
            "SELECT relics_id, GROUP_CONCAT(resource_content) as images " +
            "FROM relic_multi_mode " +
            "WHERE resource_type = 'image' " +
            "AND is_deleted = 0 " +
            "AND relics_id IN " +
            "<foreach item='id' collection='relicIds' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "GROUP BY relics_id" +
            "</script>")
    List<Map<String, Object>> getRelicImagesBatch(@Param("relicIds") List<Integer> relicIds);

    /**
     * 获取单个文物的图片
     * 备用：如果需要单独查询某个文物的图片可以使用此方法
     */
    @Select("SELECT resource_content " +
            "FROM relic_multi_mode " +
            "WHERE relics_id = #{relicsId} " +
            "AND resource_type = 'image' " +
            "AND is_deleted = 0 " +
            "ORDER BY created_at")
    List<String> getRelicImages(@Param("relicsId") Integer relicsId);

    @Select("SELECT " +
            "cr.*, " +
            "rt.type_name, rt.description as type_description, " +
            "u.username as creator_name " +
            "FROM cultural_relics cr " +
            "LEFT JOIN relics_type rt ON cr.relics_type_id = rt.relics_type_id AND rt.is_deleted = 0 " +
            "LEFT JOIN sys_user u ON cr.created_by = u.id " +
            "WHERE cr.relics_id = #{relicsId} AND cr.is_deleted = 0")
    @Results(id = "relicDetailMap", value = {
            @Result(property = "relicsId", column = "relics_id", id = true),
            @Result(property = "relicsName", column = "relics_name"),
            @Result(property = "era", column = "era"),
            @Result(property = "material", column = "material"),
            @Result(property = "discoverySite", column = "discovery_site"),
            @Result(property = "currentLocation", column = "current_location"),
            @Result(property = "description", column = "description"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "relicsTypeId", column = "relics_type_id"),
            @Result(property = "typeName", column = "type_name"),
            @Result(property = "typeDescription", column = "type_description"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "creatorName", column = "creator_name"),
            // 关键：添加 customFields 的映射
            @Result(property = "customFields", column = "custom_fields",
                    typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    })
    RelicsDetailVO selectRelicDetailById(@Param("relicsId") Integer relicsId);
    /**
     * 查询文物图片
     */
    @Select("SELECT resource_content " +
            "FROM relic_multi_mode " +
            "WHERE relics_id = #{relicsId} " +
            "AND resource_type = 'image' " +
            "AND is_deleted = 0 " +
            "ORDER BY created_at")
    List<String> selectRelicImages(@Param("relicsId") Integer relicsId);

    /**
     * 查询文物的关系（简化版）
     */
    @Select("SELECT " +
            "relation_id, relation_name, target_string, relation_description, created_at " +
            "FROM entity_relations " +
            "WHERE source_type = 'RELIC' " +
            "AND source_id = #{relicsId} " +
            "AND is_deleted = 0 " +
            "ORDER BY created_at DESC")
    List<SimpleRelationVO> selectRelicRelations(@Param("relicsId") Integer relicsId);

    // 根据类型ID查询文物数量
    @Select("SELECT COUNT(*) FROM cultural_relics WHERE relics_type_id = #{typeId} AND is_deleted = 0")
    Integer selectCountByTypeId(@Param("typeId") Long typeId);

    // 根据类型ID查询所有文物
    @Select("SELECT * FROM cultural_relics WHERE relics_type_id = #{typeId} AND is_deleted = 0")
    List<CulturalRelics> selectByTypeId(@Param("typeId") Long typeId);

    Page<RelicsBasicVO> semanticSearch(Page<RelicsBasicVO> page, @Param("tokens") List<String> tokens);

    List<RelicsBasicVO> selectByNameExact(@Param("name") String name);

    Page<RelicsBasicVO> selectRelatedByTypeOrEra(Page<RelicsBasicVO> page,
                                                 @Param("typeName") String typeName,
                                                 @Param("era") String era,
                                                 @Param("excludeIds") List<Integer> excludeIds);
}
