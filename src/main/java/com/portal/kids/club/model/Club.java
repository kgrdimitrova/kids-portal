package com.portal.kids.club.model;

import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import com.portal.kids.event.model.Event;
import com.portal.kids.membership.model.UserClub;
import com.portal.kids.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Location location;

    @URL
    private String picture;

    @ManyToOne
    private User creator;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    private LocalDateTime updatedOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType type;

//    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<User> trainers;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserClub> userClubs = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "club")
    private List<Event> events = new ArrayList<>();
}