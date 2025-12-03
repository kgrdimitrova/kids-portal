package com.portal.kids.membership.service;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.membership.model.UserClub;
import com.portal.kids.membership.model.UserClubId;
import com.portal.kids.membership.repository.UserClubRepository;
import com.portal.kids.user.model.User;
import com.portal.kids.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserClubService {

    private final UserClubRepository userClubRepository;
    private final UserService userService;
    private final ClubService clubService;


    @Transactional
    public void joinUserToClub(UUID userId, UUID clubId) {

        User user = userService.getById(userId);

        Club club = clubService.getById(clubId);

        Optional<UserClub> existing = userClubRepository.findByUserAndClub(user, club);
        if (existing.isPresent()) {
            throw new RuntimeException("User [%s] is already a member of club [%s]".formatted(user.getUsername(), club.getName()));
        }

        UserClub userClub = UserClub.builder()
                .id(new UserClubId(user.getId(), club.getId()))
                .user(user)
                .club(club)
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();

        userClubRepository.save(userClub);
    }

    @Transactional
    public void removeUserFromClub(UUID userId, UUID eventId) {
        UserClub userClub = getUserClub(userService.getById(userId), clubService.getById(eventId));
        userClubRepository.deleteById(userClub.getId());
    }

    public List<Club> getUserClubs(User user) {
        return userClubRepository.findClubsByUser(user);
    }

    public List<User> getClubUsers(Club club) {
        return userClubRepository.findUsersByClub(club);
    }

    public UserClub getUserClub(User user, Club club) {
        return userClubRepository.findByUserAndClub(user, club).orElseThrow(()->new RuntimeException("User [%s] is not a member to this club [%s]".formatted(user.getUsername(), club.getName())));
    }
}
