package com.devon.building.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSearchRequest {
    String name;
    String phone;
    String email;
    String demand;
    Long staffId;
    String status;
}
