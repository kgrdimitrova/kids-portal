package com.portal.kids.web;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.common.Status;
import com.portal.kids.event.service.EventService;
import com.portal.kids.membership.service.UserClubService;
import com.portal.kids.security.UserData;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.service.UserService;
import com.portal.kids.web.dto.ClubRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClubController.class)
@AutoConfigureMockMvc
class ClubControllerAPITest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClubService clubService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserClubService userClubService;

    @MockitoBean
    private UserEventService userEventService;

    @MockitoBean
    private EventService eventService;

    private User user;
    private UserData userData;
    private Club club;
    private UUID clubId;

    @BeforeEach
    void setUp() {

        clubId = UUID.randomUUID();

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");
        user.setRole(UserRole.USER);
        user.setPassword("1234");

        userData = new UserData(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                true
        );

        club = Club.builder()
                .id(clubId)
                .name("Test club")
                .creator(user)
                .build();
    }

    // ✅ CREATE CLUB PAGE
    @Test
    void createClubPage_shouldReturnView() throws Exception {

        mockMvc.perform(get("/clubs/create-club")
                        .with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("create-club"))
                .andExpect(model().attributeExists("clubRequest"));
    }

    // ✅ CREATE CLUB - SUCCESS
    @Test
    void createClub_shouldRedirect() throws Exception {

        when(userService.getById(user.getId())).thenReturn(user);

        mockMvc.perform(post("/clubs/create-club")
                        .with(user(userData))
                        .with(csrf())
                        .param("name", "Test Club")
                        .param("description", "Some description")
                        .param("location", "VARNA")
                        .param("type", "EDUCATION"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(clubService).createClub(any(ClubRequest.class), eq(user));
    }

    // ✅ CLUB DETAILS
    @Test
    void clubDetails_shouldReturnUpdatePage() throws Exception {

        when(userService.getById(user.getId())).thenReturn(user);
        when(clubService.getById(clubId)).thenReturn(club);

        mockMvc.perform(get("/clubs/{id}/details", clubId)
                        .with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("update-club"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("clubRequest"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("creator"));
    }

    // ✅ UPDATE CLUB - SUCCESS
    @Test
    void updateClub_shouldRedirect() throws Exception {

        when(clubService.getById(clubId)).thenReturn(club);
        when(userClubService.getClubUsers(club)).thenReturn(List.of());

        mockMvc.perform(put("/clubs/{id}/details", clubId)
                        .with(user(userData))
                        .with(csrf())
                        .param("name", "Updated name")
                        .param("description", "Updated description")
                        .param("location", "VARNA")
                        .param("type", "EDUCATION"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(clubService).updateClub(eq(clubId), any(ClubRequest.class));
    }

    // ✅ JOIN CLUB
    @Test
    void joinClub_shouldRedirectToHome() throws Exception {

        when(userService.getById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/clubs/{id}/join", clubId)
                        .with(user(userData)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userClubService).joinUserToClub(user.getId(), clubId);
    }

    // ✅ LEAVE CLUB
    @Test
    void leaveClub_shouldRedirectToHome() throws Exception {

        when(userService.getById(user.getId())).thenReturn(user);

        mockMvc.perform(get("/clubs/{id}/leave", clubId)
                        .with(user(userData)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userClubService).removeUserFromClub(user.getId(), clubId);
    }

    // ✅ CLUB SCHEDULE
    @Test
    void clubSchedule_shouldReturnSchedulePage() throws Exception {

        when(clubService.getById(clubId)).thenReturn(club);
        when(userService.getById(user.getId())).thenReturn(user);
        when(userEventService.getEventsByUser(user)).thenReturn(List.of());
        when(eventService.getActiveEventsByClubId(Status.ACTIVE, clubId)).thenReturn(List.of());

        mockMvc.perform(get("/clubs/{id}/schedule", clubId)
                        .with(user(userData)))
                .andExpect(status().isOk())
                .andExpect(view().name("club-schedule"))
                .andExpect(model().attributeExists("club"))
                .andExpect(model().attributeExists("clubEvents"))
                .andExpect(model().attributeExists("userEvents"));
    }
}
