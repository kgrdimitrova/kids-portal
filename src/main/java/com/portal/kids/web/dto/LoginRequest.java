package com.portal.kids.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest{
        @Size(min = 6, max = 24, message = "Username must be between 6 and 24 symbols")
        @NotBlank
        private String username;

        @Size(min = 6, max = 24, message = "Password must be between 6 and 24 symbols")
        @NotBlank
        private String password;
}
