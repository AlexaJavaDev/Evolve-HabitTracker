package com.evolve.Evolve.controller;

import com.evolve.Evolve.model.Habit;
import com.evolve.Evolve.model.Log;
import com.evolve.Evolve.repository.HabitRepository;
import com.evolve.Evolve.repository.LogRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/habits")
public class HabitController {

    // ======================== БЛОК 1: ПОЛЯ И КОНСТАНТЫ ========================
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

    // ======================== БЛОК 2: ГЛАВНАЯ СТРАНИЦА (список привычек) ========================
    @GetMapping
    public String listHabits(Model model) {
        List<Habit> habits = habitRepository.findAll();
        LocalDate today = LocalDate.now();

        // 2.1 Форматированная дата для приветствия
        String formattedDate = today.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", new Locale("ru")));
        model.addAttribute("formattedDate", formattedDate);

        // 2.2 Счётчики выполненных привычек сегодня
        int totalToday = habits.size();
        int doneToday = 0;
        for (Habit h : habits) {
            int progress = calculateProgress(h, h.getDuration());
            if (progress == 100) doneToday++;
        }
        model.addAttribute("totalToday", totalToday);
        model.addAttribute("doneToday", doneToday);

        // 2.3 Прогресс и календарь для каждой привычки
        Map<Long, Integer> progressMap = new HashMap<>();
        Map<Long, List<String>> daysMap = new HashMap<>();
        for (Habit h : habits) {
            int duration = h.getDuration();
            List<String> days = new ArrayList<>();
            for (int i = 0; i < duration; i++) {
                LocalDate date = today.minusDays(i);
                boolean done = logRepository.findByHabitAndDate(h, date)
                        .map(Log::isDone).orElse(false);
                days.add(done ? h.getColor() : "#f0f0f0");
            }
            daysMap.put(h.getId(), days);
            progressMap.put(h.getId(), calculateProgress(h, duration));
        }

        model.addAttribute("logsMap", new HashMap<>());
        model.addAttribute("habits", habits);
        model.addAttribute("daysMap", daysMap);
        model.addAttribute("progressMap", progressMap);
        model.addAttribute("today", today);

        return "habits";
    }

    // ======================== БЛОК 3: БЫСТРОЕ ДОБАВЛЕНИЕ ========================
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

    // ======================== БЛОК 4: ДОБАВЛЕНИЕ СВОЕЙ ПРИВЫЧКИ ========================
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

    // ======================== БЛОК 5: УДАЛЕНИЕ ========================
    @PostMapping("/delete/{id}")
    public String deleteHabit(@PathVariable Long id) {
        habitRepository.deleteById(id);
        return "redirect:/habits";
    }

    // ======================== БЛОК 6: ОТМЕТКА ВЫПОЛНЕНИЯ ========================
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

    // ======================== БЛОК 7: ВСПОМОГАТЕЛЬНЫЙ МЕТОД (прогресс) ========================
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