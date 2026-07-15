package com.evolve.Evolve.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private boolean done;

    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;

    public Log() {}

    public Log(LocalDate date, boolean done, Habit habit) {
        this.date = date;
        this.done = done;
        this.habit = habit;
    }
}
