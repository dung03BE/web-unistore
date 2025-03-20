package com.dung.UniStore.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecycleResponse {
    private Integer id;
    private String deviceType;
    private String deviceCondition;
    private String pickupMethod;
    private String status ;
    private LocalDateTime createdAt;
    private String fullName;
    private String phoneNumber;
    private String imageUrl;

}
