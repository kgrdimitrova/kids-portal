package com.portal.kids.web;

import com.portal.kids.payment.service.PaymentService;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.service.UserService;
import com.portal.kids.web.dto.EditProfileRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PaymentService paymentService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setUsername("john");
        user.setEmail("john@mail.com");
        user.setPassword("123123");
        user.setRole(UserRole.USER);
        user.setActive(true);
    }

    @Test
    @WithMockUser
    void getProfilePage_shouldReturnProfileView() throws Exception {
        Mockito.when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}/profile", userId))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("editProfileRequest"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    @WithMockUser
    void updateProfile_shouldRedirect_whenInputIsValid() throws Exception {

        Mockito.doNothing().when(userService).updateProfile(eq(userId), any(EditProfileRequest.class));

        mockMvc.perform(put("/users/{id}/profile", userId)
                        .with(csrf())
                        .param("username", "newName")
                        .param("email", "new@mail.com")
                        .param("location", "VARNA"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser
    void updateProfile_shouldReturnProfile_whenBindingError() throws Exception {

        Mockito.when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(put("/users/{id}/profile", userId)
                        .with(csrf())
                        .param("username", "")  // invalid
                        .param("email", "invalid-email"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("editProfileRequest"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnUsersPage() throws Exception {

        Mockito.when(userService.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void switchUserRole_shouldRedirect() throws Exception {

        Mockito.doNothing().when(userService).switchRole(userId);

        mockMvc.perform(patch("/users/{id}/role", userId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService).switchRole(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void switchUserStatus_shouldRedirect() throws Exception {

        Mockito.doNothing().when(userService).switchStatus(userId);

        mockMvc.perform(patch("/users/{id}/status", userId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService).switchStatus(userId);
    }
}