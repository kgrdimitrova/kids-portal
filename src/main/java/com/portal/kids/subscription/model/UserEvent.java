package com.portal.kids.subscription.model;

import com.portal.kids.event.model.Event;
import com.portal.kids.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserEvent {

        @EmbeddedId
        private UserEventId id;

        @ManyToOne(fetch = FetchType.LAZY)
        @MapsId("userId")
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne(fetch = FetchType.LAZY)
        @MapsId("eventId")
        @JoinColumn(name = "event_id")
        private Event event;

        private LocalDateTime subscribedOn;

        @Enumerated(EnumType.STRING)
        private Status status;

    public UserEvent(User user, Event event) {
        this.user = user;
        this.event = event;
        this.id = new UserEventId(user.getId(), event.getId());
    }
}
