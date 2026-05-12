package com.devon.building.model.dto;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssignBuildingDTO {
    @NotNull(message = "Building Id is required")
    private Long buildingId;
    private List<Long> staffIds;

}
