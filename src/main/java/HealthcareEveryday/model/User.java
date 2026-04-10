package HealthcareEveryday.model;

import HealthcareEveryday.MainApp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

/**
 * Represents a user with daily routines, weekly routines, and recorded day data.
 */
public class User {
    private final String name;
    private final TaskList dailyRoutines;
    private final TaskList weeklyRoutines;
    private final ArrayList<Day> days;

    /**
     * Creates a user with the given name.
     *
     * @param name Name of the user.
     */
    public User(String name) {
        this.name = name;
        this.dailyRoutines = new TaskList();
        this.weeklyRoutines = new TaskList();
        this.days = new ArrayList<>();
    }

    /**
     * Returns the user's name.
     *
     * @return User name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the user's daily routines.
     *
     * @return Daily routine list.
     */
    public TaskList getDailyRoutines() {
        return dailyRoutines;
    }

    /**
     * Returns the user's weekly routines.
     *
     * @return Weekly routine list.
     */
    public TaskList getWeeklyRoutines() {
        return weeklyRoutines;
    }

    /**
     * Returns the day record for the given date.
     *
     * @param date Date to look up.
     * @return Day record, or null if not found.
     */
    public Day getDay(LocalDate date) {
        return days.stream()
                .filter(d -> d.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns all recorded day data for the user.
     *
     * @return List of recorded days.
     */
    public ArrayList<Day> getDays() {
        return days;
    }

    /**
     * Adds a day record and keeps the list sorted by date.
     *
     * @param day Day record to add.
     */
    public void addDay(Day day) {
        days.add(day);
        days.sort(Comparator.comparing(Day::getDate));
    }

    /**
     * Returns the day record for the given date, creating one if needed.
     *
     * @param date Date to retrieve or create.
     * @return Existing or newly created day record.
     */
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

    /**
     * Returns the most recent recorded day before the given date.
     *
     * @param date Date to compare against.
     * @return Previous day record, or null if none exists.
     */
    private Day getPreviousDay(LocalDate date) {
        return days.stream()
                .filter(d -> d.getDate().isBefore(date))
                .max(Comparator.comparing(Day::getDate))
                .orElse(null);
    }
}
