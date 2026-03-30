package xmoke;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class User {
    private final String name;
    private final TaskList dailyRoutines;
    private final TaskList weeklyRoutines;
    private final ArrayList<Day> days;

    public User(String name) {
        this.name = name;
        this.dailyRoutines = new TaskList();
        this.weeklyRoutines = new TaskList();
        this.days = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public TaskList getDailyRoutines() {
        return dailyRoutines;
    }

    public TaskList getWeeklyRoutines() {
        return weeklyRoutines;
    }

    public Day getDay(LocalDate date) {
        return days.stream()
                .filter(d -> d.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Day> getDays() {
        return days;
    }

    public void addDay(Day day) {
        days.add(day);
        days.sort(Comparator.comparing(Day::getDate));
    }

    public Day getOrCreateDay(LocalDate date) {
        Optional<Day> existing = days.stream()
                .filter(d -> d.getDate().equals(date))
                .findFirst();

        if (existing.isPresent()) {
            Day day = existing.get();
            day.syncWithRoutines(dailyRoutines, weeklyRoutines, getPreviousDay(date));
            return day;
        }

        Day newDay = new Day(date);
        Day previousDay = getPreviousDay(date);
        newDay.syncWithRoutines(dailyRoutines, weeklyRoutines, previousDay);
        addDay(newDay);
        return newDay;
    }

    private Day getPreviousDay(LocalDate date) {
        return days.stream()
                .filter(d -> d.getDate().isBefore(date))
                .max(Comparator.comparing(Day::getDate))
                .orElse(null);
    }
}
