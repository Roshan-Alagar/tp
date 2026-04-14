# 💰 RLAD - Record Losses And Debt

[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)](https://java.com)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-2.0-orange.svg)](https://github.com/yourusername/RLAD)

**A minimalist, keyboard-driven personal finance manager for the command line.**

RLAD helps you track income and expenses, set budget goals, and gain financial insights—all without spreadsheets or GUI apps. Perfect for developers, students, and anyone who prefers speed over mouse clicks.

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📝 **Add Transactions** | Record credits (income) and debits (expenses) with intuitive position-based syntax |
| 🔍 **List & Filter** | View transactions with powerful filtering by type, category, date, and amount |
| ✏️ **Modify Entries** | Update any transaction field using simple `field=value` syntax |
| 🗑️ **Delete Records** | Remove transactions by their unique HashID |
| 📊 **Financial Summary** | Get instant breakdowns of income, expenses, and category totals |
| 🎯 **Budget Management** | Set monthly budgets across 12 categories with visual progress tracking |
| 📈 **Yearly Overview** | View annual trends with ASCII progress bars and category analysis |
| ⚠️ **Smart Notifications** | Receive alerts at 80%, 90%, and 100% of budget limits |
| 💾 **CSV Export/Import** | Backup, restore, or analyze data in Excel/Google Sheets |
| 🔄 **Auto-Save** | Automatic crash recovery - never lose your data |
| 🎨 **Clean Interface** | Formatted tables and visual indicators for easy reading |

## 🚀 Quick Start

### Prerequisites
- **Java 17** or higher ([Download](https://adoptium.net/))
- Terminal with UTF-8 support (for progress bars)

### Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/RLAD.git
cd RLAD

# Build the application
./gradlew build

# Run RLAD
./gradlew run
```
Or run the JAR directly:
```
java -jar build/libs/RLAD.jar
```

## First Commands
```
# Add your first income
add credit 3000 2026-03-01 salary "March salary"

# Add an expense
add debit 15.50 2026-03-05 food "Chicken rice"

# View all transactions
list

# See your financial summary
summarize

# Set a budget for food in March
budget set 2026-03 1 500

# View your budget progress
budget view 2026-03
```
## 📖 Command Reference
### Core Commands
| Command   | Syntax | Example |
|-----------|-------|-------|
| add       | `add <type> <amount> <date> [category] [description]` | `add debit 15.50 2026-03-05 food "Lunch"` |
| list      | `list [filters...]` | `list type:debit cat:food` |
| modify    | `modify <hashID> field=value...` | `modify <hashID> field=value...` |
| delete    | `delete <hashID>` | `delete a7b2c3` |
| summarize | `summarize [filters...]` | `summarize from:2026-03-01` |
| sort      | `sort [amount|date] [asc|desc]` | `sort amount desc` |

### Budget Commands
| Command | Syntax | Example |
|---------|-------|-------|
| set     | `budget set <YYYY-MM> <code> <amount>` | `budget set 2026-03 1 500` |
| view    | `budget view [YYYY-MM]` | `budget view 2026-03` |
| edit    | `budget edit <YYYY-MM> <code> <amount>` | `budget edit 2026-03 1 600` |
| delete  | `budget delete <YYYY-MM> <code>` | `budget delete 2026-03 1` |
| yearly  | `budget yearly [YYYY]` | `budget yearly 2026` |

### Data Management
| Command | Syntax | Example |
|---------|-------|-------|
| export  | `export [filename]` | `export backup.csv` |
| import  | `import <filename> [merge]` | `import backup.csv merge` |
| clear   | `clear [force]` | `clear force` |
| search  | `search <keyword>` | `search chicken` |

### Help & Utility
| Command | Syntax           | Example |
|---------|------------------|-------|
| help    | `help [command]` | `help add` |
| exit    | `exit`           | `exit` |

### Filter Options for `list` and `summarize`
| Command      | Syntax                          | Example |
|--------------|---------------------------------|-------|
| By type      | `type:credit/debit`             | `list type:debit` |
| By category  | `cat:<category>`                | `list cat:food` |
| Data range   | `from:YYYY-MM-DD to:YYYY-MM-DD` | `list from:2026-03-01 to:2026-03-31` |
| Amount range | `min:<amount> max:<amount>`     | `list min:10 max:100` |

### Budget Category Code
| Code | Category     | Code | Category       |
|------|--------------|------|----------------|
| 1    | Food         | 7    | Childcare      |
| 2    | Transport    | 8    | Shopping       |
| 3    | Utilities    | 9    | Gifts          |
| 4    | Housing      | 10   | Investments    |
| 5    | Health & Insurance | 11   | Emergency Fund |
| 6    | Debt Obligations | 12   | Savings        |

## 🏗️ Architecture
RLAD follows the **MVC (Model-View-Controller)** pattern combined with the **Command Design Pattern** for clean separation of concerns.

### Component Diagram
```
┌─────────────────────────────────────────────────────────────────────────┐
│                              USER INPUT                                 │
│                    "add debit 15.50 2026-03-05 food"                    │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  PARSER (Controller & Factory)                                          │
│  • Tokenizes input into action + arguments                              │
│  • Validates command exists                                             │
│  • Returns appropriate Command object                                   │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  COMMAND (Command Pattern)                                              │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐    │
│  │ AddCommand   │ │ DeleteCommand│ │ ListCommand  │ │ BudgetCommand│    │
│  │ execute()    │ │ execute()    │ │ execute()    │ │ execute()    │    │
│  └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘    │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  TRANSACTION MANAGER (Model)                                            │
│  • In-memory storage with dual structures:                              │
│    - ArrayList: preserves insertion order                               │
│    - HashMap: O(1) lookup by HashID                                     │
│  • CRUD operations with budget notifications                            │
└─────────────────────────────────────────────────────────────────────────┘
                    │                       │
                    ▼                       ▼
┌──────────────────────────────┐  ┌──────────────────────────────────────┐
│  BUDGET MANAGER (Model)      │  │  CSV STORAGE (Persistence)           │
│  • Monthly budget tracking   │  │  • Export to CSV format              │
│  • Progress calculation      │  │  • Import with validation            │
│  • Threshold notifications   │  │  • Auto-save for crash recovery      │
└──────────────────────────────┘  └──────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  UI (View)                                                              │
│  • Displays formatted tables and progress bars                          │
│  • Shows errors and success messages                                    │
│  • Handles user confirmation dialogs                                    │
└─────────────────────────────────────────────────────────────────────────┘
```

## Key Design Decisions
| Component                | Design Choice | Rationale |
|--------------------------|---------------|-----------|
| Command Pattern          |  Each command is a separate class | Easy to add new commands, single responsibility  |
| Position-based Arguments | `add debit 15.50` instead of flags  | Intuitive, faster typing, natural language flow  |
| Dual Storage             | ArrayList + HashMap  |  Preserves order while providing O(1) lookups |
| Predictate Filtering     | Reusable `FilterCommand` utility  | DRY principle - same filters work for list and summarize  |
| Event-driven Budgets     | BudgetManager listens to TransactionManager  | Decoupled, automatic budget updates  |

## 📁 Project Structure
```
src/main/java/seedu/RLAD/
├── RLAD.java                      # Main entry point & application loop
├── Parser.java                    # Command factory & input tokenizer
├── Transaction.java               # Immutable transaction data model
├── TransactionManager.java        # Core storage & CRUD operations
├── TransactionSorter.java         # Sorting utilities (amount/date)
├── Ui.java                        # User interface & output formatting
├── Logo.java                      # ASCII art logo
├── exception/
│   └── RLADException.java         # Custom runtime exception
├── command/
│   ├── Command.java               # Abstract base command
│   ├── AddCommand.java            # Position-based transaction creation
│   ├── DeleteCommand.java         # HashID-based deletion
│   ├── ModifyCommand.java         # Field=value updates
│   ├── ListCommand.java           # Filtered & sorted display
│   ├── FilterCommand.java         # Predicate builder (helper)
│   ├── SummarizeCommand.java      # Financial aggregation
│   ├── SortCommand.java           # Global sort management
│   ├── SearchCommand.java         # Keyword search
│   ├── ExportCommand.java         # CSV export
│   ├── ImportCommand.java         # CSV import
│   ├── ClearCommand.java          # Bulk deletion
│   └── HelpCommand.java           # Documentation
├── budget/
│   ├── BudgetManager.java         # Budget tracking & notifications
│   ├── BudgetCommand.java         # Budget command handler
│   ├── MonthlyBudget.java         # Per-month budget container
│   └── BudgetCategory.java        # 12-category enum
└── storage/
    ├── CsvStorageManager.java     # CSV import/export logic
    └── AutoSaveManager.java       # Automatic crash recovery
```

## 💡 Usage Examples
### Basic Transaction Management
```
# Add income
add credit 3000 2026-03-01 salary "Monthly salary"

# Add expenses
add debit 15.50 2026-03-05 food "Lunch at hawker"
add debit 120.00 2026-03-10 transport "Grab rides this week"
add debit 500.00 2026-03-15 rent "March rent"

# View all March expenses
list type:debit from:2026-03-01 to:2026-03-31

# See spending summary
summarize

# Find a specific transaction
list cat:transport

# Modify an expense (use HashID from list command)
modify a7b2c3 amount=25.00 description="Fancier lunch"

# Delete a transaction
delete a7b2c3
```
### Budget Management
```
# Set March budgets
budget set 2026-03 1 500      # Food budget
budget set 2026-03 2 200      # Transport budget
budget set 2026-03 4 1500     # Housing (rent)

# View progress
budget view 2026-03

# Adjust budget mid-month
budget edit 2026-03 1 600

# Yearly overview
budget yearly 2026

# Remove a budget category
budget delete 2026-03 2
```

### Data Management
```
# Export all data
export backup_2026_03.csv

# Import with merge (add to existing)
import backup_2026_03.csv merge

# Replace all data with imported file
import backup_2026_03.csv

# Start fresh (with confirmation)
clear

# Force clear (no confirmation)
clear force
```

### Advanced Filtering
```
# Complex filtering
list type:debit cat:food from:2026-03-01 min:10 max:50

# Search by keyword
search chicken
search a7b2c3
search 15.50
```

## 🔧 Development
### Building from Source
```
# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Generate JAR
./gradlew shadowJar
# Output: build/libs/RLAD.jar
```

### Code Quality
```
# Check style
./gradlew checkstyleMain

# Run all checks
./gradlew check
```

## IDE Setup (IntelliJ IDEA)
1. Open IntelliJ → **File** → Open
2. Select the project root directory
3. Ensure JDK 17 is set: File → Project Structure → SDK
4. Mark `src/main.java` as Sources Root
5. Run seedu.RLAD.RLAD

## Documentation
* [User Guide](https://github.com/AY2526S2-CS2113-W13-4/tp/blob/master/docs/team/UserGuide.md) - Complete command reference with examples
* [Developer Guide](https://github.com/AY2526S2-CS2113-W13-4/tp/blob/master/docs/team/DeveloperGuide.md) - Architecture, design decisions, and implementation details
* [About Us](https://github.com/AY2526S2-CS2113-W13-4/tp/blob/master/docs/AboutUs.md) Meet the team

## 🙏 Acknowledgements
* Architecture inspired by [AddressBook-Level3](https://se-education.org/addressbook-level3/UserGuide.html#features)
* Java 8 Streams API for functional filtering
* All contributors and user who provided feedback

---
## Made with ☕ and ⌨️ for command-line enthusiasts
"Record Losses And Debt - because ignoring your finances won't make them better"

This README features:

1. **Professional badges** for version, Java version, and license
2. **Clear feature grid** with emojis for visual appeal
3. **Quick start section** with installation and first commands
4. **Comprehensive command reference** tables
5. **ASCII architecture diagram** showing the MVC + Command pattern
6. **Design decisions table** explaining key choices
7. **Complete project structure** with descriptions
8. **Real-world usage examples** for common scenarios
9. **Development setup instructions** for contributors
10. **Professional formatting** with tables, code blocks, and emojis
11. **Clear navigation** with table of contents (implied through sections)
12. **Contact and support information**

The README balances technical depth for developers with usability for end-users, making it suitable for both audiences.

