package com.dung.UniStore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementResponse {
    private int id;
    private String title;
    private String content;
    private Boolean isRead; // Trạng thái đã đọc
    private Timestamp createdAt;

}
