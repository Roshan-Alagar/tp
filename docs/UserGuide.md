# RLAD User Guide

> **RLAD** — *Record Losses And Debt* — is a fast, keyboard-driven personal finance manager for the command line.

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Quick Start](#2-quick-start)
3. [Features Overview](#3-features-overview)
4. [Command Reference](#4-command-reference)
   - [4.1 `add` — Record a Transaction](#41-add--record-a-transaction)
   - [4.2 `delete` — Remove a Transaction](#42-delete--remove-a-transaction)
   - [4.3 `modify` — Edit a Transaction](#43-modify--edit-a-transaction)
   - [4.4 `list` — View Transactions](#44-list--view-transactions)
   - [4.5 `sort` — Set Global Sort Order](#45-sort--set-global-sort-order)
   - [4.6 `summarize` — Financial Summary](#46-summarize--financial-summary)
   - [4.7 `budget` — Budget Management](#47-budget--budget-management)
   - [4.8 `export` — Export to CSV](#48-export--export-to-csv)
   - [4.9 `import` — Import from CSV](#49-import--import-from-csv)
   - [4.10 `clear` — Clear All Data](#410-clear--clear-all-data)
   - [4.11 `help` — Get Help](#411-help--get-help)
   - [4.12 `exit` — Exit RLAD](#412-exit--exit-rlad)
5. [Advanced Usage](#5-advanced-usage)
   - [5.1 Combined Filters](#51-combined-filters)
   - [5.2 Relative Date Keywords](#52-relative-date-keywords)
   - [5.3 Multi-Category Filtering](#53-multi-category-filtering)
6. [Budget Categories](#6-budget-categories)
7. [Data Formats](#7-data-formats)
   - [7.1 Date Format](#71-date-format)
   - [7.2 Amount Format](#72-amount-format)
8. [Data Persistence](#8-data-persistence)
9. [Troubleshooting](#9-troubleshooting)
10. [FAQ](#10-faq)
11. [Command Summary](#11-command-summary)

---

## 1. Introduction

RLAD is a lightweight, terminal-native personal finance tracker built for users who prefer typed commands over spreadsheets and GUIs. It is designed for speed, precision, and portability — if you can open a terminal, you can use RLAD.

**Core capabilities:**

| Capability | Description |
|---|---|
| Transaction logging | Record credits (income) and debits (expenses) with full metadata |
| Filtering & sorting | Query your history by type, category, date range, and amount range |
| Budget tracking | Set monthly spending budgets per category and monitor usage |
| CSV I/O | Export data for Excel/Sheets; import from CSV backups |
| Persistent storage | Data is saved automatically after every change |

RLAD is designed for NUS students and young professionals who are comfortable in the terminal and want a no-overhead tool to stay on top of their personal finances.

> **Prerequisite:** Java 17 or above must be installed and accessible from your terminal (`java --version` to verify).

---

## 2. Quick Start

```bash
# 1. Navigate to the directory containing RLAD.jar
cd /path/to/rlad

# 2. Launch the application
java -jar RLAD.jar
```

Once the welcome banner appears, you are ready to enter commands. Try these to get started:

```
# Record your monthly salary
add credit 3000.00 2026-03-01 salary "March salary"

# Record a meal expense
add debit 15.50 2026-03-05 food "Chicken rice"

# View all transactions
list

# View a financial summary
summarize

# Exit
exit
```

---

## 3. Features Overview

| Command | Description |
|---|---|
| `add` | Record a credit (income) or debit (expense) transaction |
| `delete` | Permanently remove a transaction by its HashID |
| `modify` | Update one or more fields of an existing transaction |
| `list` | View all transactions with optional filters and sorting |
| `sort` | Set a persistent global sort order for all `list` output |
| `summarize` | View income/expense totals and a per-category breakdown |
| `budget` | Set, view, edit, or delete monthly category budgets |
| `export` | Export all transactions to a CSV file |
| `import` | Import transactions from a CSV file (replace or merge) |
| `clear` | Permanently delete all transaction data |
| `help` | Display usage instructions for any command |
| `exit` | Quit the application |

---

## 4. Command Reference

> **Notation:** `<param>` = required. `[param]` = optional.

---

### 4.1 `add` — Record a Transaction

Records a new credit or debit transaction. The first three parameters are mandatory; category and description are optional.

**Syntax:**
```
add <type> <amount> <date> [category] [description]
```

**Parameters:**

| Position | Parameter | Required | Accepted Values |
|---|---|---|---|
| 1 | `type` | ✅ Yes | `credit` or `debit` |
| 2 | `amount` | ✅ Yes | Positive number, up to 2 decimal places (e.g. `15.50`, `3000`) |
| 3 | `date` | ✅ Yes | `YYYY-MM-DD` format |
| 4 | `category` | ❌ No | Single word (e.g. `food`, `salary`, `transport`) |
| 5 | `description` | ❌ No | Free text; use quotes for multiple words (e.g. `"Chicken rice"`) |

**Examples:**
```
add credit 3000 2026-03-01 salary "March salary"
add debit 15.50 2026-03-05 food "Chicken rice"
add debit 5.00 2026-03-06
add credit 500 2026-03-10 freelance
```

**Expected output:**
```
✅ Transaction added successfully!
   ID: a7b2c3
   CREDIT: $3000.00 on 2026-03-01
   Category: salary
   Description: "March salary"
```

> **Note:** Each transaction is assigned a unique 6-character **HashID** upon creation.
> This ID is required for `delete` and `modify` — note it down, or retrieve it later with `list`.

---

### 4.2 `delete` — Remove a Transaction

Permanently removes a single transaction identified by its HashID.

**Syntax:**
```
delete <hashID>
```

**Parameters:**

| Parameter | Required | Description |
|---|---|---|
| `hashID` | ✅ Yes | The 6-character ID assigned when the transaction was created |

**Example:**
```
delete a7b2c3
```

**Expected output:**
```
✅ Transaction deleted successfully!
   ID: a7b2c3
   Deleted: [a7b2c3] DEBIT | 2026-03-05 | $15.50 | food | Chicken rice at Clementi
```

> ⚠️ **Warning:** Deletion is **permanent** and cannot be undone. Use `list` to verify the HashID before deleting.

---

### 4.3 `modify` — Edit a Transaction

Updates one or more fields of an existing transaction. The HashID is required; at least one field to update must be specified.

**Syntax:**
```
modify <hashID> [field=value ...]
```

**Modifiable fields:**

| Field | Example value |
|---|---|
| `type` | `credit` or `debit` |
| `amount` | `25.00` |
| `date` | `2026-03-10` |
| `category` | `transport` |
| `description` | `"Updated description"` |

**Examples:**
```
# Update only the amount
modify a7b2c3 amount=25.00

# Update amount and description together
modify a7b2c3 amount=20.00 description="Fancy chicken rice"

# Update category and date together
modify a7b2c3 category=transport date=2026-03-06
```

> **Note:** Fields not specified in the command remain unchanged.

---

### 4.4 `list` — View Transactions

Displays your transaction history. Without any filters, all transactions are shown in the current sort order.

**Syntax:**
```
list [filter:value ...]
```

**Available filters:**

| Filter | Example | Description |
|---|---|---|
| `type:` | `list type:debit` | Show only debits or only credits |
| `cat:` | `list cat:food` | Filter by category (use commas for multiple: `cat:food,transport`) |
| `from:` | `list from:2026-03-01` | Show transactions on or after this date |
| `to:` | `list to:2026-03-31` | Show transactions on or before this date |
| `min:` | `list min:50` | Show transactions with amount ≥ this value |
| `max:` | `list max:100` | Show transactions with amount ≤ this value |

**Examples:**
```
list                                        # Show all transactions
list type: debit                             # Show only expenses
list cat: food                               # Show food-related transactions
list from: 2026-03-01 to: 2026-03-31          # Show transactions in March 2026
list min: 50 max: 200                         # Show transactions between $50 and $200
list type: debit cat: food                    # Show food expenses only
list type: debit from: 2026-03-01 min: 10      # Expenses from March, $10 and above
```

**Expected output:**
```
---------------------------------------------------------------------------
  ID     TYPE     DATE           AMOUNT  CATEGORY      DESCRIPTION
---------------------------------------------------------------------------
  a7b2c3 DEBIT    2026-03-05     $15.50  food          Chicken rice at Clementi
  d4e5f6 CREDIT   2026-03-01   $3000.00  salary        March salary
---------------------------------------------------------------------------
  📊 Total: 2 transaction(s) shown.
```

---

### 4.5 `sort` — Set Global Sort Order

Sets a **persistent** default sort order that is applied to all subsequent `list` and `summarize` output, until changed or reset.

**Syntax:**
```
sort [field] [direction]
sort reset
sort
```

**Options:**

| Usage | Effect |
|---|---|
| `sort` | Displays the current global sort setting |
| `sort amount` | Sort by amount, ascending (default direction) |
| `sort amount desc` | Sort by amount, descending |
| `sort date` | Sort by date, ascending |
| `sort date desc` | Sort by date, newest first |
| `sort reset` | Clear the global sort; revert to insertion order |

**Examples:**
```
sort amount          # Cheapest transactions first
sort date desc       # Most recent transactions first
sort reset           # Back to insertion order
sort                 # Check what the current sort is
```

> **Note:** The sort setting persists across the session but does not persist after `exit`. Reapply it on next launch if needed.

---

### 4.6 `summarize` — Financial Summary

Displays total credits, total debits, net balance, and a breakdown of spending by category. Accepts the same filter flags as `list`.

**Syntax:**
```
summarize [filter:value ...]
```

**Examples:**
```
summarize                                   # Summary of all transactions
summarize type:debit                        # Summary of expenses only
summarize from:2026-01-01 to:2026-03-31     # Q1 2026 summary
summarize cat:food                          # Food category summary
```

**Expected output:**
```
===== Financial Summary =====
  Total Credit : $3000.00
  Total Debit  :   $15.50
  Net Balance  : $2984.50

--- Category Breakdown ---
  salary:            $3000.00
  food:              $15.50
=============================
```

---

### 4.7 `budget` — Budget Management

Manage monthly spending targets per category. Budget categories use fixed numeric codes (see [Section 6](#6-budget-categories)).

**Syntax:**
```
budget set    <YYYY-MM> <category_code> <amount>
budget view   [YYYY-MM]
budget edit   <YYYY-MM> <category_code> <amount>
budget delete <YYYY-MM> <category_code>
budget yearly [YYYY]
```

**Subcommands:**

| Subcommand | Description |
|---|---|
| `set` | Create a new monthly budget for a category |
| `view` | Display all budgets for a given month (defaults to current month) |
| `edit` | Update the amount for an existing budget entry |
| `delete` | Remove a budget entry for a specific category and month |
| `yearly` | Display a full-year budget summary (defaults to current year) |

**Examples:**
```
budget set 2026-03 1 500        # Set a $500 food budget for March 2026
budget view 2026-03             # View all March 2026 budgets
budget edit 2026-03 1 600       # Increase the March food budget to $600
budget delete 2026-03 1         # Remove the March food budget
budget yearly 2026              # View all budgets across 2026
```

**Expected output for `budget view`:**
```
=== BUDGET SUMMARY FOR 2026-03 ===
Category                  |     Budget |      Spent |  Remaining | Progress
---------------------------+------------+------------+------------+----------------------
[1] Food                  | $  500.00 | $    0.00 | $  500.00 | ░░░░░░░░░░░░░░░░░░░░   0%
---------------------------+------------+------------+------------+----------------------
Disposable Income         | $ 3500.00 | $    0.00 | $ 3500.00 | ░░░░░░░░░░░░░░░░░░░░   0%
TOTAL                     | $ 4000.00 | $    0.00 | $ 4000.00 | ░░░░░░░░░░░░░░░░░░░░   0%
```

> **Note:** *Disposable Income* is your total recorded credits minus all budget allocations for that month. A negative value means your allocated budgets exceed your recorded income.

---

### 4.8 `export` — Export to CSV

Exports all current transactions to a CSV file compatible with Excel and Google Sheets.

**Syntax:**
```
export [filename]
```

**Examples:**
```
export                  # Creates transactions_YYYY-MM-DD.csv in the current directory
export backup.csv       # Creates backup.csv in the current directory
```

**Expected output:**
```
Exported 15 transactions to: ./transactions_2026-04-02.csv
```

**CSV structure:**
```
HashID,Type,Category,Amount,Date,Description
a7b2c3,credit,salary,3000.00,2026-03-01,March salary
d4e5f6,debit,food,15.50,2026-03-05,"Chicken rice at Clementi"
```

> **Tip:** Use `export` before any destructive operation (`clear`, `import` without `merge`) to preserve a backup of your data.

---

### 4.9 `import` — Import from CSV

Imports transactions from a CSV file. By default, all existing data is **replaced** by the imported data. Use the `merge` flag to add imported records to your existing transactions instead.

**Syntax:**
```
import <filename> [merge]
```

**Parameters:**

| Parameter | Required | Description |
|---|---|---|
| `filename` | ✅ Yes | Path to the CSV file (relative or absolute) |
| `merge` | ❌ No | If specified, imported records are appended rather than replacing existing data |

**Examples:**
```
import backup.csv           # Replace all existing data with backup.csv
import backup.csv merge     # Add records from backup.csv to existing data
```

**Expected output:**
```
Import complete: 23 succeeded, 2 failed.
```

> ⚠️ **Warning:** Without `merge`, all existing transactions are **permanently deleted** before the import begins.

> **Note:** HashIDs are regenerated for all imported transactions to avoid ID collisions.

> **CSV format requirements:** Column headers must match exactly — `HashID,Type,Category,Amount,Date,Description`. Rows with invalid or missing fields are skipped with a warning.

---

### 4.10 `clear` — Clear All Data

Permanently deletes **all** transaction records. Budget definitions are **not** affected.

**Syntax:**
```
clear [--force]
```

**Behaviour:**

Without `--force`, RLAD prompts for confirmation:
```
WARNING: This will permanently delete all 47 transactions.
This action cannot be undone.
Type CONFIRM to proceed: CONFIRM
Cleared 47 transactions.
```

With `--force`, the confirmation step is skipped:
```
clear --force
Cleared 47 transactions.
```

> ⚠️ **Warning:** This action cannot be undone. Run `export` first to back up your data.

> **Note:** Budget entries and their category definitions are preserved after `clear`. Spending totals shown in `budget view` will reset to zero as there are no transactions to aggregate.

---

### 4.11 `help` — Get Help

Displays usage instructions. With no argument, lists all available commands. With a command name, shows detailed usage for that specific command.

**Syntax:**
```
help [command]
```

**Examples:**
```
help            # List all commands
help add        # Usage for the add command
help list       # Usage for the list command
help budget     # Usage for the budget command
```

---

### 4.12 `exit` — Exit RLAD

Saves all data and exits the application.

**Syntax:**
```
exit
```

---

## 5. Advanced Usage

### 5.1 Combined Filters

Multiple filters can be chained in a single `list` or `summarize` command. All conditions must be satisfied (logical AND).

```
list type:debit cat:food from:2026-03-01 to:2026-03-31 min:10
```

This returns all food expenses in March 2026 that are at least $10.

---

### 5.2 Relative Date Keywords

For `from:` and `to:` filters, you may use the following relative keywords instead of a full `YYYY-MM-DD` date:

| Keyword | Resolves to |
|---|---|
| `today` | Today's date |
| `yesterday` | Yesterday's date |
| `tomorrow` | Tomorrow's date |
| `this-week` | Start of the current week |
| `this-month` | Start of the current month |
| `last-month` | Start of the previous month |
| `last-year` | Start of the previous year |

**Example:**
```
list from:this-month        # All transactions since the start of this month
list from:last-month to:this-month type:debit    # Last month's expenses
```

> **Note:** Relative date keywords are only valid for `from:` and `to:`. The `date` parameter in `add` and `modify` requires an exact `YYYY-MM-DD` value.

---

### 5.3 Multi-Category Filtering

To filter by more than one category simultaneously, provide a comma-separated list with no spaces:

```
list cat:food,transport             # Show food AND transport transactions
summarize cat:food,transport,health # Summary across three categories
```

> **Note:** `none` and `(none)` are reserved keywords. They cannot be used as category names when adding transactions, and using `cat:none` or `cat:(none)` in filters will show only transactions with no category assigned.

---

## 6. Budget Categories

The `budget` command uses fixed numeric category codes. Free-text category names (used in `add` and `list`) are a separate, independent system.

| Code | Category |
|---|---|
| 1 | Food |
| 2 | Transport |
| 3 | Utilities |
| 4 | Housing |
| 5 | Health & Insurance |
| 6 | Debt & Financial Obligations |
| 7 | Child & Dependent Care |
| 8 | Shopping & Personal Care |
| 9 | Gifts & Donations |
| 10 | Investments |
| 11 | Emergency Fund |
| 12 | Savings |

**Example:**
```
budget set 2026-03 5 200    # Set $200 Health & Insurance budget for March
budget set 2026-03 10 500   # Set $500 Investments budget for March
```

---

## 7. Data Formats

### 7.1 Date Format

All date inputs must use `YYYY-MM-DD` format.

| Input | Valid? |
|---|---|
| `2026-03-15` | ✅ |
| `15-03-2026` | ❌ |
| `2026/03/15` | ❌ |
| `03-15-2026` | ❌ |

### 7.2 Amount Format

| Rule | Detail |
|---|---|
| Must be positive | Values ≤ 0 are rejected |
| Maximum value | 10,000,000 |
| Decimal places | Up to 2 (e.g. `15.50`, `3000`, `0.99`) |
| No currency symbol | Do not include `$` or any currency prefix |

| Input | Valid? |
|---|---|
| `15.50` | ✅ |
| `3000` | ✅ |
| `0.99` | ✅ |
| `$15.50` | ❌ (no currency symbol) |
| `-10` | ❌ (must be positive) |
| `15.999` | ❌ (max 2 decimal places) |

---

## 8. Data Persistence

RLAD automatically saves all transactions to a local file after every change. No manual save step is required.

| File | Purpose |
|---|---|
| `data/rlad.txt` | Primary data store — transactions and budgets |

Your data is restored automatically the next time you launch RLAD from the same directory. Use `export` to create a portable CSV backup that can be opened in Excel or Google Sheets.

> **Note:** If `data/rlad.txt` is deleted or corrupted, RLAD will start with an empty state. Keep regular CSV exports as offsite backups.

---

## 9. Troubleshooting

### Common Mistakes

| Incorrect Command | Problem | Correct Usage |
|---|---|---|
| `add --amount 50` | Missing required parameters | `add debit 50 2026-03-05` |
| `add pizza 10 2026-03-05` | Invalid type value | `add debit 10 2026-03-05 food pizza` |
| `list cat:salary food` | Space instead of comma for multiple categories | `list cat:salary,food` |
| `delete a7b2c3 b8c9d0` | Only one HashID accepted per command | Run `delete` twice |
| `modify a7b2c3` | No fields to update specified | `modify a7b2c3 amount=25.00` |
| `add debit 15.50 2026-03-05 "Chicken rice"` | Description in position 4 (category position) | `add debit 15.50 2026-03-05 food "Chicken rice"` |

### Known Limitations

- **Double dash in descriptions:** The string `--` inside a description confuses the parser. Use a single dash `-` instead.
- **HashID lookup:** If you lose a HashID, use `list` to find it in the leftmost column.
- **Duplicate monthly budgets:** Each category supports at most one budget per month. Use `budget edit` to update an existing entry.

---

## 10. FAQ

**Q: What happens if I forget a transaction's HashID?**

Use `list` to retrieve it. The HashID is shown in the leftmost column of every transaction row.

---

**Q: Can I have multiple budgets for the same category in one month?**

No. Each category supports at most one budget per month. Use `budget edit` to update an existing entry rather than creating a new one.

---

**Q: What is "Disposable Income" in the budget view?**

It is the portion of your recorded credits for that month that has not been allocated to any budget category. A negative value means your total budget allocations exceed your recorded income for that month.

---

**Q: Can I import a CSV I edited in Excel?**

Yes. Ensure the first row contains the exact headers: `HashID,Type,Category,Amount,Date,Description`. Rows with missing or invalid fields are skipped with a warning.

---

**Q: Does RLAD save my data automatically?**

Yes. RLAD writes to `data/rlad.txt` after every modifying command. Your data is fully restored the next time you launch from the same directory.

---

**Q: What happens to my budgets when I use `clear`?**

Budget definitions and amounts are preserved. Only transaction data is deleted. Spending totals in `budget view` will show zero since there are no transactions to count against the budgets.

---

**Q: Can I name a category "none" or "(none)"?**

No. These are reserved keywords that represent transactions with no category. The application prevents you from creating a category with these names, and using `list cat:none` or `list cat:(none)` will show only transactions that have no category assigned.

---

**Q: I launched RLAD from a different directory and my data is gone.**

RLAD loads `data/rlad.txt` relative to the directory you launch it from. Always run `java -jar RLAD.jar` from the same working directory, or use `export` to maintain portable CSV backups.

---

## 11. Command Summary

| Command | Syntax |
|---|---|
| `add` | `add <type> <amount> <date> [category] [description]` |
| `delete` | `delete <hashID>` |
| `modify` | `modify <hashID> [field=value ...]` |
| `list` | `list [type:T] [cat:C] [from:D] [to:D] [min:A] [max:A]` |
| `sort` | `sort [field [direction]]` \| `sort reset` |
| `summarize` | `summarize [type:T] [cat:C] [from:D] [to:D]` |
| `budget` | `budget <set\|view\|edit\|delete\|yearly> [args...]` |
| `export` | `export [filename]` |
| `import` | `import <filename> [merge]` |
| `clear` | `clear [--force]` |
| `help` | `help [command]` |
| `exit` | `exit` |