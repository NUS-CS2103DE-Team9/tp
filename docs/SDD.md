# Software Design Document (SDD)

## System Overview

The Healthcare App for Tracking Daily Activities for Seniors is a desktop GUI-based application designed to help seniors and caregivers manage recurring routines and record daily wellbeing logs.

The system allows users to:
- manage daily or weekly routines such as medication, hydration, supplements, or exercise
- record daily wellbeing logs in free-text form
- store records persistently for later review
- generate weekly or monthly summaries for caregivers or healthcare professionals

The system is intended for routine tracking and record-keeping purposes only. It does not provide medical diagnosis or treatment recommendations.

---

## Architecture Design

The system follows a modular layered architecture consisting of three main components:

- **UI**
- **Model**
- **Storage**

The responsibilities of the logic layer are distributed across controllers and model classes.

### Component Interaction

1. The **UI** handles user interaction through a graphical interface.
2. Controllers process user actions and coordinate system behavior.
3. The **Model** stores and manages application data.
4. The **Storage** handles reading and writing data to local files.

This structure ensures separation of concerns while keeping the implementation simple.

---

## Major System Components

### UI Component

The UI component is responsible for:
- displaying the graphical interface using JavaFX
- allowing users to interact with routines and daily logs
- presenting summaries and historical data
- forwarding user actions to controllers
- displaying feedback such as success messages or errors

Key UI views include:
- login view (user selection)
- senior task checklist view
- daily log input view
- caregiver menu view
- history view
- summary generation view

The UI is designed to be simple, readable, and suitable for senior users.

---

### Model Component

The Model component represents the core data structures of the system.

It is responsible for:
- storing routines and their properties
- storing daily wellbeing logs
- maintaining user profile data
- organizing historical records

Key model classes include:

#### User
- represents a single user
- stores daily and weekly routines
- stores a list of day records
- provides methods to retrieve or create day data

#### Task
- represents a recurring routine
- contains description and routine type

#### TaskList
- manages a collection of tasks
- supports addition, removal, and lookup

#### Day
- represents a single day’s record
- contains:
  - date
  - log (free-text)
  - daily task completion status
  - weekly task completion status
- handles synchronization with routines

#### Enums
- Role (SENIOR, CAREGIVER)
- RoutineType (DAILY, WEEKLY)

---

### Storage Component

The Storage component handles persistent data storage using local files.

It is responsible for:
- saving user profiles, routines, and logs
- loading data when the application starts
- organizing data into user-specific folders
- handling caregiver authentication

The storage structure is:

- data/ 
  - app/ 
    - caregiver.txt 
  - users/
    - senior1/ 
      - profile.txt 
      - dailyRoutines.txt 
      - weeklyRoutines.txt 
      - days/ 
        - YYYY-MM-DD.txt
    - senior2/
    - ...


Each day file stores:
- log text
- daily task completion
- weekly task completion

---

### Summary Component

The system includes a summary generation component.

#### SummaryGenerator
- generates monthly summary reports in CSV format
- computes:
  - completion rates
  - task statistics
  - daily logs
  - detailed history
- uses a rolling 30-day period 

---

## Data Flow

A typical system interaction follows this sequence:

1. The user performs an action in the GUI.
2. The controller processes the action.
3. The Model is updated.
4. The Storage component saves the updated data.
5. The UI refreshes to reflect the new state.

---

## UML Diagrams

### Use Case Diagram

![Use Case Diagram](images/Use Case Diagram.png)

---

### Class Diagram

![Class Diagram](images/Class Diagram.png)

---

### Sequence Diagrams

**Scenario**: Senior marking a routine as completed

1. Senior selects a task checkbox
2. Controller receives the action
3. Controller updates the corresponding Day object
4. Controller calls Storage.saveUser()
5. Storage writes updated data to file
6. UI updates display

![Sequence Diagram](images/Sequence Diagram.png)

---

## Key Design Decisions

### 1. Desktop GUI-based design

The system is implemented as a desktop GUI application to improve accessibility for seniors.

A graphical interface is easier to understand and interact with compared to command-line interfaces.

---

### 2. Simplified layered architecture

The system uses UI, Model, and Storage components.

Controllers handle interaction logic directly instead of introducing a separate logic layer.

This reduces complexity while maintaining separation of concerns.

---

### 3. File-based storage

The system uses local file storage instead of a database.

Advantages:
- supports offline usage
- requires no setup
- easy to inspect and debug

---

### 4. Day-based data organization

Each day is stored as a separate record.

This allows:
- tracking historical data
- generating summaries
- preventing data overwrite

---

### 5. Immediate persistence

All updates are saved immediately after user actions.

This ensures:
- minimal data loss risk
- consistent system state

---

### 6. Free-text logging

Daily logs are stored as free-text entries.

This allows seniors to:
- record information naturally
- avoid complex input structures