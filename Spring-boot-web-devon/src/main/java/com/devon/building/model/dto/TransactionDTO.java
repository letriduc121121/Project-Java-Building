package com.devon.building.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO extends AbstractDTO {

    @NotNull(message = "Id khách hàng không được để trống")
    private Long customerId;

    @NotNull(message = "Chi tiết giao dịch không được để trống")
    private String note;

    @NotNull(message = "Mã giao dịch không được để trống")
    private String code;
}
