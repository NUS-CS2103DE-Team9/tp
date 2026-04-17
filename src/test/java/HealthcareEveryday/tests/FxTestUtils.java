package HealthcareEveryday.tests;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;

import javafx.application.Platform;

class FxTestUtils {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {
                // JavaFX runtime bootstrap for tests.
            });
        } catch (IllegalStateException ignored) {
            // Toolkit already started by another test class.
        }
    }

    static void runOnFxThreadAndWait(Runnable action) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();

        Platform.runLater(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });

        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new AssertionError("Timed out waiting for JavaFX action.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Interrupted while waiting for JavaFX action.", e);
        }

        if (error.get() != null) {
            throw new AssertionError("JavaFX action failed.", error.get());
        }
    }
}
