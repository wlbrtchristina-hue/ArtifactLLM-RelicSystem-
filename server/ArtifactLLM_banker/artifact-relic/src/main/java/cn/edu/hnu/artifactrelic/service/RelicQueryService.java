package cn.edu.hnu.artifactrelic.service;

import java.util.List;

public interface RelicQueryService {
    List<String> getAllEras();

    List<String> getAllMaterials();

    List<String> getAllTypes();

    List<String> getAllSites();
}
