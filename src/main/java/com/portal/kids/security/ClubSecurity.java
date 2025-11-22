package com.portal.kids.security;

import com.portal.kids.club.repository.ClubRepository;
import com.portal.kids.event.repository.EventRepository;
import com.portal.kids.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClubSecurity {

    private final ClubRepository clubRepository;
    private final UserService userService;

    public boolean isCreator(UUID clubId, UUID userId) {
        return clubRepository.findById(clubId)
                .map(e -> e.getCreator().equals(userService.getById(userId)))
                .orElse(false);
    }
}
