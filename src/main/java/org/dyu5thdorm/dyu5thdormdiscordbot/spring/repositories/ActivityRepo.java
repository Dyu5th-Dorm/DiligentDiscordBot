package org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories;

import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepo extends JpaRepository<Activity, Integer> {
    List<Activity> findAllByRegistrationDeadlineAfter(LocalDateTime registrationDeadline);
    Activity findTopByOrderByActivityIdDesc();
    boolean existsByActivityIdAndRegistrationDeadlineBefore(Integer activityId, LocalDateTime registrationDeadline);
    boolean existsByActivityIdAndStartTimeBefore(Integer activityId, LocalDateTime registrationDeadline);
}
