package com.dung.UniStore.dto.response;

import com.dung.UniStore.entity.Role;
import com.dung.UniStore.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    int id;
    String phoneNumber;
    String fullName;
    String address;
    Date dateOfBirth;
    private int roleId;
    private String email;
    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getPhoneNumber(),
                user.getFullName(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getRole().getId(), // Tránh vòng lặp vô hạn khi serialize
                user.getEmail()
        );
    }
}