package com.evolve.Evolve.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 200)
    private String description;

    @Column(length = 20)
    private String color;

    private LocalDate createdAt;

    @Column(length = 20)
    private String frequency;

    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Log> logs = new ArrayList<>();

    public Habit() {}

    public Habit(String name, String description, String color, LocalDate createdAt, String frequency) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.createdAt = createdAt;
        this.frequency = frequency;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public List<Log> getLogs() { return logs; }
    public void setLogs(List<Log> logs) { this.logs = logs; }
}