# RLAD - Record Losses And Debt

A minimalistic, user-centric financial tracker.

## Project Structure

```
src/main/java/seedu/RLAD/
├── RLAD.java                 # Main entry point
├── Parser.java               # Parses user input, creates Command objects
├── Transaction.java          # Transaction data model
├── TransactionManager.java   # Data storage (Model layer)
├── Ui.java                   # User interface / output display
├── Logo.java                 # ASCII logo
├── exception/
│   └── RLADException.java    # Custom exception
└── command/
    ├── Command.java          # Abstract base class
    ├── AddCommand.java       # Add new transaction
    ├── DeleteCommand.java    # Delete transaction by ID
    ├── ModifyCommand.java    # Modify existing transaction
    ├── ListCommand.java      # List transactions (with filtering)
    ├── FilterCommand.java    # Helper: filtering logic (Predicate)
    ├── SummarizeCommand.java # Summarize transactions
    └── HelpCommand.java      # Show help
```

## Architecture

The project follows the **MVC pattern** with the **Command Design Pattern**:

```
┌─────────────────────────────────────────────────────────────────┐
│                           USER INPUT                            │
│               (e.g., "add --type credit --amount 50")           │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  PARSER (Parser.java)                                           │
│  • Validates command format                                     │
│  • Acts as Factory - creates appropriate Command object         │
│  • Does NOT interact with TransactionManager                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                    returns Command object
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  COMMAND (Command.java - base class)                            │
│                                                                 │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐             │
│  │ AddCommand   │ │DeleteCommand │ │ ListCommand  │  ...        │
│  │              │ │              │ │              │             │
│  │ execute()    │ │ execute()    │ │ execute()    │             │
│  │ → addTrans() │ │ → delete()   │ │ → getTrans() │             │
│  │              │ │ → find()     │ │ + filter     │             │
│  └──────────────┘ └──────────────┘ └──────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              │
              uses TransactionManager methods
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  TRANSACTION MANAGER (TransactionManager.java)                  │
│  • Model layer - handles data storage                           │
│  • CRUD operations: add, find, delete, update, get              │
│                                                                 │
│  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐       │
│  │addTransaction()│ │findTransaction()│ │getTransactions()      │
│  │deleteTransaction()    │updateTransaction()  │                │
│  └────────────────┘ └────────────────┘ └────────────────┘       │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  UI (Ui.java)                                                   │
│  • Displays results to user                                     │
└─────────────────────────────────────────────────────────────────┘
```

## How Commands Use TransactionManager

| Command | TransactionManager Methods Used |
|---------|--------------------------------|
| **AddCommand** | `addTransaction(t)` |
| **DeleteCommand** | `findTransaction(id)`, `deleteTransaction(id)` |
| **ModifyCommand** | `findTransaction(id)`, `updateTransaction(id, t)` |
| **ListCommand** | `getTransactions()` + `FilterCommand.buildPredicate()` |
| **SummarizeCommand** | `getTransactions()` + `FilterCommand.buildPredicate()` |

## Filtering (FilterCommand)

**Important:** `FilterCommand` is NOT a user-facing command. It's a helper class that provides filtering logic.

```java
// ListCommand uses FilterCommand
Predicate<Transaction> filter = FilterCommand.buildPredicate(rawArgs);
List<Transaction> filtered = transactions.getTransactions().stream()
    .filter(filter)
    .collect(Collectors.toList());
```

## Setup

### Prerequisites
- JDK 17

### Build
```bash
./gradlew build
```

### Run
```bash
./gradlew run
```

## Usage

```
add --type <credit/debit> --amount <amount> --date <YYYY-MM-DD>
list
list --type <credit/debit>
list --category <category>
delete <id>
modify <id> --amount <new amount>
summarize
help
```

### Setting Budget Goals

The budget supports the following predefined categories:

[1] Food
[2] Transport
[3] Utilities
[4] Housing
[5] Health & Insurance
[6] Debt & Financial Obligation
[7] Child & Financial Dependent Care
[8] Shopping & Personal Care
[9] Gifts & Donations
[10] Investments
[11] Emergency Fund
[12] Savings
### Example Format
**Setting Budgets**
`budget set --month 2026-03 --category 1 --amount 500`
`budget set --month 2026-03 --category 2 --amount 120`
`budget set --month 2026-03 --category 3 --amount 150`
`budget set --month 2026-03 --category 4 --amount 2000`
`budget set --month 2026-03 --category 12 --amount 300`

**Viewing Budget**
`budget view --month 2026-03`

**Expected Output:**
```
=== BUDGET SUMMARY FOR MARCH 2026 ===
Category Budget Spent Remaining Progress
────────────────────────────────────────────────────────────────
[1] Food $500.00 $327.50 $172.50 ████████░░ 65%
[2] Transport $120.00 $45.00 $75.00 ████░░░░░░ 38%
[3] Utilities $150.00 $0.00 $150.00 ░░░░░░░░░░ 0%
[4] Housing $2000.00 $850.00 $1150.00 ████░░░░░░ 43%
[12] Savings $300.00 $0.00 $300.00 ░░░░░░░░░░ 0%
────────────────────────────────────────────────────────────────
Disposable Income $1230.00 $450.00 $780.00 ███░░░░░░░ 37%
TOTAL $4000.00 $1672.50 $2327.50 ████░░░░░░ 42%
```

**Editing Budget**
`budget edit --month 2026-03 --category 1 --amount 600`

**Deleting Budget Category**
`budget delete --month 2026-03 --category 2`
