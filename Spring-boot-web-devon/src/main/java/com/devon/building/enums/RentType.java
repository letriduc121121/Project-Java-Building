package com.devon.building.enums;

import lombok.Getter;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public enum RentType {
    TANG_TRET("Tầng Trệt"),
    NGUYEN_CAN("Nguyên Căn"),
    NOI_THAT("Nội Thất");

    private final String name;
    RentType(String name) {
        this.name = name;
    }
    public static Map<String,String> getTypeCode(){
        Map<String,String> map=new LinkedHashMap<>();
        for(RentType rentType:RentType.values()){
            map.put(rentType.toString(),rentType.name);
        }
        return map;
    }
}
