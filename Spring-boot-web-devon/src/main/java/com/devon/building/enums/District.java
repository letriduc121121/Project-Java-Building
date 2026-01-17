package com.devon.building.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public enum District {
    QUAN_1("Quận 1"),
    QUAN_2("Quận 2"),
    QUAN_3("Quận 3"),
    QUAN_4("Quận 4"),
    QUAN_10("Quận 10");
    // ham khoi tao
    private final String districtName;
    District(String districtName){
        this.districtName = districtName;
    }
    public static Map<String,String> getDistrict(){
        Map<String,String> map = new LinkedHashMap<>();
        for(District d: District.values()){
            map.put(d.toString(),d.districtName);
        }
        return  map;
    }
}
