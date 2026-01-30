package com.devon.building.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.devon.building.entity.BuildingEntity;
import com.devon.building.entity.RentAreaEntity;
import com.devon.building.enums.District;
import com.devon.building.exception.InvalidBuildingException;
import com.devon.building.model.response.BuildingSearchResponse;
import com.devon.building.model.dto.BuildingDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BuildingConvertor {

    @Autowired
    private ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public BuildingSearchResponse convertToBuildingResponseDTO(BuildingEntity entity) {
        // 1.
        if (modelMapper.getTypeMap(BuildingEntity.class, BuildingSearchResponse.class) == null) {
            modelMapper.createTypeMap(BuildingEntity.class, BuildingSearchResponse.class)
                    .addMappings(m -> m.skip(BuildingSearchResponse::setRentArea));
        }

        // 2. Dùng ModelMapper
        BuildingSearchResponse dto = modelMapper.map(entity, BuildingSearchResponse.class);

        // 3. địa chỉ
        dto.setAddress(entity.getStreet() + ", " + entity.getWard() + ", " +
                District.getDistrict().getOrDefault(entity.getDistrict(), ""));

        // 4. diện tích
        List<RentAreaEntity> rentAreaEntities = entity.getRentAreaEntities();
        if (rentAreaEntities != null) {
            List<Long> rentAreas = rentAreaEntities.stream().map(RentAreaEntity::getValue).collect(Collectors.toList());
            dto.setRentArea(rentAreas);
        }

        return dto;
    }

    public BuildingEntity toBuildingEntity(BuildingDTO buildingDTO) {
        BuildingEntity buildingEntity;
        if (buildingDTO.getId() != null) {
            buildingEntity = entityManager.find(BuildingEntity.class, buildingDTO.getId());
            if (buildingEntity == null) {
                throw new InvalidBuildingException("Building not found with ID: " + buildingDTO.getId());
            }
        } else {
            buildingEntity = new BuildingEntity();
        }
        modelMapper.map(buildingDTO, buildingEntity);
        if (buildingDTO.getTypeCode() != null && !buildingDTO.getTypeCode().isEmpty()) {
            buildingEntity.setType(String.join(",", buildingDTO.getTypeCode()));
        }
        return buildingEntity;
    }
}
