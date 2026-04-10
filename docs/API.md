# Implementation notes — Healthcare Everyday

Scope: **desktop JavaFX app**, local file storage, no public HTTP API.

## Stack

- Java 21, JavaFX 21, Gradle  
- GUI: FXML under `src/main/resources/view/`, controllers in `src/main/java/`  
- Model and persistence: `HealthcareEveryday` package (`User`, `Day`, `Task`, `Storage`, etc.)

## Storage (`HealthcareEveryday.storage.Storage`)

- `data/users/` — one folder per senior; `profile.txt`, routine files, `days/*.txt`  
- `data/app/caregiver.txt` — single shared caregiver password  
- Key methods: `listSeniorNames()`, `addUser`, `loadUser`, `saveUser`, caregiver password helpers  

## Main navigation (`HealthcareEveryday.MainApp`)

Scenes: login (senior picker + caregiver), senior tasks, senior log, caregiver login/menu, user pick for editing routines, history flows, summary generation.

Global stylesheet: `/css/app.css` applied to every `Scene`.

## Controllers (summary)

| Controller | Role |
|------------|------|
| `HealthcareEveryday.controller.LoginController` | Senior buttons; caregiver entry |
| `HealthcareEveryday.controller.SeniorTasksController` | Daily/weekly checkboxes for today |
| `HealthcareEveryday.controller.SeniorLogController` | Free-text daily log |
| `HealthcareEveryday.controller.CaregiverLoginController` / `HealthcareEveryday.controller.CaregiverMenuController` | Caregiver auth and menu |
| `HealthcareEveryday.controller.CaregiverSelectUserController` | Pick senior to edit routines |
| `HealthcareEveryday.controller.EditRoutineController` | Add/remove routine tasks |
| History / summary controllers | Read-only views and CSV report |

No text-command chatbot or `Xmoke` command line class is part of this codebase.
