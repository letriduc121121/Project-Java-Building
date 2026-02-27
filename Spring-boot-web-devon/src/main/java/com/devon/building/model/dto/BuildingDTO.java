package com.devon.building.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BuildingDTO extends AbstractDTO {
    @NotBlank(message = "Name Building is Blank")
    private String name;

    private String street;

    private String ward;

    @NotBlank(message = "District is required")
    private String district;
    private Long numberOfBasement;
    private Long floorArea;
    private String level;

    @Size(min = 1, message = "Type Code is required")
    private List < String > typeCode;

    private String overtimeFee;

    private String electricityFee;

    private String deposit;

    private String payment;

    private String rentTime;

    private String decorationTime;

    private String rentPriceDescription;

    private String carFee;

    private String motoFee;

    private String waterFee;

    private String structure;

    private String direction;

    private String note;

    @NotBlank(message = "Rent area is required")
    private String rentArea;

    @NotBlank(message = "Manager name is required")
    private String managerName;

    @jakarta.validation.constraints.Pattern(regexp = "^\\d{10}$", message = "Manager Phone must be 10 digits")
    private String managerPhone;

    @NotNull(message = "Rent Price is required")
    @Min(value = 0, message = "Rent Price must be a positive integer")
    private Long rentPrice;

    private String serviceFee;

    private String brokerageFee;

    private String linkOfBuilding;

    private String map;

}