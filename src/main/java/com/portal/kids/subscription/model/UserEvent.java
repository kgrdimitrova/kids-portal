package com.portal.kids.subscription.model;

import com.portal.kids.common.Status;
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

        @ManyToOne(fetch = FetchType.EAGER)
        @MapsId("userId")
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne(fetch = FetchType.EAGER)
        @MapsId("eventId")
        @JoinColumn(name = "event_id")
        private Event event;

        private LocalDateTime subscribedOn;

        @Enumerated(EnumType.STRING)
        private Status status;
}
