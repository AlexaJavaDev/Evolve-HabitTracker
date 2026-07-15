package com.evolve.Evolve.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }
}
