package com.dung.UniStore.dto.response.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductTypeDTO {
    private String type;
    private int value;
}