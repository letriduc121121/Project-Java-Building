package com.devon.building.model.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffResponseDTO {
    Long id;
    String username;
    String checked;// nhan vien dang quan ly toa nha
}
