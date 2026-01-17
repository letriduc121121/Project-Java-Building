package com.devon.building.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min=1,message = "")
    private List<Long> staffIds;

}
