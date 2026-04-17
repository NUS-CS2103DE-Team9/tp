# Developer Guide

This section covers the technical documentation of Healthcare Everyday.

## Table of contents

- [Architecture](#architecture)
- [Class Diagram](#class-diagram)
- [Implementation](#implementation)
  - [Entry Point](#entry-point)
  - [Controllers](#controllers)
    - [MainController](#maincontroller)
    - [Feature: Sessions](#feature-sessions)
    - [Feature: Breaks](#feature-breaks)
    - [StatsController](#statscontroller)
    - [Feature: Statistics](#feature-statistics)
  - [Model](#model)
  - [Service](#service)
    - [Session Service](#session-service)
    - [Storage Service](#storage-service)
  - [Storage](#storage)
  - [Testing](#testing)

## Architecture

Healthcare Everyday uses a layered architecture:

- **UI layer**: JavaFX + FXML views, controlled by controller classes.
- **Controller layer**: receives UI events and delegates logic.
- **Service layer**: business logic (auth, routines/sessions, logs, history, summary).
- **Model layer**: domain objects (`User`, `Day`, `Task`, `TaskList`, `RoutineType`).
- **Storage layer**: file-based persistence under `data/`.

`MainApp` is the application coordinator. It creates one shared `Storage` instance, initializes all services, and injects services into controllers through scene setup.

![Architecture](images/Architecture.png)

## Class Diagram

The class model centers around `User` and `Day`:

- `User` owns routine lists (`daily`, `weekly`) and a set of day records.
- `Day` stores log text plus completion maps for daily and weekly tasks.
- Services depend on `Storage`, not on controller classes.
- Controllers depend on `MainApp` and service interfaces.

![Class Diagram](images/Class%20Diagram.png)

## Implementation

### Entry Point

- `Launcher` is the JAR main class and forwards startup to `MainApp.main(...)`.
- `MainApp` extends `Application`, boots JavaFX, opens the login scene, and handles all scene transitions.
- Service wiring happens in `MainApp` constructor fields:
  - `AuthService`
  - `RoutineService`
  - `LogService`
  - `HistoryService`
  - `SummaryService`

### Sequence Diagrams (Implementation Flows)

The following sequence diagrams document key implemented flows:

#### Open (show the senior main page)

- Purpose: show the senior main page after senior selection/login.

![Sequence Open](images/Sequence_Open.png)

#### Checkbox (seniors click done)

- Purpose: senior marks a daily/weekly routine as completed.

![Sequence Checkbox](images/Sequence_Checkbox.png)

#### View (view log)

- Purpose: open and view history/log information.

![Sequence View](images/Sequence_View.png)

#### EditRoutine (caregiver edits daily routine)

- Purpose: caregiver adds/removes routines for a selected senior.

![Sequence EditRoutine](images/Sequence_EditRoutine.png)

#### Dailylog (submit daily log)

- Purpose: senior submits the daily log text.

![Sequence Dailylog](images/Sequence_Dailylog.png)

### Controllers

Controller classes are in `src/main/java/HealthcareEveryday/controller/` and are responsible for:

- receiving user actions from JavaFX views
- validating immediate UI state
- delegating operations to services
- updating labels/checkboxes/scene navigation

#### MainController

There is no class literally named `MainController` in the current codebase.

The equivalent orchestration role is handled by `MainApp`, which:

- loads FXML views
- obtains each view controller instance
- injects `MainApp` into controllers
- navigates between scenes based on user flows

#### Feature: Sessions

In this codebase, "sessions" map to daily/weekly routine tracking:

- `SeniorTasksController` renders daily and weekly checkboxes.
- On checkbox toggle, it calls:
  - `RoutineService.setDailyCompleted(...)`, or
  - `RoutineService.setWeeklyCompleted(...)`.
- `RoutineService` loads user data, updates today's `Day`, and saves immediately.

This gives a complete flow from UI action to persisted task completion state.

#### Feature: Breaks

There is no dedicated "Breaks" module yet.

Current closest mapping:

- Break-like activities can be represented as routine items in daily/weekly lists.
- They are managed through the same path as sessions (`RoutineService` + `SeniorTasksController` / `EditRoutineController`).

If a standalone Break feature is added later, it can be introduced as:

- a new model (e.g., `BreakEntry`)
- a new service (e.g., `BreakService`)
- one or more focused controllers and views

#### StatsController

There is no class named `StatsController`.

Statistics/history responsibilities are split across:

- `TodayHistoryController` (today snapshot across users)
- `WeeklyHistoryController` (7-day user history)
- `GenerateSummarySelectUserController` (summary report trigger)

#### Feature: Statistics

Statistics are delivered through `HistoryService` and `SummaryService`:

- `HistoryService.getTodayHistoryForAllUsers()` builds per-user task status lists.
- `HistoryService.getWeeklyHistory(userName)` builds a 7-day result window:
  - daily task completion counts + missed days
  - weekly task completion done-days
- `SummaryService.generateMonthlySummary(userName)` delegates to `SummaryGenerator` to output CSV reports in `report/`.

### Model

Core model classes (package `HealthcareEveryday.model`):

- `User`: owns routines and historical day records.
- `Day`: stores one date's log and completion maps.
- `Task`: task description + `RoutineType`.
- `TaskList`: collection operations for tasks.
- `RoutineType`: enum (`DAILY`, `WEEKLY`).

Model objects are loaded and persisted through services + storage, not directly by controllers.

### Service

Services encapsulate business rules and persistence orchestration.

- `AuthService`: caregiver auth, user listing/creation/deletion, password changes.
- `RoutineService`: add/remove routines, retrieve today, set completion.
- `LogService`: read/write today's free-text log.
- `HistoryService`: prepare today and weekly historical representations.
- `SummaryService`: generate export summaries.

#### Session Service

Session-like routine logic lives in `RoutineService`:

- validates and normalizes task text
- rejects blank/duplicate routines
- applies completion updates onto today's `Day`
- persists updates with `Storage.saveUser(...)`

This ensures immediate persistence after every session-related action.

#### Storage Service

There is no standalone class named `StorageService`.

Equivalent storage-facing service behavior is distributed across domain services (`AuthService`, `RoutineService`, `LogService`, `HistoryService`, `SummaryService`), each calling the shared `Storage` instance.

### Storage

`Storage` implements file-based persistence and bootstrap.

Main responsibilities:

- create/maintain base folders (`data/users`, `data/app`)
- manage caregiver credential file (`data/app/caregiver.txt`)
- save/load each user profile and routine files
- save/load per-day files in `data/users/<user>/days/YYYY-MM-DD.txt`

Day-file format uses sections:

- `log=<text>`
- `[daily]` + task completion lines
- `[weekly]` + task completion lines

This storage approach keeps data human-readable and offline-friendly.

### Testing

Tests are under `src/test/java/HealthcareEveryday/tests/`.

Current coverage includes:

- `StorageTest`: persistence bootstrap, user lifecycle, save/load round-trip.
- `AuthServiceTest`: authentication and user-management service behavior.
- `RoutineServiceTest`: routine validation and completion persistence.
- `CaregiverLoginControllerTest`: controller-level login behavior.
- `UiViewTest`: FXML loading and presence of required controls.

Run test suite from repository root:

```bash
./gradlew test
```

On Windows:

```batch
gradlew.bat test
```
