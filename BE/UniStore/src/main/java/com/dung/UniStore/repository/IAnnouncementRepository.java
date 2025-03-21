package com.dung.UniStore.repository;

import com.dung.UniStore.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAnnouncementRepository  extends JpaRepository<Announcement, Integer> {
}
