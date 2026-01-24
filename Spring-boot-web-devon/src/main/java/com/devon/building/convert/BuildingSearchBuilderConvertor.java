package com.devon.building.convert;

import com.devon.building.builder.BuildingSearchBuilder;
import com.devon.building.model.request.BuildingSearchRequest;
import org.springframework.stereotype.Component;

@Component
public class BuildingSearchBuilderConvertor {

    public BuildingSearchBuilder toBuildingSearchBuilder(BuildingSearchRequest request) {
        return new BuildingSearchBuilder.Builder()
                .setName(request.getName())
                .setFloorArea(request.getFloorArea())
                .setDistrictCode(request.getDistrictCode())
                .setWard(request.getWard())
                .setStreet(request.getStreet())
                .setNumberOfBasement(request.getNumberOfBasement())
                .setDirection(request.getDirection())
                .setLevel(request.getLevel() != null ? String.valueOf(request.getLevel()) : null)
                .setRentAreaFrom(request.getRentAreaFrom())
                .setRentAreaTo(request.getRentAreaTo())
                .setRentPriceFrom(request.getRentPriceFrom())
                .setRentPriceTo(request.getRentPriceTo())
                .setManagerName(request.getManagerName())
                .setManagerPhone(request.getManagerPhone())
                .setStaffId(request.getStaffId())
                .setTypeCode(request.getTypeCode())
                .build();
    }
}
