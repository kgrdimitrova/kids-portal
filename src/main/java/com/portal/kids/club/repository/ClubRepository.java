package com.portal.kids.club.repository;

import com.portal.kids.club.model.Club;
import com.portal.kids.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClubRepository extends JpaRepository<Club, UUID> {

    Club findByName(String name);
}
