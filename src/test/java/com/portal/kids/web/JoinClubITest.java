package com.portal.kids.web;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.repository.ClubRepository;
import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import com.portal.kids.membership.model.UserClub;
import com.portal.kids.membership.repository.UserClubRepository;
import com.portal.kids.membership.service.UserClubService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import com.portal.kids.web.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class JoinClubITest {

    @Autowired
    private UserService userService;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private UserClubRepository userClubRepository;

    @Autowired
    private UserClubService userClubService;

    @Test
    @Transactional
    void shouldJoinUserToClub_success() {

        RegisterRequest registerRequest = new RegisterRequest("test1", "123123", "test1@gmail.com", Location.VARNA);
        User user = userService.register(registerRequest);
        assertThat(user).isNotNull();

        Club club = clubRepository.save(
                Club.builder()
                        .name("Test club")
                        .location(Location.ASENOVGRAD)
                        .description("Test club description")
                        .type(ActivityType.EDUCATION)
                        .createdOn(LocalDate.of(2025, 12, 1))
                        .updatedOn(LocalDate.of(2025, 12, 2))
                        .build()
        );

        LocalDateTime beforeJoin = LocalDateTime.now();
        userClubService.joinUserToClub(user.getId(), club.getId());
        LocalDateTime afterJoin = LocalDateTime.now();

        Optional<UserClub> userClubsAfter = userClubRepository.findByUserAndClub(user, club);
        assertThat(userClubsAfter).isPresent();

        UserClub userClub = userClubsAfter.get();
        assertThat(userClub.getUser().getId()).isEqualTo(user.getId());
        assertThat(userClub.getClub().getId()).isEqualTo(club.getId());
        assertThat(userClub.isActive()).isTrue();
        assertThat(userClub.getJoinedAt()).isBetween(beforeJoin, afterJoin);
    }

    @Test
    @Transactional
    void shouldThrowException_whenUserAlreadyJoinedClub() {

        RegisterRequest registerRequest = new RegisterRequest("test1", "123123", "test1@gmail.com", Location.VARNA);
        User user = userService.register(registerRequest);

        Club club = clubRepository.save(
                Club.builder()
                        .name("Test club")
                        .location(Location.ASENOVGRAD)
                        .description("Test club description")
                        .type(ActivityType.EDUCATION)
                        .createdOn(LocalDate.of(2025, 12, 1))
                        .updatedOn(LocalDate.of(2025, 12, 2))
                        .build()
        );

        userClubService.joinUserToClub(user.getId(), club.getId());

        assertThatThrownBy(() -> userClubService.joinUserToClub(user.getId(), club.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User [%s] is already a member of club [%s]".formatted(user.getUsername(), club.getName()));
    }

    @Test
    @Transactional
    void shouldThrowException_whenUserDoesNotExist() {

        Club club = clubRepository.save(
                Club.builder()
                        .name("Test club")
                        .location(Location.ASENOVGRAD)
                        .description("Test club description")
                        .type(ActivityType.EDUCATION)
                        .createdOn(LocalDate.of(2025, 12, 1))
                        .updatedOn(LocalDate.of(2025, 12, 2))
                        .build()
        );

        UUID invalidUserId = UUID.fromString("2b7a0bb1-14c0-4d06-9e2e-66a1e792b6");

        assertThatThrownBy(() -> userClubService.joinUserToClub(invalidUserId, club.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User with [%s] [%s] does not exists.".formatted("id", invalidUserId));
    }

    @Test
    @Transactional
    void shouldThrowException_whenClubDoesNotExist() {

        RegisterRequest registerRequest = new RegisterRequest("test1", "123123", "test1@gmail.com", Location.VARNA);
        User user = userService.register(registerRequest);

        UUID invalidClubId = UUID.fromString("2b7a0bb1-14c0-4d06-9e2e-66a1e792b6");

        assertThatThrownBy(() -> userClubService.joinUserToClub(user.getId(), invalidClubId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Club with id [%s] does not exist.".formatted(invalidClubId));
    }

}
