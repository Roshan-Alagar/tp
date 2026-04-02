# Koh Li Tian - Project Portfolio Page

## Overview

**Project:** Record Losses And Debt (RLAD)

RLAD is a minimalist **Command-Line Interface (CLI) finance tracker** built for power users who prefer the speed and 
efficiency of a terminal. Unlike bloated apps or complex spreadsheets, RLAD allows users to track income/expenses, 
set budget goals, and get instant summaries with simple, intuitive commands. 
My primary role was to architect and implement the **budgeting feature**, which forms the core of RLAD's financial planning capabilities.

## Summary of Contributions

### Code contributed
[RepoSense Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=litiankoh&breakdown=true)

### Enhancements implemented

Here is a summary of my key enhancements to RLAD:

| Enhancement | Description | Key Technical Highlights |
| :--- | :--- | :--- |
| **AddCommand & Argument Parsing** | Built the foundational command for adding transactions, including a custom argument parser for flag-based inputs. | - Implemented a state-based parser to handle flags, values, and quoted strings (e.g., descriptions with spaces).<br>- Provided clear validation errors for missing required fields (`--type`, `--amount`, `--date`), invalid amounts, and wrong date formats. |
| **Budget Management System** | Implemented a full suite of commands (`set`, `view`, `edit`, `delete`) for users to create and manage monthly budget goals across 12 predefined categories. | - Designed `MonthlyBudget` and `BudgetManager` classes to handle budget storage and calculations.<br>- Integrated with existing `TransactionManager` to calculate real-time spending against budgets.<br>- Implemented robust input validation for month formats and category codes. |
| **Progressive Budget Notifications** | Created a real-time notification system that alerts users at 80%, 90%, and 100% of their budget limits. | - Used a `Map<String, Set<Integer>>` to track notifications per `(month, category)` and prevent duplicate alerts.<br>- Implemented conditional logic to show **positive/encouraging messages for Savings** and **warnings for all other categories**, enhancing user experience. |
| **Yearly Budget Summary with Trend Graph** | Developed a `budget yearly` command that provides a comprehensive annual financial overview. | - Generated a formatted, 12-month table with ASCII progress bars for visual trend analysis.<br>- Calculated and displayed per-category yearly totals, highlighting over/under budget status.<br>- Computed annual totals and monthly averages for a complete financial snapshot. |


### Contributions to the User Guide (UG)

I authored and maintained the documentation for the `budget` feature, ensuring it was clear, comprehensive, and user-friendly. My contributions include:

- **Added UG for BudgetManager:** Wrote detailed sections for all budget commands (`set`, `view`, `edit`, `delete`) with examples, input formats, and expected outputs.
- **Added UG for Budget Notification Logic:** Documented the progressive notification system (80%, 90%, 100% thresholds), clearly differentiating between Savings (positive) and other categories (warnings).

### Contributions to the Developer Guide (DG)
I made substantial contributions to the Developer Guide, documenting the architecture and implementation of key features to help the team understand and maintain the codebase.\
Added all the UMLs shown in the DG as well.

| Section | Contribution                                                                                                                                                        |
|---------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|3. Design | Authored the Architecture Overview, Component Descriptions, and all ASCII UML class diagrams showing relationships between core components.                         |
| 4.1 Add Transaction | Documented the complete implementation sequence, including parser logic, validation steps, and integration with TransactionManager.                                 |
| 4.7 Budget Management | Wrote comprehensive documentation for the entire budget feature, including sequence diagrams, class interactions, and progress tracking logic.                      |
| 4.8 Storage Management | Documented the CSV export/import and clear data feature, including sequence diagrams, parser changes, and required methods for TransactionManager and BudgetManager. |
| 6. User Stories | Authored the complete user stories table covering v1.0 and v2.0 features.                                                                                           |
| 7. Non-Functional Requirements | Defined all NFRs including performance, reliability, portability, data integrity, maintainability, and usability.                                                   |
| 8. Glossary | Created the glossary of key terms used throughout the DG.|
| 9. Instructions for Manual Testing | Authored the complete manual testing guide covering all 29 test cases across add, list, sort, summarize, modify, delete, budget, export, import, clear, and exit commands. |

### Contributions to Team-Based Tasks

I played a key role in the project's foundational setup and ongoing quality.

- **Essential Code Foundation:** Created the `AddCommand`, which was the first fully functional command in RLAD. This required implementing:
    - A robust argument parser to handle flags (e.g., `--type`, `--amount`, `--date`) and quoted descriptions.
    - Comprehensive input validation for required fields, amount formats, and date formats (yyyy-MM-dd).
    - Seamless integration with `TransactionManager` for storage and `Ui` for user feedback. 
    - This command served as a blueprint for other team members to implement subsequent commands (`Delete`, `Modify`), accelerating the team's development velocity.
- **Enhanced User Experience:** Incorporated a visual **progress bar** into the budget view, providing an intuitive, at-a-glance understanding of budget consumption.

### Reviewing Contributions (Mentoring)

I actively reviewed team members' pull requests to ensure code quality, adherence to standards, and functional correctness.

- [PR #18: ListCommand Implementation](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/18)
- [PR #22: ModifyCommand Implementation](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/22)
- [PR #39: BudgetManager Persistence](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/39)
- [PR #40: BudgetCommand Refactoring](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/40)
- [PR #45: TransactionManager Enhancements](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/45)
- [PR #52: Implement Budget Yearly Summary Command](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/52#pullrequestreview-4051302843)
- [PR #53: Enchanced FilterCommand](https://github.com/AY2526S2-CS2113-W13-4/tp/pull/53)

### Contributions beyond the project team

I took a proactive leadership role by managing the project's workflow and ensuring all team members had clear, well-defined tasks.

- **Project Management & Task Delegation:** Proactively created and assigned detailed GitHub issues for the entire team, ensuring a fair distribution of work and that everyone had an opportunity to contribute meaningfully to different parts of the project.

**Issues created:**

- [Issue #20: Implement SetBudgetCommand (Logic)](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/20)
- [Issue #26: Implement Budget Persistence](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/26)
- [Issue #46: Budget Goals - Progressive Notifications](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/46)
- [Issue #47: Yearly Budget Summary with Trend Graph](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/47)
- [Issue #49: Search and Edit Specific Transaction](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/49)
- [Issue #50: Update UG and DG](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/50)
- [Issue #55: Implement Storage Management System (CSV Export/Import & Data Clear)](https://github.com/AY2526S2-CS2113-W13-4/tp/issues/55)