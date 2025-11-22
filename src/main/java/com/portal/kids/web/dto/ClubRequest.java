package com.portal.kids.web.dto;

import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClubRequest {

    @Size(min = 2, max = 24, message = "Title must be between 2 and 24 symbols")
    @NotBlank
    private String name;

    @Size(min = 2, message = "Description must be more than 2 symbols")
    @NotBlank
    private String description;

    @NotNull
    private Location location;

    @NotNull
    private ActivityType type;

    @URL
    private String picture;
}
