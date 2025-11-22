package com.portal.kids.event.model;

import com.portal.kids.club.model.Club;
import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import com.portal.kids.subscription.model.UserEvent;
import com.portal.kids.user.model.User;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventPeriodicity periodicity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgeCategory ageCategory;

    private String picture;

    @Column(nullable = false)
    private LocalDate startDate;

    //@Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String requirements;

    private int maxParticipants;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @ManyToOne
    private User creator;

    //@Column(nullable = false)
    private BigDecimal pass;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserEvent> userEvents = new ArrayList<>();

    @ManyToOne
    private Club club;
}