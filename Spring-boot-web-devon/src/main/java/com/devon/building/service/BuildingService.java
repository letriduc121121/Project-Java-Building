package com.devon.building.service;

import com.devon.building.entity.BuildingEntity;
import com.devon.building.model.dto.AssignBuildingDTO;
import com.devon.building.model.dto.BuildingDTO;
import com.devon.building.model.dto.ResponseDTO;
import com.devon.building.model.request.BuildingSearchRequest;
import com.devon.building.model.response.BuildingSearchResponse;

import java.util.List;
import java.util.Map;

public interface BuildingService {
    List<BuildingSearchResponse> searchBuildings(BuildingSearchRequest request);

    void delete(List<Long > ids);
    
    BuildingDTO findById(Long id);

    BuildingEntity create(BuildingDTO buildingDTO);

    BuildingEntity update(BuildingDTO buildingDTO);

    ResponseDTO loadStaffByBuildingId(Long id);
    
    void assignBuilding(AssignBuildingDTO dto);
}
