package com.devon.building.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class BuildingSearchRequest {
    //16 field timf kieem
    Long id;
    String name;
    Long floorArea;
    String districtCode;
    String district;
    String ward;
    String street;
    Long numberOfBasement;
    String direction;
    Long level;
    Long rentAreaFrom;
    Long rentAreaTo;
    Long rentPriceFrom;
    Long rentPriceTo;
    String managerName;
    String managerPhone;
    Long staffId;
    List<String> typeCode;
    List<Long> rentAreas;
}
