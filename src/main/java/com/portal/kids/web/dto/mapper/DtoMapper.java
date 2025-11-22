package com.portal.kids.web.dto.mapper;

import com.portal.kids.club.model.Club;
import com.portal.kids.event.model.Event;
import com.portal.kids.user.model.User;
import com.portal.kids.web.dto.ClubRequest;
import com.portal.kids.web.dto.EventRequest;
import com.portal.kids.web.dto.EditProfileRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static EditProfileRequest fromUser(User user) {
        return EditProfileRequest.builder()
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePicture())
                .location(user.getLocation())
                .build();
    }

    public static ClubRequest fromClub(Club club) {
        return ClubRequest.builder()
                .name(club.getName())
                .description(club.getDescription())
                .location(club.getLocation())
                .picture(club.getPicture())
                .location(club.getLocation())
                .type(club.getType())
                .build();
    }

    public static EventRequest fromEvent(Event event) {


        return EventRequest.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .clubId(event.getClub() != null ? event.getClub().getId() : null)
                .periodicity(event.getPeriodicity())
                .type(event.getType())
                .picture(event.getPicture())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .ageCategory(event.getAgeCategory())
                .maxParticipants(event.getMaxParticipants())
                .requirements(event.getRequirements())
                .build();
    }
}
