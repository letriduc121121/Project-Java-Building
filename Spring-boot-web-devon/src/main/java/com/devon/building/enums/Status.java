package com.devon.building.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum Status {
    CHUA_XU_LY("Chưa xử lý"),
    DANG_XU_LY("Đang xử lý"),
    DA_XU_LY("Đã xử lý");

    private final String statusName;

    Status(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public static Map<String, String> getStatusMap() {
        Map<String, String> statusMap = new LinkedHashMap<>();
        for (Status status : Status.values()) {
            statusMap.put(status.name(), status.getStatusName());
        }
        return statusMap;
    }

    public static String getLabel(String key) {
        if (key == null) return "";
        for (Status s : Status.values()) {
            if (s.name().equals(key) || s.getStatusName().equals(key)) {
                return s.getStatusName();
            }
        }
        return key;
    }
}
