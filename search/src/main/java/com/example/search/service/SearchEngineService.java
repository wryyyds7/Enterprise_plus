package com.example.search.service;

import com.example.common.domain.entity.Enterprise;
import com.example.common.domain.entity.Position;

import java.util.List;
import java.util.Map;

public interface SearchEngineService {
    Enterprise searchEnterpriseByName(String name);
    List<Position> searchPositionInWebsite(String websiteUrl, String enterpriseName, Long enterpriseId);

}
