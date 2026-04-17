package HealthcareEveryday.service;

import java.time.LocalDate;

import HealthcareEveryday.model.Day;
import HealthcareEveryday.model.User;
import HealthcareEveryday.storage.Storage;

/**
 * Handles daily log-related logic.
 */
public class LogService {
    private final Storage storage;

    /**
     * Creates a log service with the given storage.
     *
     * @param storage Storage instance.
     */
    public LogService(Storage storage) {
        this.storage = storage;
    }

    /**
     * Returns today's log text for the specified user.
     *
     * @param userName Name of the user.
     * @return Today's log text.
     */
    public String getTodayLog(String userName) {
        User user = storage.loadUser(userName);
        Day today = user.getOrCreateDay(LocalDate.now());
        storage.saveUser(user);
        return today.getLog();
    }

    /**
     * Saves today's log text for the specified user.
     *
     * @param userName Name of the user.
     * @param log Log text to save.
     */
    public void saveTodayLog(String userName, String log) {
        User user = storage.loadUser(userName);
        Day today = user.getOrCreateDay(LocalDate.now());
        today.setLog(log);
        storage.saveUser(user);
    }
}
