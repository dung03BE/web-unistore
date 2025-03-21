package com.dung.UniStore.repository;

import com.dung.UniStore.entity.UserAnnouncement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserAnnouncementRepository extends JpaRepository<UserAnnouncement, Integer> {
        List<UserAnnouncement> findByUserId(Integer userId);

    boolean existsByUserIdAndAnnouncementId(Integer userId, Integer announcementId);
}