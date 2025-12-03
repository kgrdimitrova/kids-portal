package com.portal.kids.subscription.repository;

import com.portal.kids.event.model.Event;
import com.portal.kids.subscription.model.UserEvent;
import com.portal.kids.subscription.model.UserEventId;
import com.portal.kids.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, UUID> {

    @Query("SELECT s.event FROM UserEvent s WHERE s.user = :user ORDER BY s.event.startTime DESC")
    List<Event> findEventsByUserOrderByStartDateDESC(@Param("user") User user);

    List<UserEvent> findUserEventsByUser(@Param("user") User user);

    Optional<UserEvent> findByUserAndEvent(User user, Event event);

    void deleteById(UserEventId userEventId);

    @Query("SELECT s.user FROM UserEvent s WHERE s.event = :event ")
    List<User> findUsersByEvent(@Param("event") Event event);
}
