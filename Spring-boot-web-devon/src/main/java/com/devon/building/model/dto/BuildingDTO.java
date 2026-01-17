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
public class BuildingDTO  extends  AbstractDTO{
    @NotBlank(message = "Name Building is Blank")
    private String name;
    
    @NotBlank(message = "Street is required")
    private String street;
    
    @NotBlank(message = "Ward is required")
    private String ward;
    
    @NotBlank(message = "District is required")
    private String district;
    
    @NotNull(message = "Number of basement is required")
    @Min(value = 0, message = "Number of basement must be positive")
    private Long numberOfBasement;
    
    @NotNull(message = "Floor area is required")
    @Min(value = 0, message = "Floor area must be positive")
    private Long floorArea;
    
    @NotBlank(message = "Level is required")
    private String level;
    
    @Size(min = 1, message = "Type Code is required")
    private List<String> typeCode;
    
    @NotBlank(message = "Overtime fee is required")
    private String overtimeFee;
    
    @NotBlank(message = "Electricity fee is required")
    private String electricityFee;
    
    @NotBlank(message = "Deposit is required")
    private String deposit;
    
    @NotBlank(message = "Payment is required")
    private String payment;
    
    @NotBlank(message = "Rent time is required")
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
    
    @NotBlank(message = "Service fee is required")
    private String serviceFee;
    
    private String brokerageFee;
    
    @NotBlank(message = "Link of building is required")
    private String linkOfBuilding;
    
    @NotBlank(message = "Map is required")
    private String map;
}
