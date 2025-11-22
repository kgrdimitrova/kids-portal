package com.portal.kids.club.service;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.repository.ClubRepository;
import com.portal.kids.user.model.User;
import com.portal.kids.web.dto.ClubRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ClubService {

    private final ClubRepository clubRepository;

    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @PreAuthorize("hasAuthority('CREATE_CLUB') and @clubSecurity.isCreator(#clubId, authentication.principal.userId)")
    public void createClub(ClubRequest createClubRequest, User user) {
        Club club = Club.builder()
                .name(createClubRequest.getName())
                .description(createClubRequest.getDescription())
                .location(createClubRequest.getLocation())
                .type(createClubRequest.getType())
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .creator(user)
                .build();

        clubRepository.save(club);
        log.info("A club [{}] is created by the user [{}]", club.getName(), user.getUsername());
    }

    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    public Club getClubByName(String name) {
        return clubRepository.findByName(name);
    }

    public Club getById(UUID id) {
        return clubRepository.findById(id).orElseThrow(()->new RuntimeException("Club with username [%s] does not exists.".formatted(id)));
    }

    @PreAuthorize("hasAuthority('EDIT_CLUB') and @clubSecurity.isCreator(#clubId, authentication.principal.userId)")
    public void updateClub(UUID id, ClubRequest clubRequest) {

        Club club = getById(id);
        club.setName(clubRequest.getName());
        club.setDescription(clubRequest.getDescription());
        club.setLocation(clubRequest.getLocation());
        club.setType(clubRequest.getType());
        club.setPicture(clubRequest.getPicture());
        club.setUpdatedOn(LocalDateTime.now());
        clubRepository.save(club);
    }
}
