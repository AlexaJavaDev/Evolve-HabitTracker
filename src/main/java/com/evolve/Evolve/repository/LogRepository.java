package com.evolve.Evolve.repository;

import com.evolve.Evolve.model.Habit;
import com.evolve.Evolve.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByHabit(Habit habit);
    Optional<Log> findByHabitAndDate(Habit habit, LocalDate date);
}
