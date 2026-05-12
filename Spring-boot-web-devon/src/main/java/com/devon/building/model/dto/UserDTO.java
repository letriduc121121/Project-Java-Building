package com.devon.building.model.dto;

import com.devon.building.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO extends AbstractDTO {
    @NotBlank(message = "UserName is required")
    private String userName;

    @JsonProperty("fullname")
    @NotBlank(message = "FullName is required")
    private String fullName;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @JsonProperty("retype_password")
    private String retypePassword;

    private Integer status;

    private MultipartFile fileData;

    private Map<String, String> roleDTO;

    private String roleCode;

    private String phone;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String address;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

//    @JsonProperty("facebook_account_id")
//    private int facebookAccountId;
//
//    @JsonProperty("google_account_id")
//    private int googleAccountId;

    @JsonProperty("role_id")
    private Long roleId;

    private String base64Image;
    private String imageName;

    public void initRoles() {
        this.roleDTO = Arrays.stream(UserRole.values()).collect(Collectors.toMap(UserRole::getCode, UserRole::getLabel));
    }
}
