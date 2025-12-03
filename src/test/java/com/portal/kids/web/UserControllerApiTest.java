package com.portal.kids.web;

import com.portal.kids.common.Location;
import com.portal.kids.security.UserData;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.service.UserService;
import com.portal.kids.web.dto.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc

public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void patchRequestToChangeUserStatus_fromAdminUser_shouldReturnRedirectAndInvokeServiceMethod() throws Exception {

        UserDetails authentication = adminAuthentication();
        MockHttpServletRequestBuilder httpRequest = patch("/users/{userId}/status", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        verify(userService).switchStatus(any());
    }

    @Test
    void patchRequestToChangeUserStatus_fromNormalUser_shouldReturn404StatusCodeAndViewNotFound() throws Exception {

        UserDetails authentication = userAuthentication();
        MockHttpServletRequestBuilder httpRequest = patch("/users/{userId}/status", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
        verifyNoInteractions(userService);
    }

    @Test
    void patchRequestToChangeUserRole_fromAdminRole_shouldReturnRedirectAndInvokeServiceMethod() throws Exception {

        UserDetails authentication = adminAuthentication();
        MockHttpServletRequestBuilder httpRequest = patch("/users/{userId}/role", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
        verify(userService).switchRole(any());
    }

    @Test
    void patchRequestToChangeUserRole_fromNormalUser_shouldReturn404StatusCodeAndViewNotFound() throws Exception {

        UserDetails authentication = userAuthentication();
        MockHttpServletRequestBuilder httpRequest = patch("/users/{userId}/role", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));
        verifyNoInteractions(userService);
    }

    @Test
    void getProfilePage_shouldReturnProfileViewAndModel() throws Exception {

        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setUsername("TestUser");

        when(userService.getById(id)).thenReturn(user);

        MockHttpServletRequestBuilder request = get("/users/{id}/profile", id)
                .with(user(userAuthentication()));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("editProfileRequest"))
                .andExpect(model().attributeExists("user"));

        verify(userService).getById(id);
    }

    @Test
    void updateProfile_whenValid_shouldRedirectToHome() throws Exception {

        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);

        when(userService.getById(id)).thenReturn(user);

        MockHttpServletRequestBuilder request = put("/users/{id}/profile", id)
                .with(user(userAuthentication()))
                .with(csrf())
                .param("username", "UpdatedName")
                .param("location", Location.VARNA.toString())
                .param("email", "updated@example.com");

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService).updateProfile(eq(id), any(EditProfileRequest.class));
    }

    @Test
    void updateProfile_whenValidationFails_shouldReturnProfileView() throws Exception {

        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);

        when(userService.getById(id)).thenReturn(user);

        MockHttpServletRequestBuilder request = put("/users/{id}/profile", id)
                .with(user(userAuthentication()))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));

        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void getAllUsers_asAdmin_shouldReturnUsersView() throws Exception {

        when(userService.getAll()).thenReturn(List.of(new User()));

        MockHttpServletRequestBuilder request = get("/users")
                .with(user(adminAuthentication()));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"));

        verify(userService).getAll();
    }

    @Test
    void getAllUsers_asNormalUser_shouldReturnNotFound() throws Exception {

        MockHttpServletRequestBuilder request = get("/users")
                .with(user(userAuthentication()));

        mockMvc.perform(request)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verifyNoInteractions(userService);
    }

    public static UserDetails adminAuthentication() {

        return new UserData(UUID.randomUUID(), "Test", "111111", UserRole.ADMIN, true);
    }

    public static UserDetails userAuthentication() {

        return new UserData(UUID.randomUUID(), "Test", "222222", UserRole.USER, true);
    }
}
