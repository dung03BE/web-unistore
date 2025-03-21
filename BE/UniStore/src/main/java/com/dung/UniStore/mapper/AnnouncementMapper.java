package com.dung.UniStore.mapper;

import com.dung.UniStore.dto.response.AnnouncementResponse;
import com.dung.UniStore.entity.UserAnnouncement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    AnnouncementMapper INSTANCE = Mappers.getMapper(AnnouncementMapper.class);

    @Mapping(source = "announcement.id", target = "id")
    @Mapping(source = "announcement.title", target = "title")
    @Mapping(source = "announcement.content", target = "content")
    @Mapping(source = "isRead", target = "isRead")
    @Mapping(source = "announcement.createdAt", target = "createdAt")
    AnnouncementResponse toDto(UserAnnouncement userAnnouncement);

    List<AnnouncementResponse> toDtoList(List<UserAnnouncement> userAnnouncements);
}
