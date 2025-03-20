package com.dung.UniStore.dto.request;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecycleRequest {
    private Integer id;
    private String deviceType;
    private String deviceCondition;
    private String pickupMethod;
    private String status;
    private String imageUrl;
}
