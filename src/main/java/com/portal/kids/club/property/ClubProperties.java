package com.portal.kids.club.property;


import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "clubs")
public class ClubProperties {

    private DefaultClub defaultClub;

    @Data
    public static class DefaultClub
    {
        private String name;
        private Location location;
        private ActivityType type;
    }

}
