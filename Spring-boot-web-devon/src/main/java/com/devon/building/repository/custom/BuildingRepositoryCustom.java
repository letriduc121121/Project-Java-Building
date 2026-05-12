package com.devon.building.repository.custom;

import com.devon.building.builder.BuildingSearchBuilder;
import com.devon.building.entity.BuildingEntity;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.pagination.PaginationResult;

import java.util.List;

public interface BuildingRepositoryCustom {
    PaginationResult<BuildingEntity> searchBuildings(BuildingSearchRequest buildingSearchRequest, int page, int maxResult, int maxNavigationPage);
}
