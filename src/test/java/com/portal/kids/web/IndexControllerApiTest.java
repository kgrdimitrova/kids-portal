package com.portal.kids.web;

import com.portal.kids.club.model.Club;
import com.portal.kids.club.service.ClubService;
import com.portal.kids.common.Location;
import com.portal.kids.event.model.AgeCategory;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.model.EventStatus;
import com.portal.kids.event.service.EventService;
import com.portal.kids.membership.model.UserClub;
import com.portal.kids.membership.service.UserClubService;
import com.portal.kids.security.UserData;
import com.portal.kids.subscription.model.UserEvent;
import com.portal.kids.subscription.service.UserEventService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.service.UserService;
import com.portal.kids.weather.service.WeatherService;
import com.portal.kids.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private UserEventService userEventService;

    @MockitoBean
    private UserClubService userClubService;

    @MockitoBean
    private ClubService clubService;

    @MockitoBean
    private WeatherService weatherService;

    @Captor
    private ArgumentCaptor<RegisterRequest> registerRequestArgumentCaptor;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getIndexEndPoint_shouldReturn200okAndIndexView() throws Exception {

        MockHttpServletRequestBuilder httpRequest = get("/");

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void postRegister_shouldReturn302RedirectAndRedirectToLoginAndInvokeRegisterServiceMethod() throws Exception {

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "testtest")
                .formField("password", "123123")
                .formField("location", "VARNA")
                .formField("email", "testtest@gmail.com")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService).register(registerRequestArgumentCaptor.capture());

        RegisterRequest dto = registerRequestArgumentCaptor.getValue();
        assertEquals("testtest", dto.getUsername());
        assertEquals("testtest@gmail.com", dto.getEmail());
        assertEquals("123123", dto.getPassword());
        assertEquals(Location.VARNA, dto.getLocation());
    }

    @Test
    void postRegisterWithInvalidFormData_shouldReturn200OkAndShowRegisterViewAndRegisterServiceMethodIsNeverInvoked() throws Exception {

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "T")
                .formField("password", "11")
                .formField("location", "VARNA")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any());
    }

    @Test
    void getHomePage_shouldReturnHomeViewWithUserModelAttributeAndStatusCodeIs200() throws Exception {

        User user = randomUser();
        when(userService.getById(any())).thenReturn(user);

        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());
        MockHttpServletRequestBuilder httpRequest = get("/home")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }

    @Test
    void getHomePageSomethingWentWrongInTheServiceLayer_shouldReturnInternalServerErrorView() throws Exception {

        User user = randomUser();
        when(userService.getById(any())).thenThrow(RuntimeException.class);

        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(), user.getRole(), user.isActive());
        MockHttpServletRequestBuilder httpRequest = get("/home")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("internal-server-error"));
    }

    public static User randomUser() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("test")
                .password("123123")
                .role(UserRole.USER)
                .location(Location.VARNA)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        Event event = Event.builder()
                .id(UUID.randomUUID())
                .startDate(LocalDate.of(2025, 12, 20))
                .status(EventStatus.ACTIVE)
                .ageCategory(AgeCategory.ALL)
                .title("Event1")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        Club club = Club.builder()
                .id(UUID.randomUUID())
                .location(Location.VARNA)
                .createdOn(LocalDate.now())
                .build();

        user.setUserClubs(List.of(UserClub.builder().club(club).user(user).joinedAt(LocalDateTime.now()).build()));
        user.setUserEvents(List.of(UserEvent.builder().event(event).user(user).status(EventStatus.ACTIVE).subscribedOn(LocalDateTime.now()).build()));

        return user;
    }
}
