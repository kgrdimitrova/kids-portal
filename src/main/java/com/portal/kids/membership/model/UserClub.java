package com.portal.kids.membership.model;

import com.portal.kids.club.model.Club;
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
public class UserClub {

    @EmbeddedId
    private UserClubId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clubId")
    @JoinColumn(name = "club_id")
    private Club club;

    private LocalDateTime joinedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private MembershipRole role = MembershipRole.MEMBER;

    private boolean active = true;
}
