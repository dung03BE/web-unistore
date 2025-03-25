package com.dung.UniStore.service;


import com.dung.UniStore.dto.response.AnnouncementResponse;
import com.dung.UniStore.entity.Announcement;
import com.dung.UniStore.entity.User;
import com.dung.UniStore.entity.UserAnnouncement;
import com.dung.UniStore.exception.ApiException;
import com.dung.UniStore.exception.AppException;
import com.dung.UniStore.mapper.AnnouncementMapper;
import com.dung.UniStore.repository.IAnnouncementRepository;
import com.dung.UniStore.repository.IUserAnnouncementRepository;
import com.dung.UniStore.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAnnouncementService {
    private final IUserAnnouncementRepository userAnnouncementRepository;
    private final IUserRepository userRepository;
    private final IAnnouncementRepository announcementRepository;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE','ROLE_USER')")
    public void assignAnnouncementToUser(Integer userId, Integer announcementId) throws ApiException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        if (userAnnouncementRepository.existsByUserIdAndAnnouncementId(userId, announcementId)) {
            throw new ApiException("User đã có thông báo này, không thể thêm lại!");
        }
        UserAnnouncement userAnnouncement = new UserAnnouncement();
        userAnnouncement.setUser(user);
        userAnnouncement.setAnnouncement(announcement);
        userAnnouncement.setIsRead(false);
        userAnnouncementRepository.save(userAnnouncement);


    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE','ROLE_USER')")
    public List<AnnouncementResponse> getAnnouncementsForUser(Integer userId) {
        List<UserAnnouncement> userAnnouncements = userAnnouncementRepository.findByUserId(userId);
        Collections.sort(userAnnouncements, Comparator.comparing(UserAnnouncement::getCreatedAt).reversed());
        return AnnouncementMapper.INSTANCE.toDtoList(userAnnouncements);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_EMPLOYEE','ROLE_USER')")
    public void updateStatusAnnounce(Long userId) {
        List<UserAnnouncement> userAnnouncements = userAnnouncementRepository.findByUserId(Math.toIntExact(userId));
        for (UserAnnouncement u : userAnnouncements) {
            if (u.getIsRead() == false)
                u.setIsRead(true);
            userAnnouncementRepository.save(u);
        }
    }
}