package com.portal.kids.user.property;

import com.portal.kids.common.Location;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "users")
public class UserProperties {

    private DefaultUser defaultUser;

    @Data
    public static class DefaultUser {
        private String username;
        private String password;
        private Location location;
        }
    }

