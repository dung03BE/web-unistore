package com.dung.UniStore.dto.response;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CounponResponse {
    private Long id;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer userLimit;
    private Long userId; // có thể null nếu public
}