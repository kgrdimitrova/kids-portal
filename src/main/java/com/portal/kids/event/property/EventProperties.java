package com.portal.kids.event.property;

import com.portal.kids.common.ActivityType;
import com.portal.kids.common.Location;
import com.portal.kids.event.model.AgeCategory;
import com.portal.kids.event.model.EventPeriodicity;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Configuration
@ConfigurationProperties(prefix = "events")
public class EventProperties {

    private DefaultEvent defaultEvent;

    @Data
    public static class DefaultEvent {
        private String title;
        private Location location;
        private EventPeriodicity periodicity;
        private AgeCategory ageCategory;
        private ActivityType type;
        private BigDecimal pass;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
    }
}
