package com.portal.kids.user.service;

import com.portal.kids.common.Location;
import com.portal.kids.exception.UserNotFoundException;
import com.portal.kids.exception.UsernameAlreadyExistException;
import com.portal.kids.security.UserData;
import com.portal.kids.user.model.User;
import com.portal.kids.user.model.UserRole;
import com.portal.kids.user.repository.UserRepository;
import com.portal.kids.web.dto.EditProfileRequest;
import com.portal.kids.web.dto.RegisterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest registerRequest) {

        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistException("User with username [%s] already exists.".formatted(registerRequest.getUsername()));
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .location(Location.valueOf(String.valueOf(registerRequest.getLocation())))
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("User with name [{}] was successfully registered.", registerRequest.getUsername());

        return user;
    }

    @Cacheable("users")
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    public List<User> getAll() {

        return userRepository.findAll();
    }

    public User getByUsername(String username) {

        return userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("User with [%s] [%s] does not exists.".formatted("username", username)));
    }

    public User getById(UUID id) {

        return userRepository.findById(id).orElseThrow(()->new UserNotFoundException("User with [%s]] [%s] does not exists.".formatted("id", id)));
    }

    @CacheEvict(value = "users", allEntries = true)
    @PreAuthorize("hasAuthority('EDIT_USER')")
    public void switchRole(UUID userId) {

        User user = getById(userId);
        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.TRAINER);
        }
        else {
            user.setRole(UserRole.USER);
        }
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @PreAuthorize("hasAuthority('EDIT_USER')")
    public void switchStatus(UUID userId) {

        User user = getById(userId);

        user.setActive(!user.isActive());

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException(username));

        return new UserData(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }

    public void updateProfile(UUID id, EditProfileRequest editProfileRequest) {

            User user = getById(id);

            user.setEmail(editProfileRequest.getEmail());
            user.setProfilePicture(editProfileRequest.getProfilePictureUrl());
            user.setLocation(editProfileRequest.getLocation());

            userRepository.save(user);
    }
}
