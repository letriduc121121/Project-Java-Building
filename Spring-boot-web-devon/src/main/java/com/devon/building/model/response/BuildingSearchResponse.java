package com.devon.building.model.response;

import com.devon.building.model.dto.AbstractDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingSearchResponse extends AbstractDTO {
    String name;
    String address;
    Long numberOfBasement;
    String managerName;
    String managerPhone;
    Long floorArea;
    List<Long> rentArea;
    String emptyArea;
    Long rentPrice;
    String serviceFee;
    Double brokerageFee;

}
