package org.dyu5thdorm.dyu5thdormdiscordbot.spring.services;

import lombok.RequiredArgsConstructor;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.models.NoCallRollDate;
import org.dyu5thdorm.dyu5thdormdiscordbot.spring.repositories.NoCallRollDateRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NoCallRollDateService {
    final
    NoCallRollDateRepo repository;

    public boolean exists(LocalDate localDay) {
        return repository.existsByDay(localDay);
    }

    public boolean save(NoCallRollDate noCallRollDate) {
        if (this.exists(noCallRollDate.getDay())) return false;
        repository.save(noCallRollDate);
        return true;
    }
}
