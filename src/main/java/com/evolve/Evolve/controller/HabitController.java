package com.evolve.Evolve.controller;

import com.evolve.Evolve.model.Habit;
import com.evolve.Evolve.repository.HabitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/habits")
public class HabitController {

    private static final List<String> QUICK_HABITS = Arrays.asList(
            "Зарядка", "Чтение 30 минут", "Медитация", "Прогулка на свежем воздухе",
            "Пить воду", "Учить английский", "Планировать день", "Благодарность"
    );

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

    @GetMapping("/quick-add")
    public String showQuickAdd(Model model) {
        model.addAttribute("quickHabits", QUICK_HABITS);
        return "quick-add";
    }

    @PostMapping("/quick-add")
    public String addQuickHabit(@RequestParam String name) {
        Habit habit = new Habit();
        habit.setName(name);
        habit.setDescription("Быстрая привычка");
        habit.setColor("#aa96da");
        habit.setFrequency("DAILY");
        habit.setCreatedAt(LocalDate.now());
        habitRepository.save(habit);
        return "redirect:/habits";
    }

    @GetMapping
    public String listHabits(Model model) {
        model.addAttribute("habits", habitRepository.findAll());
        return "habits";
    }

    @GetMapping("/delete/{id}")
    public String deleteHabit(@PathVariable Long id) {
        habitRepository.deleteById(id);
        return "redirect:/habits";
    }
}