package com.portal.kids.club;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.repository.ClubRepository;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.user.model.User;
import com.portal.kids.web.dto.ClubRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ClubServiceUTest {

    private ClubRepository clubRepository;
    private ClubService clubService;

    @BeforeEach
    void setUp() {
        clubRepository = mock(ClubRepository.class);
        clubService = new ClubService(clubRepository);
    }

    @Test
    void getById_existingId_shouldReturnClub() {
        UUID id = UUID.randomUUID();
        Club club = new Club();
        club.setId(id);

        when(clubRepository.findById(id)).thenReturn(Optional.of(club));

        Club result = clubService.getById(id);

        assertThat(result).isEqualTo(club);
    }

    @Test
    void getById_nonExistingId_shouldThrowException() {
        UUID id = UUID.randomUUID();
        when(clubRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> clubService.getById(id));
        assertThat(exception.getMessage()).contains("Club with id");
    }

    @Test
    void getClubByName_existingName_shouldReturnClub() {
        String name = "Tenis Club";
        Club club = new Club();
        club.setName(name);
        when(clubRepository.findByName(name)).thenReturn(Optional.of(club));

        Club result = clubService.getClubByName(name);

        assertThat(result).isEqualTo(club);
    }

    @Test
    void getClubByName_nonExistingName_shouldThrowException() {
        String name = "NonExistingClub";
        when(clubRepository.findByName(name)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> clubService.getClubByName(name));
        assertThat(exception.getMessage()).contains("Club with name");
    }

    @Test
    void getAllClubs_shouldReturnList() {
        Club club1 = new Club();
        Club club2 = new Club();
        when(clubRepository.findAll()).thenReturn(List.of(club1, club2));

        List<Club> result = clubService.getAllClubs();

        assertThat(result).hasSize(2).containsExactly(club1, club2);
    }

    @Test
    void createClub_shouldSaveClub() {
        User user = new User();
        user.setUsername("TestUser");

        ClubRequest request = new ClubRequest();
        request.setName("Chess Club");
        request.setDescription("Description");
        request.setLocation(null);
        request.setType(null);

        clubService.createClubInternal(request, user);

        ArgumentCaptor<Club> captor = ArgumentCaptor.forClass(Club.class);
        verify(clubRepository).save(captor.capture());

        Club saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Chess Club");
        assertThat(saved.getCreator()).isEqualTo(user);
        assertThat(saved.getDescription()).isEqualTo("Description");
        assertThat(saved.getCreatedOn()).isEqualTo(LocalDate.now());
    }

    @Test
    void updateClub_shouldUpdateExistingClub() {
        UUID id = UUID.randomUUID();
        Club existing = new Club();
        existing.setId(id);
        existing.setName("Old Name");

        ClubRequest request = new ClubRequest();
        request.setName("New Name");
        request.setDescription("New Description");
        request.setLocation(null);
        request.setType(null);
        request.setPicture(null);

        when(clubRepository.findById(id)).thenReturn(Optional.of(existing));

        clubService.updateClub(id, request);

        ArgumentCaptor<Club> captor = ArgumentCaptor.forClass(Club.class);
        verify(clubRepository).save(captor.capture());

        Club saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("New Name");
        assertThat(saved.getDescription()).isEqualTo("New Description");
    }
}
