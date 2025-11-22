package com.portal.kids.web.dto;

import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import com.portal.kids.event.model.AgeCategory;
import com.portal.kids.event.model.EventPeriodicity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequest {

    @Size(min = 2, max = 24, message = "Title must be between 2 and 24 symbols")
    @NotBlank
    private String title;

    @Size(min = 2, message = "Description must be more than 2 symbols")
    @NotBlank
    private String description;

    @NotNull
    private Location location;

    @NotNull
    private ActivityType type;

    @NotNull
    private EventPeriodicity periodicity;

    @URL
    private String picture;

    private BigDecimal pass;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    private LocalDate endDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @NotNull
    private LocalTime startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @NotNull
    private LocalTime endTime;

    private AgeCategory ageCategory;

    private String requirements;

    private int maxParticipants;

    private UUID clubId;

    private boolean generateFutureEvents;
}
