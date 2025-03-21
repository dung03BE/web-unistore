package com.dung.UniStore.controller;

import com.dung.UniStore.dto.response.AnnouncementResponse;
import com.dung.UniStore.entity.UserAnnouncement;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.service.UserAnnouncementService;
import com.dung.UniStore.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/user-announcements")
@RequiredArgsConstructor
public class UserAnnouncementController {
    private final UserAnnouncementService userAnnouncementService;
    private final AuthUtil authUtil;
    @PostMapping("/assign")
    public ResponseEntity<String> assignAnnouncement(@RequestParam Integer announcementId) throws ApiException {
        Long userId = authUtil.loggedInUserId();
        userAnnouncementService.assignAnnouncementToUser(Math.toIntExact(userId), announcementId);
        return ResponseEntity.ok("Announcement assigned successfully.");
    }

    @GetMapping("/userId")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements() {
        Long userId = authUtil.loggedInUserId();
        return ResponseEntity.ok(userAnnouncementService.getAnnouncementsForUser(Math.toIntExact(userId)));
    }
    @PutMapping("/isRead")
    public ResponseEntity<String> updateStatusAnnounce() {
        Long userId = authUtil.loggedInUserId();
        userAnnouncementService.updateStatusAnnounce((long) Math.toIntExact(userId));
        return ResponseEntity.ok("Announcement assigned successfully.");
    }
}