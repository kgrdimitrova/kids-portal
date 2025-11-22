package com.portal.kids.web.dto;

import com.portal.kids.common.Location;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequest {

    @Email
    private String email;

    @NotNull
    private Location location;

    @URL
    private String profilePictureUrl;
}
