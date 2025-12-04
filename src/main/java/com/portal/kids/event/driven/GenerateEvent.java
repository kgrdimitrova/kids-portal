package com.portal.kids.event.driven;

import com.portal.kids.common.Status;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateEvent {

    private UUID eventId;

    private LocalDate startDate;

    private LocalDate createdOn;

    private Status status;
}
