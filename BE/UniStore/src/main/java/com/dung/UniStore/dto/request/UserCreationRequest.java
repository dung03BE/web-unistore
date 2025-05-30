package com.dung.UniStore.dto.request;

import com.dung.UniStore.validator.DobConstraint;
import com.dung.UniStore.validator.DobConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreationRequest {

    @JsonProperty("fullName")
    private String fullName;
    @Size(min = 4,message = "USERNAME_INVALID")


    private String phoneNumber;
    private String address;

    @JsonProperty("old_password")
    private String oldPassword;
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;

    @DobConstraint(min = 10, message = "INVALID_DOB")

    private Date dateOfBirth;
    @JsonProperty("facebook_account_id")
    private int facebookAccountId;
    @JsonProperty("google_account_id")
    private int googleAccountId;
    @JsonProperty("role_id")
    private int roleId;
    private String email;
    @JsonProperty("recaptcha")
    private String recaptcha;
}
