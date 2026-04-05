# Healthcare App for Tracking Daily Activities for Seniors

## 1. Product Overview

This application is a desktop GUI-based system designed to help seniors and caregivers manage essential daily and weekly routines, such as medication intake, supplements, hydration, and exercise.

In addition, the system allows users to record simple daily wellbeing logs (e.g., headache, sleep quality, appetite), enabling long-term tracking of health-related patterns.

The application stores records over time and provides weekly or monthly summaries, which can be reviewed by caregivers or shared with healthcare professionals to support better monitoring and communication.

The system is intended for routine tracking and record-keeping purposes only and does not provide medical diagnosis or treatment recommendations.

---

## 2. Problem Statement

Seniors may forget to perform essential daily or weekly activities such as taking medication, maintaining hydration, or following exercise routines. This can negatively affect their health and overall wellbeing.

At the same time, caregivers and healthcare professionals often lack consistent and structured records of a senior’s daily condition, making it difficult to identify patterns or issues over time.

Existing solutions may be too complex, require constant internet access, or are not tailored for elderly users.

Therefore, there is a need for a simple and user-friendly application that helps seniors track routines and log daily wellbeing, while allowing caregivers to review and monitor this information easily.

---

## 3. Target Users / Stakeholders

### Primary Users
- Seniors who need assistance in remembering and tracking daily or weekly routines
- Seniors who want to log their daily wellbeing in a simple way

### Secondary Users
- Caregivers (family members or helpers) who want to monitor the senior’s condition but are not always physically present
- Healthcare professionals who may review summary reports during medical consultations or checkups

---

## 4. User Stories

- As a senior, I want to view my daily routines, so that I know what tasks I need to complete today.
- As a senior, I want to mark routines as completed, so that I can keep track of what I have done.
- As a senior, I want to log how I feel each day, so that my condition can be tracked over time.
- As a senior, I want the system to be simple and easy to use, so that I do not feel overwhelmed.

- As a caregiver, I want to review a senior’s weekly records, so that I can monitor their wellbeing.
- As a caregiver, I want to see if routines are being followed, so that I can identify any issues.
- As a caregiver, I want to generate a summary report, so that it can be shared with a doctor.
- As a caregiver, I want to add, edit, or delete routines, so that I can adapt them according to changing needs or medical advice.

- As a user, I want my data to be saved automatically, so that I do not lose my records.
- As a user, I want the system to remember my profile, so that my data can be differentiated from other users.

---

## 5. Functional Requirements

### 5.1 User Management
- The system shall allow caregivers to add new senior users
- The system shall store each user’s data in separate folders
- The system shall support caregiver authentication via password

### 5.2 Routine Management
- The system shall allow caregivers to:
    - Add daily routines
    - Add weekly routines
    - Remove routines
- The system shall prevent duplicate routines

### 5.3 Task Tracking
- The system shall display daily and weekly routines to seniors
- The system shall allow seniors to mark tasks as completed
- The system shall persist task completion immediately

### 5.4 Daily Log
- The system shall allow seniors to record daily logs in free-text form
- The system shall allow editing of existing logs
- Logs shall be stored per date

### 5.5 History Viewing
- The system shall allow caregivers to view weekly completion summaries
- The system shall display:
    - completion counts
    - missed days
    - weekly completion status

### 5.6 Summary Generation
- The system shall generate monthly summary reports
- Reports shall include:
    - completion rates
    - task statistics
    - daily logs
    - detailed completion history
- Reports shall be exported as CSV files

---

## 6. Non-Functional Requirements

### Usability
- Simple and intuitive GUI suitable for seniors
- Minimal steps for completing tasks

### Performance
- System should respond within 1 second for typical actions
- Summary generation should complete within a few seconds

### Reliability
- Data must persist across sessions
- System should handle invalid input without crashing

### Security
- Data stored locally
- Caregiver password required for administrative actions

### Portability
- Runs on desktop platforms supporting Java

### Maintainability
- Modular structure (Model, Storage, UI)