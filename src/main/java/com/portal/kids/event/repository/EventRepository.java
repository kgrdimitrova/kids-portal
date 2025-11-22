package com.portal.kids.event.repository;

import com.portal.kids.event.model.AgeCategory;
import com.portal.kids.event.model.Event;
import com.portal.kids.event.model.EventPeriodicity;
import com.portal.kids.event.model.EventStatus;
import com.portal.kids.event.driven.GenerateEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByStartDateAfter(LocalDate localDate);

    Event findByTitleAndStartDateAndAgeCategory(String title, LocalDate startDate, AgeCategory ageCategory);

    List<Event> findAllByStartDateBetweenAndPeriodicity(LocalDate startDate, LocalDate endDate, EventPeriodicity eventPeriodicity);

    List<Event> findByStartDateBefore(LocalDate startDate);

    List<Event> findAllActiveEventsByClubId(UUID clubId);
}
