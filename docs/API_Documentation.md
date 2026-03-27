# API Documentation
## Personal Health Routine Tracker (Desktop App, Non-Medical)

This document describes the internal APIs exposed between the main modules of the Personal Health Routine Tracker desktop application. Although the system is not a web service, it still uses clear internal module boundaries so that the UI, parser, services, repositories, and export logic remain maintainable and testable.

- **Scope:** Internal module APIs only (not public network APIs).
- **Product type:** Desktop application with strict command-based input; no LLM or natural language inference.
- **Roles:** Patient (data entry) and Caregiver (view-only summaries and logs).

---

## 1. Core Modules

| Module | Responsibility |
|---|---|
| CommandController | Receives raw command text from the patient UI and routes it to the right service. |
| CommandParser | Validates syntax and converts strict commands into structured command objects. |
| RoutineService | Handles record/skip/list logic for fixed daily tasks such as meals and medications. |
| DeadlineService | Creates and retrieves one-off deadlines with flexible time parsing. |
| ExerciseService | Stores daily exercise entries. |
| SymptomService | Stores symptom description with severity from 1-10. |
| DailyNoteService | Stores freeform special notes for the day. |
| ListService | Builds the today view containing fixed tasks and deadlines. |
| SummaryService | Calculates adherence and symptom trends for weekly review. |
| ExportService | Exports weekly summary to CSV and PDF. |

---

## 2. Command Grammar Supported by the Parser

| Command | Intent |
|---|---|
| `record breakfast [optional note]` | Mark breakfast as done; overwrite same-day value if it already exists. |
| `record lunch [optional note]` | Mark lunch as done. |
| `record dinner [optional note]` | Mark dinner as done. |
| `record medication1 [optional note]` | Mark medication1 as done. |
| `record medication2 [optional note]` | Mark medication2 as done. |
| `skip <fixed item> [description]` | Mark fixed routine item as skipped and save reason/description. |
| `deadline [description] [time]` | Add one deadline using flexible time parsing. |
| `exercise [description]` | Save a freeform exercise log. |
| `symptom [description] [severity]` | Save symptom with integer severity 1-10. |
| `note [description]` | Save a freeform special note for the day. |
| `list` | Return today's routine status and deadlines. |

---

## 3. API Reference

### `CommandController.handleCommand(rawText: String, role: UserRole)`

Entry point for all command-based interactions. Validates the caller role, invokes the parser, and forwards the parsed command to the matching service.

| Parameter / Input | Type | Description |
|---|---|---|
| `rawText` | `String` | Original user input from the command box, e.g. `record lunch felt full`. |
| `role` | `UserRole` | `PATIENT` or `CAREGIVER`. Only `PATIENT` can create or update records. |

**Return value:** `CommandResult` containing success flag, user-facing message, optional payload for the UI, and error code if parsing fails.  
**Example usage:** `handleCommand("list", PATIENT)`

---

### `CommandParser.parse(rawText: String)`

Parses a strict command into a structured command object. No natural language inference is used.

| Parameter / Input | Type | Description |
|---|---|---|
| `rawText` | `String` | One command line entered by the user. |

**Return value:** `ParsedCommand` subtype such as `RecordCommand`, `SkipCommand`, `DeadlineCommand`, `ExerciseCommand`, `SymptomCommand`, `NoteCommand`, or `ListCommand`.  
**Example usage:** `parse("symptom headache after lunch 6")`

---

### `RoutineService.recordRoutine(item: RoutineItem, date: LocalDate, note: String?)`

Marks a fixed daily routine item as done. If the item already has a same-day status, the old value is overwritten.

| Parameter / Input | Type | Description |
|---|---|---|
| `item` | `RoutineItem` | `BREAKFAST`, `LUNCH`, `DINNER`, `MEDICATION1`, or `MEDICATION2`. |
| `date` | `LocalDate` | Date to update, usually today's date. |
| `note` | `String?` | Optional note supplied after the command. |

**Return value:** `RoutineUpdateResult` with final status, saved timestamp, and display message.  
**Example usage:** `recordRoutine(MEDICATION1, today, "after breakfast")`

---

### `RoutineService.skipRoutine(item: RoutineItem, date: LocalDate, reason: String?)`

Marks a fixed daily routine item as skipped and saves the optional reason/description.

| Parameter / Input | Type | Description |
|---|---|---|
| `item` | `RoutineItem` | Fixed routine task being skipped. |
| `date` | `LocalDate` | Date to update. |
| `reason` | `String?` | Optional explanation such as `felt tired`. |

**Return value:** `RoutineUpdateResult`  
**Example usage:** `skipRoutine(BREAKFAST, today, "slept late")`

---

### `RoutineService.getTodayRoutineStatus(date: LocalDate)`

Returns today's fixed routine schedule together with its current done/skipped/not-done state for list rendering.

| Parameter / Input | Type | Description |
|---|---|---|
| `date` | `LocalDate` | Target day. |

**Return value:** `List<RoutineStatusView>` where each item contains label, scheduled time, state, and note/reason if present.  
**Example usage:** `getTodayRoutineStatus(today)`

---

### `DeadlineService.addDeadline(description: String, dueAt: LocalDateTime)`

Creates a user deadline. The incoming date/time value is expected to come from a flexible time parser.

| Parameter / Input | Type | Description |
|---|---|---|
| `description` | `String` | User-entered deadline description. |
| `dueAt` | `LocalDateTime` | Normalized deadline date and time. |

**Return value:** `DeadlineRecord` containing id, description, normalized due time, and created timestamp.  
**Example usage:** `addDeadline("drink water", 2026-03-17T19:00)`

---

### `DeadlineService.parseFlexibleDateTime(rawTime: String, baseDate: LocalDate)`

Attempts to normalize several common date/time formats into one `LocalDateTime` object.

| Parameter / Input | Type | Description |
|---|---|---|
| `rawTime` | `String` | Examples: `7pm`, `19:00`, `17/03/2026 7pm`, `2026-03-17 19:00`. |
| `baseDate` | `LocalDate` | Used when the input only contains time and no date. |

**Return value:** `ParsedDateTimeResult` containing normalized `LocalDateTime` or a validation error.  
**Example usage:** `parseFlexibleDateTime("7pm", today)`

---

### `ExerciseService.addExercise(date: LocalDate, description: String)`

Adds a daily exercise log entry. Multiple exercise entries may exist on the same day.

| Parameter / Input | Type | Description |
|---|---|---|
| `date` | `LocalDate` | Log date. |
| `description` | `String` | Exercise text such as `swimming 20 minutes`. |

**Return value:** `ExerciseRecord`  
**Example usage:** `addExercise(today, "swimming 20 minutes")`

---

### `SymptomService.addSymptom(date: LocalDate, description: String, severity: Int)`

Stores one symptom entry for the day. Severity must be validated as an integer from 1-10.

| Parameter / Input | Type | Description |
|---|---|---|
| `date` | `LocalDate` | Log date. |
| `description` | `String` | Freeform symptom description. |
| `severity` | `Int` | Severity from 1 to 10 inclusive. |

**Return value:** `SymptomRecord`  
**Example usage:** `addSymptom(today, "headache after lunch", 6)`

---

### `DailyNoteService.addDailyNote(date: LocalDate, text: String)`

Stores a special note for the day.

| Parameter / Input | Type | Description |
|---|---|---|
| `date` | `LocalDate` | Log date. |
| `text` | `String` | Freeform note text. |

**Return value:** `DailyNoteRecord`  
**Example usage:** `addDailyNote(today, "went out with caregiver in the afternoon")`

---

### `ListService.buildTodayList(date: LocalDate)`

Builds the output shown for the `list` command. Combines routine states and pending deadlines into one formatted view model.

| Parameter / Input | Type | Description |
|---|---|---|
| `date` | `LocalDate` | Target day. |

**Return value:** `TodayListView` containing fixed tasks, statuses shown as `[X]` / `[ ]`, notes/reasons, and today's deadlines.  
**Example usage:** `buildTodayList(today)`

---

### `SummaryService.generateWeeklySummary(weekStart: LocalDate)`

Aggregates adherence rates, symptom trends, deadlines, exercise, and special notes for one week.

| Parameter / Input | Type | Description |
|---|---|---|
| `weekStart` | `LocalDate` | Start date of the reporting week. |

**Return value:** `WeeklySummary` object with completion counts, skip counts, symptom timeline, and supporting notes.  
**Example usage:** `generateWeeklySummary(2026-03-16)`

---

### `ExportService.exportWeeklyCsv(summary: WeeklySummary, filePath: String)`

Writes weekly summary data into CSV format for doctor/caregiver review.

| Parameter / Input | Type | Description |
|---|---|---|
| `summary` | `WeeklySummary` | Aggregated report object. |
| `filePath` | `String` | Destination path. |

**Return value:** `ExportResult` with success flag and generated file location.  
**Example usage:** `exportWeeklyCsv(summary, "exports/weekly_summary.csv")`

---

### `ExportService.exportWeeklyPdf(summary: WeeklySummary, filePath: String)`

Renders the weekly summary into a simple PDF report that can be shared with a doctor or caregiver.

| Parameter / Input | Type | Description |
|---|---|---|
| `summary` | `WeeklySummary` | Aggregated report object. |
| `filePath` | `String` | Destination path. |

**Return value:** `ExportResult`  
**Example usage:** `exportWeeklyPdf(summary, "exports/weekly_summary.pdf")`

---

## 4. Key Data Contracts

| Object | Fields |
|---|---|
| `RoutineUpdateResult` | `success, item, date, finalStatus, savedAt, noteOrReason, message` |
| `CommandResult` | `success, message, commandType, payload, errorCode` |
| `TodayListView` | `date, routineItems[], deadlines[]` |
| `RoutineStatusView` | `label, scheduledTime, state, noteOrReason` |
| `WeeklySummary` | `weekStart, adherenceMetrics, symptomTrendPoints, exerciseLogs, dailyNotes, deadlineList` |
| `ExportResult` | `success, filePath, generatedAt, errorMessage` |

---

## 5. Validation Rules

- Only the strict command set is accepted. Commands outside the grammar return a parser error.
- Symptom severity must be an integer from 1-10.
- Only five fixed routine items are supported: `breakfast`, `lunch`, `dinner`, `medication1`, `medication2`.
- The `record` command overwrites the previous same-day status for the same fixed item.
- The caregiver role is read-only and cannot call write methods through the UI controller.
- Deadline parsing should normalize several common time formats into one internal `LocalDateTime` format.
