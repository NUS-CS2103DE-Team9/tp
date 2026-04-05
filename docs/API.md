# API Documentation
## Personal Health Routine Tracker (Desktop App, Non-Medical)

This document describes how command-based interaction and internal modules fit together for the Personal Health Routine Tracker. The product is **local-first**, **non-medical**, and uses **strict text commands** in the chatbot (no free-form AI interpretation).

- **Scope:** Internal module boundaries and the chatbot command surface (not public HTTP APIs).
- **Product type:** Desktop JavaFX application.
- **Roles (target):** Patient (data entry) and Caregiver (view-only summaries and logs). Enforcement in the chatbot path is **planned** (see below).

---

## 1. Document status: what is implemented vs planned

| Area | Status | Notes |
|------|--------|--------|
| **Chatbot strict commands** (`record`, `skip`, `symptom`, `exercise`, `note`, `deadline`, `list`, `bye`) | **Implemented** | Handled in `xmoke.Xmoke` via `getResponse(String)`; routes each line to validation, in-memory updates, and persistence. |
| **Flexible deadline time parsing** | **Implemented** | `xmoke.Parser.parseDateTime(String)` accepts multiple formats (e.g. `7pm`, `19:00`, `yyyy-MM-dd HH:mm`, legacy `yyyy-MM-dd HHmm`, etc.). |
| **Today list output** | **Implemented** | `list` builds a same-day view: fixed routines with `[X]` / `[ ] (skipped)` and today’s deadlines. |
| **Per-user file storage (`User`, `Day`, routines)** | **Implemented** | `xmoke.Storage` loads/saves users under `data/users/…`; GUI flows use `User` / `Day` for routines and daily logs. |
| **CSV summary export** | **Partially implemented** | `xmoke.SummaryGenerator` generates monthly CSV reports under `report/`. |
| **Modular services** (`RoutineService`, `CommandController`, …) | **Planned** | Described in §5 as the target split; logic is not necessarily in separate classes yet—easy to refactor toward these names. |
| **Weekly summary + PDF export** | **Planned** | Align with PRD/SDD; extend or complement `SummaryGenerator`. |
| **Caregiver read-only in chatbot** | **Planned** | Pass `UserRole` (or equivalent) into a future `handleCommand` and block writes for caregiver. |

---

## 2. Implemented: chatbot command grammar

The chatbot accepts **one command per message**. Unknown input returns an error string (no silent fallback).

| Command | Behaviour |
|---------|-----------|
| `record <item> [optional note]` | Marks a **fixed routine item** as done for **today**. Valid items: `breakfast`, `lunch`, `dinner`, `medication1`, `medication2`. Recording again the **same item on the same day** overwrites the previous status/note. |
| `skip <item> [optional reason]` | Marks the item as skipped for today, with optional reason. Same-day re-skip overwrites prior skip for that item. |
| `symptom <description> <severity>` | Stores a symptom; **severity** must be the **last token**, integer **1–10**. Example: `symptom headache after lunch 6`. |
| `exercise <description>` | Appends a free-form exercise log (multiple per day allowed). |
| `note <text>` | Stores a free-form daily note. |
| `deadline <description> <time>` | Adds a one-off deadline. Time may be separated by spaces or use ` /by ` after the description. Parsed to `LocalDateTime`. |
| `list` | Shows **today’s** fixed routine status and **today’s** deadlines. |
| `bye` | Ends the session (GUI may close after reply). |

**Removed from chatbot (old task-manager commands):** e.g. `todo`, `event`, `mark`, `unmark`, `delete`, `find`, `sort`, `cheer`—these are no longer part of the health chatbot grammar.

---

## 3. Implemented: primary entry point

### `xmoke.Xmoke.getResponse(input: String)`

- **Role:** Single entry for the chat window: one input string → one reply string.
- **Parameters:** `input` — raw user line from the text field.
- **Returns:** User-facing message (success, list text, or error). Not a structured `CommandResult` object yet—that remains a **planned** cleanup once `CommandController` exists.

---

## 4. Implemented: parsing utilities

### `xmoke.Parser.parseDateTime(dateTimeStr: String)`

Used by `deadline` (and any future time fields). Supports **flexible** inputs including examples aligned with the product brief, such as:

- Time-only on **today’s date:** `7pm`, `19:00`
- Date + time: `2026-03-17 19:00`, `17/03/2026 7pm`
- Legacy compact forms: `yyyy-MM-dd HHmm`, `d/M/yyyy HHmm`
- Date-only (end of day): `yyyy-MM-dd`, `d/M/yyyy`

Throws `DateTimeParseException` when no pattern matches.

---

## 5. Target modular API (planned refactor)

The following table is the **intended** separation of concerns (names match the architecture docs). Refactoring can map current `Xmoke` logic into these modules without changing the user-visible command grammar.

| Module | Responsibility |
|--------|----------------|
| `CommandController` | Receives raw command text from the patient UI and routes it to the right service; enforces **role** (patient vs caregiver). |
| `CommandParser` | Validates syntax and maps lines to structured command objects. |
| `RoutineService` | `record` / `skip` / same-day overwrite / today routine state. |
| `DeadlineService` | Deadlines + flexible parsing delegation to `Parser`. |
| `ExerciseService` | Exercise logs. |
| `SymptomService` | Symptoms with severity 1–10. |
| `DailyNoteService` | Daily notes. |
| `ListService` | Builds the **today** view for `list`. |
| `SummaryService` | Weekly aggregates (routines, skips, symptoms, notes, exercise, deadlines). |
| `ExportService` | CSV / PDF export of weekly (or monthly) summaries. |

---

## 6. Reference: target service-style API signatures

These signatures describe the **target** internal API (implementation may still be inlined in `Xmoke` today).

| API | Purpose |
|-----|---------|
| `CommandController.handleCommand(rawText, role)` | Validates caller; dispatches to parser + services. |
| `CommandParser.parse(rawText)` | Returns structured commands (`RecordCommand`, `SkipCommand`, …). |
| `RoutineService.recordRoutine(item, date, note)` | Same-day overwrite for fixed items. |
| `RoutineService.skipRoutine(item, date, reason)` | Skip state + reason. |
| `RoutineService.getTodayRoutineStatus(date)` | Data for list UI. |
| `DeadlineService.addDeadline(description, dueAt)` | Persist deadline. |
| `DeadlineService.parseFlexibleDateTime(rawTime, baseDate)` | Normalize times (partially covered by `Parser.parseDateTime` today). |
| `ExerciseService.addExercise(date, description)` | Exercise entry. |
| `SymptomService.addSymptom(date, description, severity)` | Symptom entry. |
| `DailyNoteService.addDailyNote(date, text)` | Note entry. |
| `ListService.buildTodayList(date)` | Today composite view. |
| `SummaryService.generateWeeklySummary(weekStart)` | Weekly rollup. |
| `ExportService.exportWeeklyCsv(summary, filePath)` | CSV export. |
| `ExportService.exportWeeklyPdf(summary, filePath)` | PDF export. |

---

## 7. Key data contracts

### Current implementation (chatbot persistence)

Health chatbot entries may be stored in a compact **encoded** form inside the existing `Task` / task-list persistence (e.g. prefixes such as `ROUTINE|…`, `SYMPTOM|…`, `EXERCISE|…`, `NOTE|…`, `DEADLINE|…`) until merged fully with `User` / `Day` records.

### Target structured types (planned)

| Object | Fields (target) |
|--------|-----------------|
| `RoutineUpdateResult` | `success, item, date, finalStatus, savedAt, noteOrReason, message` |
| `CommandResult` | `success, message, commandType, payload, errorCode` |
| `TodayListView` | `date, routineItems[], deadlines[]` |
| `RoutineStatusView` | `label, scheduledTime, state, noteOrReason` |
| `WeeklySummary` | `weekStart, adherenceMetrics, symptomTrendPoints, exerciseLogs, dailyNotes, deadlineList` |
| `ExportResult` | `success, filePath, generatedAt, errorMessage` |

---

## 8. Validation rules

- Only the **supported** chatbot grammar is accepted; anything else yields an error message.
- Symptom severity must be an integer from **1–10** (parsed from the **last** token).
- Only the five fixed routine items are valid for `record` / `skip`.
- `record` **overwrites** the same-day status for the same fixed item.
- **Planned:** Caregiver role must not trigger write paths through the command controller.
- Deadline input should normalize to one internal `LocalDateTime` (see §4).

---

## 9. What we plan to change next (short roadmap)

1. **Unify data model:** Persist chatbot health entries through `User` / `Day` (or dedicated entities) instead of long-term reliance on encoded task lines, where applicable.
2. **Extract services:** Move logic from `Xmoke` into `RoutineService`, `SymptomService`, etc., behind a thin `CommandController`.
3. **Roles:** Thread `UserRole` (patient vs caregiver) from UI into command handling and block writes for caregivers.
4. **Summaries & export:** Implement weekly summary per PRD/SDD; add PDF if required; align naming with `ExportService` / `SummaryService`.
5. **Tests:** Add parser and command tests for each grammar rule and edge cases (same-day overwrite, invalid severity).
