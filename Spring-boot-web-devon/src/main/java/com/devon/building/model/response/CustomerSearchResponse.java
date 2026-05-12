package com.devon.building.model.response;

import com.devon.building.enums.Status;
import com.devon.building.model.dto.AbstractDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerSearchResponse extends AbstractDTO {
    String fullName;
    String phone;
    String email;
    String demand;
    String status;
    String createdByName;
    Date createdDate;

    // Constructor dùng trong JPQL query
    public CustomerSearchResponse(Long id, String fullName, String phone, String email,
                                  String demand, String status, String createdByName, Date createdDate) {
        this.setId(id);
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.demand = demand;
        this.status = Status.getLabel(status);
        this.createdByName = createdByName;
        this.createdDate = createdDate;
    }
}
