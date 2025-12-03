package com.portal.kids.membership.repository;

import com.portal.kids.club.model.Club;
import com.portal.kids.membership.model.UserClub;
import com.portal.kids.membership.model.UserClubId;
import com.portal.kids.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserClubRepository extends JpaRepository<UserClub, UUID> {

    @Query("SELECT s.club FROM UserClub s WHERE s.user = :user")
    List<Club> findClubsByUser(@Param("user") User user);

    @Query("SELECT s.user FROM UserClub s WHERE s.club = :club")
    List<User> findUsersByClub(@Param("club") Club club);

    void deleteById(UserClubId userClubId);

    Optional<UserClub> findByUserAndClub(User user, Club club);
}
