package com.portal.kids.web.dto;

import com.portal.kids.common.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// RECORD се ползва за рест заявки
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Size(min = 6, max = 24, message = "Username must be between 6 and 24 symbols")
    @NotBlank
    private String username;

    @Size(min = 6, max = 24, message = "Password must be between 6 and 24 symbols")
    @NotBlank
    private String password;

    @NotBlank
    private String email;

    @NotNull
    private Location location;
}