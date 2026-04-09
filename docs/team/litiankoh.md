# Koh Li Tian - Project Portfolio Page

## Overview

**Project:** Record Losses And Debt (RLAD)

RLAD is a minimalist **Command-Line Interface (CLI) finance tracker** built for power users who prefer the speed and 
efficiency of a terminal. Unlike bloated apps or complex spreadsheets, RLAD allows users to track income/expenses, 
set budget goals, and get instant summaries with simple, intuitive commands. 
My primary role was to architect and implement the **budgeting feature**, which forms the core of RLAD's financial planning capabilities.
---
## Summary of Contributions

**Primary Role:** Architect and implementor of the core adding transactions and budgeting feature.

**Code contributed:** [RepoSense Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=litiankoh&breakdown=true)

---
### Enhancements implemented

Here is a summary of my key enhancements to RLAD:

| Enhancement | Description | Key Technical Highlights |
| :--- | :--- | :--- |
| **AddCommand & Argument Parsing** | Built the foundational command for adding transactions with a position-based parser — no flags needed. | - Position-based parsing: `type`, `amount`, `date`, `category`, `description` in natural order<br>- Quote handling for multi-word descriptions<br>- `add debit 15.50 2026-03-05 food` instead of `add --type debit --amount 15.50 --date 2026-03-05 --category food` |
| **Budget Management System** | Full suite of commands (`set`, `view`, `edit`, `delete`) for monthly budget goals across 12 categories. | - Designed `MonthlyBudget` and `BudgetManager` classes for storage and calculations<br>- Integrated with `TransactionManager` for real-time spend tracking<br>- Simplified syntax: `budget set 2026-03 1 500` instead of `budget set --month 2026-03 --category 1 --amount 500` |
| **Progressive Budget Notifications** | Real-time alerts at 80%, 90%, and 100% of budget limits. | - `Map<String, Set<Integer>>` tracks notifications per `(month, category)` to prevent duplicates<br>- Positive messages for Savings; warnings for all other categories |

---

### Contributions to User Experience Design

I led the effort to **redesign the entire CLI syntax** from flag-based to position-based commands, making RLAD significantly more user-friendly:

**Before (flag-based):**
```
add --type debit --amount 15.50 --date 2026-03-05 --category food --description "Chicken rice"
```
**After (position-based):**
```
add debit 15.50 2026-03-05 food "Chicken rice"
```
---
### Contributions to the User Guide (UG)

I authored and maintained the documentation for the `budget` feature, ensuring it was clear, comprehensive, and user-friendly. My contributions include:

- **Added UG for BudgetManager:** Wrote detailed sections for all budget commands (`set`, `view`, `edit`, `delete`) with examples, input formats, and expected outputs.
- **Added UG for Budget Notification Logic:** Documented the progressive notification system (80%, 90%, 100% thresholds), clearly differentiating between Savings (positive) and other categories (warnings).
---
### Contributions to the Developer Guide (DG)

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

---

### Team-Based & Beyond-Project Contributions

- **Essential Code Foundation:** Created the `AddCommand` with position-based parsing, which served as a template for all other commands, enabling the team to quickly implement consistent, user-friendly commands.
- **Leadership:** Led the team discussion. Managed the project's workflow and ensuring all team members had clear, well-defined tasks.
- **Code Review:** Actively reviewed team members' PRs to ensure they followed the new position-based pattern consistently.
