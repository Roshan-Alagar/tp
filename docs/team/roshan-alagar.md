# Roshan Alagar - Project Portfolio Page

## Overview

**Project:** Record Losses And Debt (RLAD)

RLAD is a minimalist **Command-Line Interface (CLI) finance tracker** built for power users who prefer the speed and
efficiency of a terminal. Unlike bloated apps or complex spreadsheets, RLAD allows users to track income/expenses,
set budget goals, and get instant summaries with simple, intuitive commands.
My primary role was to implement the **transaction listing and filtering feature**, the **delete command**, and the
**budget yearly summary**, which together form the core of RLAD's data retrieval and reporting capabilities.

---

## Summary of Contributions

### Code Contributed
[RepoSense Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=roshan-alagar&breakdown=true)

---

### Enhancements Implemented

| Enhancement | Description | Key Technical Highlights |
| :--- | :--- | :--- |
| **ListCommand — Transaction Filtering and Sorting** | Implemented the full `list` command allowing users to view, filter, and sort their transactions using any combination of supported flags. | - Supports filtering by `--type`, `--category`, `--amount` (with operators `-gt`, `-gte`, `-eq`, `-lt`, `-leq`), `--date`, `--date-from`, `--date-to`.<br>- Supports one-off sorting via `--sort date` or `--sort amount`.<br>- Displays results in a formatted table with transaction count.<br>- Handles "Empty Wallet" state gracefully. |
| **FilterCommand — Shared Filtering Logic** | Implemented `FilterCommand.parseFlags()` and `FilterCommand.buildPredicate()` as shared utilities used by both `ListCommand` and `SummarizeCommand`. | - Used Java `Predicate<Transaction>` chaining with `.and()` for clean, composable filtering.<br>- Validates flag values (e.g., `--type` must be `credit` or `debit`, dates must follow `yyyy-MM-dd`).<br>- Supports amount comparison operators for flexible numeric filtering. |
| **DeleteCommand — Remove Transactions by ID** | Implemented the `delete` command to permanently remove a transaction from `TransactionManager` by its HashID. | - Validates `--hashID` flag presence and value before execution.<br>- Throws `RLADException` with clear messages for missing or non-existent IDs.<br>- Shows success message with full details of the deleted transaction. |
| **Budget Yearly Summary — `budget yearly`** | Implemented the `budget yearly` subcommand providing a comprehensive 12-month financial overview. | - Displays a monthly table with budget, spent, remaining, percentage used, and ASCII progress bar.<br>- Includes per-category yearly breakdown with over/under status.<br>- Shows annual totals: total budget, total spent, net balance, and average monthly spending.<br>- Supports `--year YYYY` flag with validation (2000–2100), defaulting to current year. |

---

### Contributions to the User Guide (UG)

I authored and maintained the documentation for the features I implemented:

- **ListCommand (Section 4.4):** Documented all supported filter flags, amount operators, sorting options, and provided expected output examples.
- **DeleteCommand (Section 4.2):** Documented the `--hashID` parameter, expected output, and added a warning about permanent deletion.
- **Budget Yearly Summary (Section 4.7.5):** Wrote the full section for `budget yearly` including format, parameters, and expected output.

---

### Contributions to the Developer Guide (DG)

| Section | Contribution |
|---------|-------------|
| Architecture Overview | Authored the MVC architecture diagram showing interactions between `Parser`, `Command`, `TransactionManager`, `BudgetManager`, and `Ui`. |
| 4.3 ListCommand Implementation | Documented the complete execution sequence including flag parsing, predicate building, filtering, sorting, and display. |
| 4.4 FilterCommand Implementation | Documented `parseFlags()` and `buildPredicate()` with code snippets showing predicate chaining logic. |
| 4.5 DeleteCommand Implementation | Documented the validation and deletion flow including error handling cases. |
| Manual Testing Instructions | Authored test cases for `add`, `list`, `delete`, `summarize`, and `budget yearly` commands. |

---

### Testing

| Test File | Tests | Coverage |
|-----------|-------|----------|
| `ListCommandTest` | 17 tests | Filtering by type, category, amount operators, date, date range; sorting by date and amount; combined filters; empty results; data immutability |
| `DeleteCommandTest` | 14 tests | Valid deletion, missing flag, null args, non-existent ID, correct transaction deleted when multiple exist, `TransactionManager` not modified on failure |

---

### Contributions to Team-Based Tasks

- **Defensive Programming:** Added Java `assert` statements to `ListCommand` and `FilterCommand` for runtime safety.
- **Logging:** Added `java.util.logging.Logger` to `ListCommand` and `FilterCommand` for runtime diagnostics.
- **Assertions in Build:** Added `jvmArgs '-ea'` to `build.gradle` to enable assertions in the team repo's CI pipeline.
- **Milestone Setup:** Created the `v2.0` milestone with a deadline on GitHub for the team repo.

---

### Reviewing Contributions

I actively reviewed teammates' pull requests to ensure code quality and correctness:

- Reviewed `SummarizeCommand` and identified a bug where null categories were not handled correctly, causing CI failures across all PRs.
- Left constructive comments on `TransactionManager` enhancements regarding null guard for `addTransaction(null)`, redundant casts, and stale TODO comments.
- Left comments on the architecture flowchart PR regarding tight coupling between `TransactionManager` and `BudgetManager`, and suggested improvements to the diagram.