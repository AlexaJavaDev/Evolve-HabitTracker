package com.evolve.Evolve.controller;

import com.evolve.Evolve.model.Habit;
import com.evolve.Evolve.model.Log;
import com.evolve.Evolve.repository.HabitRepository;
import com.evolve.Evolve.repository.LogRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/habits")
public class HabitController {

    private final HabitRepository habitRepository;
    private final LogRepository logRepository;

    private static final List<String> QUICK_HABITS = Arrays.asList(
            "Зарядка", "Чтение 30 минут", "Медитация", "Прогулка",
            "Пить воду", "Учить английский", "Планировать день", "Благодарность"
    );

    public HabitController(HabitRepository habitRepository, LogRepository logRepository) {
        this.habitRepository = habitRepository;
        this.logRepository = logRepository;
    }

    @GetMapping
    public String listHabits(Model model) {
        List<Habit> habits = habitRepository.findAll();
        Map<Long, Integer> progressMap = new HashMap<>();
        Map<Long, List<String>> daysMap = new HashMap<>();
        LocalDate today = LocalDate.now();

        for (Habit h : habits) {
            int duration = h.getDuration();
            progressMap.put(h.getId(), calculateProgress(h, duration));
            List<String> days = new ArrayList<>();
            for (int i = 0; i < duration; i++) {
                LocalDate date = today.minusDays(i);
                boolean done = logRepository.findByHabitAndDate(h, date)
                        .map(Log::isDone).orElse(false);
                days.add(done ? h.getColor() : "#f0f0f0"); // цвет или серый
            }
            daysMap.put(h.getId(), days);
        }

        model.addAttribute("habits", habits);
        model.addAttribute("progressMap", progressMap);
        model.addAttribute("daysMap", daysMap);
        model.addAttribute("today", today);
        return "habits";
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
        habit.setDuration(7);
        habit.setCreatedAt(LocalDate.now());
        habitRepository.save(habit);
        return "redirect:/habits";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Habit habit = new Habit();
        habit.setDuration(7);
        model.addAttribute("habit", habit);
        return "add-habit";
    }

    @PostMapping("/add")
    public String addHabit(@ModelAttribute Habit habit) {
        if (habit.getColor() == null || habit.getColor().isBlank()) {
            habit.setColor("#aa96da");
        }
        if (habit.getFrequency() == null || habit.getFrequency().isBlank()) {
            habit.setFrequency("DAILY");
        }
        if (habit.getDuration() < 5 || habit.getDuration() > 30) {
            habit.setDuration(7);
        }
        habit.setCreatedAt(LocalDate.now());
        habitRepository.save(habit);
        return "redirect:/habits";
    }

    @PostMapping("/delete/{id}")
    public String deleteHabit(@PathVariable Long id) {
        habitRepository.deleteById(id);
        return "redirect:/habits";
    }

    @GetMapping("/toggle/{id}")
    public String toggleHabit(@PathVariable Long id, @RequestParam String date) {
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Привычка не найдена"));
        LocalDate localDate = LocalDate.parse(date);
        Log log = logRepository.findByHabitAndDate(habit, localDate)
                .orElse(new Log(localDate, false, habit));
        log.setDone(!log.isDone());
        logRepository.save(log);
        return "redirect:/habits";
    }

    private int calculateProgress(Habit habit, int totalDays) {
        LocalDate today = LocalDate.now();
        int doneDays = 0;
        for (int i = 0; i < totalDays; i++) {
            LocalDate date = today.minusDays(i);
            boolean exists = habit.getLogs().stream()
                    .anyMatch(log -> log.getDate().equals(date) && log.isDone());
            if (exists) doneDays++;
        }
        return (doneDays * 100) / totalDays;
    }
}