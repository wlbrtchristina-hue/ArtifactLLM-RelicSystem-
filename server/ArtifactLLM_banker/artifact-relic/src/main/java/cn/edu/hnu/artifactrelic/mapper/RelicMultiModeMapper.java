package cn.edu.hnu.artifactrelic.mapper;

import cn.edu.hnu.artifactrelic.entity.RelicMultiMode;
import cn.edu.hnu.artifactrelic.entity.RelicsType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RelicMultiModeMapper extends   BaseMapper<RelicMultiMode> {
    // 逻辑删除指定文物的所有资源
    @Update("UPDATE relic_multi_mode SET is_deleted = 1, updated_at = NOW() WHERE relics_id = #{relicId}")
    void logicalDeleteByRelicId(@Param("relicId") Long relicId);

    @Update("UPDATE relic_multi_mode SET is_deleted = 1, updated_at = NOW() " +
            "WHERE relics_id = #{relicId} AND resource_content = #{imageUrl} AND is_deleted = 0")
    int logicalDeleteByUrl(@Param("relicId") Long relicId, @Param("imageUrl") String imageUrl);
}
