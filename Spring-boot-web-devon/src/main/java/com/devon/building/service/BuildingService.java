package com.devon.building.service;

import com.devon.building.entity.BuildingEntity;
import com.devon.building.model.dto.AssignBuildingDTO;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.model.response.BuildingSearchResponse;
import com.devon.building.pagination.PaginationResult;

import java.util.List;

public interface BuildingService {
    PaginationResult<BuildingSearchResponse> searchBuildings(BuildingSearchRequest request, int page, int maxResult, int maxNavigationPage);

    void delete(List<Long> ids);

    BuildingDTO findById(Long id);

    BuildingEntity create(BuildingDTO buildingDTO);

    BuildingEntity update(BuildingDTO buildingDTO);

    ResponseDTO loadStaffByBuildingId(Long id);

    void assignBuilding(AssignBuildingDTO dto);
}
