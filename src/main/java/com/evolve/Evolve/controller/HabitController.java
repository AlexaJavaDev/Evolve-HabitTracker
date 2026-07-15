package com.evolve.Evolve.controller;

import com.evolve.Evolve.model.Habit;
import com.evolve.Evolve.repository.HabitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/habits")
public class HabitController {

    private final HabitRepository habitRepository;

    public HabitController(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("habit", new Habit());
        return "add-habit";
    }

    @PostMapping("/add")
    public String addHabit(@ModelAttribute Habit habit) {
        habit.setCreatedAt(LocalDate.now());
        habitRepository.save(habit);
        return "redirect:/habits";
    }

    @GetMapping
    public String listHabits(Model model) {
        model.addAttribute("habits", habitRepository.findAll());
        return "habits";
    }
}