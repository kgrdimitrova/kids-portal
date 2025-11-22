package com.portal.kids.membership.service;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.repository.ClubRepository;
import com.portal.kids.membership.model.UserClub;
import com.portal.kids.membership.model.UserClubId;
import com.portal.kids.membership.repository.UserClubRepository;
import com.portal.kids.user.model.User;
import com.portal.kids.user.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final UserClubRepository userClubRepository;


    @Transactional
    public UserClub joinUserToClub(UUID userId, UUID clubId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("Club not found: " + clubId));

        // Check if already subscribed
        Optional<UserClub> existing = userClubRepository.findByUserAndClub(user, club);
        if (existing.isPresent()) {
            throw new IllegalStateException("User already joined to this club");
        }

        // Create the link
        UserClub userClub = UserClub.builder()
                .id(new UserClubId(user.getId(), club.getId()))
                .user(user)
                .club(club)
                .joinedAt(LocalDateTime.now())
                .active(true)
                .build();

        return userClubRepository.save(userClub);
    }

    @Transactional
    public void removeUserFromClub(UUID userId, UUID eventId) {
        userClubRepository.deleteByUserIdAndClubId(userId, eventId);
    }

    public List<Club> getUserClubs(User user) {
        return userClubRepository.findClubsByUser(user);
    }

    public List<User> getClubUsers(Club club) {
        return userClubRepository.findUsersByClub(club);
    }
}
