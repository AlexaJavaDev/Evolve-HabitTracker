package com.evolve.Evolve.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private String color;
    private LocalDate createdAt;
    private String frequency;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Log> logs = new ArrayList<>();

    public Habit() {}

    public Habit(long id, String name, String description, String color, LocalDate createdAt, String frequency) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.createdAt = createdAt;
        this.frequency = frequency;
    }
}

