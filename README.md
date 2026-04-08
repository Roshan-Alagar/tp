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
│               (e.g., "add --type credit --amount 50")          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  PARSER (Parser.java)                                           │
│  • Validates command format                                     │
│  • Acts as Factory - creates appropriate Command object        │
│  • Does NOT interact with TransactionManager                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                    returns Command object
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  COMMAND (Command.java - base class)                            │
│                                                                  │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐           │
│  │ AddCommand   │ │DeleteCommand │ │ ListCommand  │  ...       │
│  │              │ │              │ │              │           │
│  │ execute()    │ │ execute()    │ │ execute()    │           │
│  │ → addTrans() │ │ → delete()   │ │ → getTrans() │           │
│  │              │ │ → find()     │ │ + filter     │           │
│  └──────────────┘ └──────────────┘ └──────────────┘           │
└─────────────────────────────────────────────────────────────────┘
                              │
              uses TransactionManager methods
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  TRANSACTION MANAGER (TransactionManager.java)                  │
│  • Model layer - handles data storage                          │
│  • CRUD operations: add, find, delete, update, get              │
│                                                                  │
│  ┌────────────────┐ ┌────────────────┐ ┌────────────────┐      │
│  │addTransaction()│ │findTransaction()│ │getTransactions()    │
│  │deleteTransaction()    │updateTransaction()  │                    │
│  └────────────────┘ └────────────────┘ └────────────────┘      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  UI (Ui.java)                                                   │
│  • Displays results to user                                    │
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
| SearchCommand  | getTransactions() + keyword matching |
| SortCommand    | setGlobalSort() |
| BudgetCommand  | BudgetManager methods |
| ExportCommand  | CsvStorageManager.exportToCsv() |
| ImportCommand  | CsvStorageManager.importFromCsv(), addTransaction() |
| ClearCommand   | clearAllTransactions() |

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
add --type <credit/debit> --amount <amount> --date <yyyy-MM-dd> --category <category> --description "<text>"
delete --hashID <id>
modify <id> --amount <amount> --category <category> --date <date> --type <type> --description "<text>"
list
list --type <credit/debit> --category <category> --sort <date/amount>
search --keyword <term>
summarize
summarize --type <debit> --date-from <date> --date-to <date>
budget set --month <yyyy-MM> --category <code> --amount <amount>
export --file <filename> --path <directory>
import --file <filename> --merge
clear
help
exit
```
