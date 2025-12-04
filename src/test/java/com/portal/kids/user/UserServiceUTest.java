package com.portal.kids.user;

import com.portal.kids.common.Location;
import com.portal.kids.exception.UserNotFoundException;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.repository.UserRepository;
import com.portal.kids.user.service.UserService;
import com.portal.kids.web.dto.EditProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void whenEditUserProfile_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID userId = UUID.randomUUID();
        EditProfileRequest dto = null;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(userId, dto));
    }

    @Test
    void whenEditUserProfile_andRepositoryReturnsUserFromTheDatabase_thenUpdateTheUserProfileAndSaveItToTheDatabase() {

        UUID userId = UUID.randomUUID();
        EditProfileRequest dto = EditProfileRequest.builder()
                .location(Location.ASENOVGRAD)
                .profilePictureUrl("https://static.vecteezy.com/system/resources/previews/026/619/142/original/default-avatar-profile-icon-of-social-media-user-photo-image-vector.jpg")
                .email("updated@gmail.com")
                .build();
        User userRetrievedFromDatabase = User.builder()
                .id(userId)
                .username("initialUser")
                .email("database@gmail.com")
                .location(Location.VARNA)
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(userRetrievedFromDatabase));

        userService.updateProfile(userId, dto);

        assertNotNull(userRetrievedFromDatabase.getProfilePicture());
        assertEquals("https://static.vecteezy.com/system/resources/previews/026/619/142/original/default-avatar-profile-icon-of-social-media-user-photo-image-vector.jpg", userRetrievedFromDatabase.getProfilePicture());
        assertEquals(Location.ASENOVGRAD, userRetrievedFromDatabase.getLocation());
        assertEquals("updated@gmail.com", userRetrievedFromDatabase.getEmail());
        verify(userRepository).save(userRetrievedFromDatabase);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsTrainer_thenUserIsUpdatedWithRoleUserAndUpdatedOnNow_andPersistedInTheDatabase() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.TRAINER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertEquals(UserRole.USER, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsUser_thenUserIsUpdatedWithRoleTrainerAndUpdatedOnNow_andPersistedInTheDatabase() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.USER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertEquals(UserRole.TRAINER, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.switchRole(userId));
    }
}
