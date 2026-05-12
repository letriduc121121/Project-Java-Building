package com.devon.building.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CustomerDTO extends AbstractDTO {
    @NotBlank(message = "Tên khách hàng không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private String email;
    private String companyName;

    @NotBlank(message = "Nhu cầu không được để trống")
    private String demand;

    private String status;
    private List<TransactionDTO> transactions;
}
