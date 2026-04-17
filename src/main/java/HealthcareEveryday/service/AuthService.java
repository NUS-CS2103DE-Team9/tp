package HealthcareEveryday.service;

import java.util.List;

import HealthcareEveryday.storage.Storage;

/**
 * Handles login-related and caregiver account-related logic.
 */
public class AuthService {
    private final Storage storage;

    /**
     * Creates an auth service with the given storage.
     *
     * @param storage Storage instance.
     */
    public AuthService(Storage storage) {
        this.storage = storage;
    }

    /**
     * Returns all senior user names.
     *
     * @return List of senior names.
     */
    public List<String> getSeniorNames() {
        return storage.listSeniorNames();
    }

    /**
     * Adds a new senior user.
     *
     * @param name Name of the user.
     * @return True if added successfully.
     */
    public boolean addUser(String name) {
        return storage.addUser(name);
    }

    /**
     * Returns whether the specified user exists.
     *
     * @param name Name of the user.
     * @return True if the user exists.
     */
    public boolean userExists(String name) {
        return storage.userExists(name);
    }

    /**
     * Deletes the specified senior user.
     *
     * @param name Name of the user.
     * @return True if deleted successfully.
     */
    public boolean deleteUser(String name) {
        return storage.deleteUser(name);
    }

    /**
     * Checks whether the caregiver password is correct.
     *
     * @param password Password to validate.
     * @return True if valid.
     */
    public boolean validateCaregiverPassword(String password) {
        return storage.validateCaregiverPassword(password);
    }

    /**
     * Changes caregiver password.
     *
     * @param oldPassword Old password.
     * @param newPassword New password.
     * @return True if changed successfully.
     */
    public boolean changeCaregiverPassword(String oldPassword, String newPassword) {
        return storage.changeCaregiverPassword(oldPassword, newPassword);
    }
}
