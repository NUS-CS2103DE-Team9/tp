package HealthcareEveryday.storage;

/**
 * Runtime exception for storage read/write failures that should be surfaced to callers.
 */
public class StorageException extends RuntimeException {
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
