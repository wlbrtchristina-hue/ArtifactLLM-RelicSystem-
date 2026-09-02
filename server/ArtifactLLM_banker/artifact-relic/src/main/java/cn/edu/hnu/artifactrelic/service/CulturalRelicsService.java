package cn.edu.hnu.artifactrelic.service;

import java.util.List;
import java.util.Map;

import cn.edu.hnu.artifactrelic.dto.RelicQueryDTO;
import cn.edu.hnu.artifactrelic.entity.CulturalRelics;

import cn.edu.hnu.artifactrelic.vo.RelicsBasicVO;
import cn.edu.hnu.artifactrelic.vo.RelicsDetailVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

// CulturalRelicsService.java
public interface CulturalRelicsService extends IService<CulturalRelics> {

    // 筛选获取所有文物简单信息
    Page<RelicsBasicVO> getRelicsByCondition(RelicQueryDTO queryDTO, Page<RelicsBasicVO> page);

    /**
     * 批量查询文物图片（优化性能）
     *
     @param relicIds 文物ID列表
      * @return 文物ID到图片列表的映射
     */
    Map<Integer, List<String>> getRelicImagesMap(List<Integer> relicIds);

    RelicsDetailVO getRelicDetail(Integer relicsId) throws Exception;

    Page<RelicsBasicVO> searchSemantic(String q, Page<RelicsBasicVO> page);

    List<RelicsBasicVO> findByNameExact(String name);

    Page<RelicsBasicVO> findRelatedByPrimary(RelicsBasicVO primary, Page<RelicsBasicVO> page);
}
