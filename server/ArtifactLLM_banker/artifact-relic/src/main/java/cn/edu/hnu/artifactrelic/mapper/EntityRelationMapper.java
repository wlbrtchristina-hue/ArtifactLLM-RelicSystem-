package cn.edu.hnu.artifactrelic.mapper;

import cn.edu.hnu.artifactrelic.entity.EntityRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EntityRelationMapper extends BaseMapper<EntityRelation> {
    @Update("UPDATE entity_relations SET is_deleted = 1, updated_at = NOW() WHERE source_id = #{relicsId}")
    void logicalDeleteByRelicId(@Param("relicsId") Long relicId);

    /**
     * 逻辑删除指定实体的所有关系
     */
    @Update("UPDATE entity_relations " +
            "SET is_deleted = 1, updated_at = NOW() " +
            "WHERE source_type = #{sourceType} AND source_id = #{sourceId}")
    void deleteBySource(@Param("sourceType") String sourceType,
                        @Param("sourceId") Long sourceId);

    /**
     * 批量逻辑删除关系
     */
    @Update("<script>" +
            "UPDATE entity_relations " +
            "SET is_deleted = 1, updated_at = NOW() " +
            "WHERE relation_id IN " +
            "<foreach item='id' collection='relationIds' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "</script>")
    void batchDelete(@Param("relationIds") List<Long> relationIds);

    /**
     * 查询实体的所有关系
     */
    @Select("SELECT * FROM entity_relations " +
            "WHERE source_type = #{sourceType} " +
            "  AND source_id = #{sourceId} " +
            "  AND is_deleted = 0")
    List<EntityRelation> selectBySource(@Param("sourceType") String sourceType,
                                        @Param("sourceId") Long sourceId);
}
