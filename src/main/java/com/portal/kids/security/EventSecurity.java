package com.portal.kids.security;

import com.portal.kids.event.repository.EventRepository;
import com.portal.kids.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventSecurity {

    private final EventRepository eventRepository;
    private final UserService userService;

    public boolean isCreator(UUID eventId, UUID userId) {
        return eventRepository.findById(eventId)
                .map(e -> e.getCreator().equals(userService.getById(userId)))
                .orElse(false);
    }
}
