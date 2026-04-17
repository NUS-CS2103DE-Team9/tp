# Healthcare Everyday

Some days, care is not one big decision. It is ten small ones: medicine on time, enough water, a short walk, and a quick note on how the day felt.

Healthcare Everyday is an offline-first JavaFX desktop app for seniors and caregivers to keep those small decisions visible and manageable: daily and weekly routines, daily logs, history review, and summary reports, all stored locally on the same machine.

> This app is for routine tracking and record-keeping only. It does not provide medical diagnosis or treatment advice.

---

## User guide

- End-user instructions: [`/docs/USER_GUIDE.md`](/docs/USER_GUIDE.md)
- Developer documentation: [`/docs/DEVELOPER_GUIDE.md`](/docs/DEVELOPER_GUIDE.md)

---

## User stories

- As a senior, I want to see today’s daily and weekly routines and mark them done, so I do not forget important habits.
- As a senior, I want to write a short daily log, so wellbeing patterns can be reviewed later.
- As a caregiver, I want to add, edit, or remove routines for each senior, so task lists stay aligned with care needs.
- As a caregiver, I want to view history and generate a summary report, so I can review progress and share updates clearly.
- As a user, I want records stored locally on this machine, so data remains available across sessions.

---

## Requirements

- **JDK 21**
- **Gradle** (wrapper included: `gradlew` / `gradlew.bat`)

---

## Run the application

Run from repository root:

```bash
./gradlew run
```

On Windows:

```batch
gradlew.bat run
```

From an IDE, use `HealthcareEveryday.Launcher` as the main class.

---

## Build a runnable JAR

```bash
./gradlew shadowJar
```

Output: `build/libs/healthcare-everyday.jar`

```bash
java -jar build/libs/healthcare-everyday.jar
```

On Windows, use `gradlew.bat shadowJar` if you use the batch wrapper.

---

## Tests

```bash
./gradlew test
./gradlew checkstyleMain
```

---

## Tech stack

- Java 21, JavaFX 21, Gradle
- FXML views in `src/main/resources/view/`
- Local persistence under `data/`

---

**AI assistance:** Some work on this project used AI-assisted tooling, including IDE autocomplete, debugging help, and UI design tips.
