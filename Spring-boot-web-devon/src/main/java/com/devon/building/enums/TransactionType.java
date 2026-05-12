package com.devon.building.enums;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum TransactionType {
    CSKH("Chăm sóc khách hàng"),
    DDX("Dẫn đi xem");

    private final String transactionName;

    TransactionType(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public static Map<String, String> getTransactionType() {
        Map<String, String> map = new LinkedHashMap<>();
        for (TransactionType t : TransactionType.values()) {
            map.put(t.name(), t.transactionName);
        }
        return map;
    }
}
