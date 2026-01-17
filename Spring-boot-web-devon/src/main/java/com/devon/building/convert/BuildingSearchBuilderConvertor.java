package com.devon.building.convert;

import com.devon.building.builder.BuildingSearchBuilder;
import com.devon.building.utils.MapUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BuildingSearchBuilderConvertor {
    public BuildingSearchBuilder toBuildingSearchBuilder(Map<String, String> params, List<String> typeCode) {

        BuildingSearchBuilder buildingSearchBuilder = new BuildingSearchBuilder.Builder()
                .setName(MapUtil.getObject(params, "name", String.class))
                .setFloorArea(MapUtil.getObject(params, "floorArea", Long.class))
                .setDistrictCode(MapUtil.getObject(params, "districtCode", String.class))
                .setWard(MapUtil.getObject(params, "ward", String.class))
                .setStreet(MapUtil.getObject(params, "street", String.class))
                .setNumberOfBasement(MapUtil.getObject(params, "numberOfBasement", Long.class))
                .setDirection(MapUtil.getObject(params, "direction", String.class))
                .setLevel(MapUtil.getObject(params, "level", String.class))
                .setRentAreaFrom(MapUtil.getObject(params, "rentAreaFrom", Long.class))
                .setRentAreaTo(MapUtil.getObject(params, "rentAreaTo", Long.class))
                .setRentPriceFrom(MapUtil.getObject(params, "rentPriceFrom", Long.class))
                .setRentPriceTo(MapUtil.getObject(params, "rentPriceTo", Long.class))
                .setManagerName(MapUtil.getObject(params, "managerName", String.class))
                .setManagerPhone(MapUtil.getObject(params, "managerPhone", String.class))
                .setStaffId(MapUtil.getObject(params, "staffId", Long.class))
                .setTypeCode(typeCode)
                .build();

        return buildingSearchBuilder;
    }
}
