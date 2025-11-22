package com.portal.kids.event.driven;

import com.portal.kids.event.model.EventStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEvent {

    private UUID eventId;

    private LocalDate createdOn;

    private EventStatus status;
}
