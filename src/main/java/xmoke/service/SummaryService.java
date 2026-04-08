package xmoke.service;

import xmoke.model.User;
import xmoke.storage.Storage;

import java.nio.file.Path;

/**
 * Handles summary generation logic.
 */
public class SummaryService {
    private final Storage storage;
    private final SummaryGenerator summaryGenerator;

    /**
     * Creates a summary service with the given storage.
     *
     * @param storage Storage instance.
     */
    public SummaryService(Storage storage) {
        this.storage = storage;
        this.summaryGenerator = new SummaryGenerator();
    }

    /**
     * Generates a monthly summary report for the specified user.
     *
     * @param userName Name of the user.
     * @return Path to the generated report.
     */
    public Path generateMonthlySummary(String userName) {
        User user = storage.loadUser(userName);
        return summaryGenerator.generateMonthlySummary(user);
    }
}
