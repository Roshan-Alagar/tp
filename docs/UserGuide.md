# RLAD User Guide

## Table of Contents

1. [Introduction](#1-introduction)
2. [Quick Start](#2-quick-start)
3. [Features Overview](#3-features-overview)
4. [Command Reference](#4-command-reference)
    - 4.1 [add — Record a Transaction](#41-add--record-a-transaction)
    - 4.2 [delete — Remove a Transaction](#42-delete--remove-a-transaction)
    - 4.3 [modify — Edit a Transaction](#43-modify--edit-a-transaction)
    - 4.4 [list — View Transactions](#44-list--view-transactions)
    - 4.5 [sort — Set Global Sort Order](#45-sort--set-global-sort-order)
    - 4.6 [summarize — Financial Summary](#46-summarize--financial-summary)
    - 4.7 [budget — Budget Management](#47-budget--budget-management)
    - 4.8 [export — Export to CSV](#48-export--export-to-csv)
    - 4.9 [import — Import from CSV](#49-import--import-from-csv)
    - 4.10 [clear — Clear All Data](#410-clear--clear-all-data)
    - 4.11 [help — Get Help](#411-help--get-help)
    - 4.12 [exit — Exit RLAD](#412-exit--exit-rlad)
    - 4.13 [Advanced Filtering — Tips & Tricks](#413-advanced-filtering--tips--tricks)
    - 4.14 [Troubleshooting — Common Mistakes](#414-troubleshooting--common-mistakes)
5. [Budget Categories](#5-budget-categories)
6. [Date & Amount Formats](#6-date--amount-formats)
7. [FAQ](#7-faq)
8. [Command Summary](#8-command-summary)

---

## 1. Introduction

**RLAD** (Record Losses And Debt) is a fast, keyboard-driven personal finance manager built for the command line. No spreadsheets, no GUI — just clean, structured commands for people who prefer efficiency over aesthetics.

RLAD lets you:
- Record income (credits) and expenses (debits)
- Filter and sort your transaction history
- Set monthly budgets per category and track spending against them
- Export your data to CSV for backup or analysis in Excel/Sheets
- Import transaction data from CSV files

RLAD is best suited for NUS students or young professionals who are comfortable with the terminal and want a lightweight tool to stay on top of their finances.

> **Note:** RLAD is a CLI application. All interaction happens through typed commands.

---

## 2. Quick Start

**Prerequisites:** Java 17 or above must be installed.

1. Open a terminal and navigate to the directory containing your `RLAD.jar`.
2. Run the application: `java -jar RLAD.jar`
3. The welcome banner will appear. Try your first command:
   `add --type credit --amount 3000.00 --date 2026-03-01 --category salary --description "March salary"`
4. Type `exit` to quit.

---

## 3. Features Overview

| Feature    | Description                                              |
|------------|----------------------------------------------------------|
| add        | Record a credit (income) or debit (expense) transaction  |
| delete     | Remove a transaction by its HashID                       |
| modify     | Update one or more fields of an existing transaction     |
| list       | View all transactions with optional filters and sorting  |
| sort       | Set a persistent global sort order for list              |
| summarize  | View totals and category breakdowns                      |
| budget     | Set, view, edit, or delete monthly category budgets      |
| export     | Export all transactions to a CSV file                    |
| import     | Import transactions from a CSV file (replace or merge)   |
| clear      | Permanently delete all transactions                      |
| help       | Print usage instructions                                 |
|------------|----------------------------------------------------------|

---

## 4. Command Reference

### 4.1 `add` — Record a Transaction

Records a new credit or debit transaction.

**Format:**
```
add --type TYPE --amount AMOUNT --date DATE [--category CATEGORY] [--description DESCRIPTION]
```

**Parameters:**

| Parameter       | Required | Description                                  |
|-----------------|----------|----------------------------------------------|
| `--type`        | Yes      | `credit` (income) or `debit` (expense)       |
| `--amount`      | Yes      | Positive number, up to 2 decimal places      |
| `--date`        | Yes      | Date in `yyyy-MM-dd` format                  |
| `--category`    | No       | Free-text label (e.g., `food`, `transport`)  |
| `--description` | No       | Short description; use quotes for spaces     |
|-----------------|----------| -------------------------------------------- |

**Examples:**
```
add --type credit --amount 3000.00 --date 2026-03-01 --category salary
add --type debit --amount 15.50 --date 2026-03-05 --category food --description "Chicken rice at Clementi"
add --type debit --amount 1.50 --date 2026-03-06
```

**Expected Output:**
```
✅ Transaction added successfully!
   HashID: a7b2c3
   DEBIT: $15.50 on 2026-03-05
   Category: food
   Description: "Chicken rice at Clementi"
   ....
```

> **Note:** Each transaction is assigned a unique 6-character **HashID** upon creation. Keep a note of it if you plan to delete or modify the transaction later.

---

### 4.2 `delete` — Remove a Transaction

Permanently removes a transaction by its HashID.

**Format:**
```
delete --hashID HASHID
```

**Parameters:**

| Parameter  | Required | Description                    |
|------------|----------|--------------------------------|
| `--hashID` | Yes      | The 6-character transaction ID |
|------------|----------|--------------------------------|

**Example:**
```
delete --hashID a7b2c3
```

**Expected Output:**
```
Transaction deleted successfully!
   HashID: a7b2c3
   [a7b2c3] DEBIT | 2026-03-05 | $15.50 | food | Chicken rice at Clementi
```

> **Warning:** Deletion is permanent and cannot be undone.

---

### 4.3 `modify` — Edit a Transaction

Updates one or more fields of an existing transaction by its HashID. At least one optional field must be provided.

**Format:**
```
modify --hashID HASHID [--type TYPE] [--amount AMOUNT] [--date DATE] [--category CATEGORY] [--description DESCRIPTION]
```

**Parameters:**

| Parameter       | Required | Description                              |
|-----------------|----------|------------------------------------------|
| `--hashID`      | Yes      | The HashID of the transaction to update  |
| `--type`        | No       | New type: `credit` or `debit`            |
| `--amount`      | No       | New amount                               |
| `--date`        | No       | New date in `yyyy-MM-dd`                 |
| `--category`    | No       | New category label                       |
| `--description` | No       | New description                          |
|-----------------|----------|------------------------------------------|

**Example:**
```
modify --hashID a7b2c3 --amount 20.00 --description "Fancy chicken rice"
```

---

### 4.4 `list` — View Transactions

Displays your transaction history. All filter and sort options are optional.

**Format:**
```
list [--type TYPE] [--category CATEGORY] [--amount [OPERATOR] VALUE]
     [--date DATE] [--date-from DATE] [--date-to DATE] [--sort FIELD [DIRECTION]]
```

**Filter Parameters:**

| Parameter     | Description                                                           |
|---------------|-----------------------------------------------------------------------|
| `--type`      | `credit` or `debit`                                                   |
| `--category`  | Partial match (case-insensitive), comma-separated for multiple        |
| `--amount`    | Exact match, or with operator: `-gt`, `-gte`, `-eq`, `-lt`, `-leq`   |
| `--date`      | Exact date match (`yyyy-MM-dd`)                                       |
| `--date-from` | Show transactions on or after this date                               |
| `--date-to`   | Show transactions on or before this date                              |
| `--sort`      | `date` or `amount`, optionally followed by `asc` or `desc`           |
|---------------|-----------------------------------------------------------------------|

**Examples:**
```
list
list --type debit
list --category food
list --amount -gt 50
list --date-from 2026-01-01 --date-to 2026-03-31
list --type debit --sort amount desc
```

**Expected Output:**
```
---------------------------------------------------------------------------
  ID     TYPE     DATE           AMOUNT  CATEGORY      DESCRIPTION
---------------------------------------------------------------------------
  a7b2c3 DEBIT    2026-03-05     $15.50  food          Chicken rice at Clementi
  d4e5f6 CREDIT   2026-03-01   $3000.00  salary        March salary
---------------------------------------------------------------------------
  Total: 2 transaction(s) shown.
```

**Amount operators:**

| Operator | Meaning               |
|----------|-----------------------|
| `-gt`    | Greater than          |
| `-gte`   | Greater than or equal |
| `-eq`    | Equal to              |
| `-lt`    | Less than             |
| `-leq`   | Less than or equal    |
|----------|-----------------------|

---

### 4.5 `sort` — Set Global Sort Order

Sets a persistent default sort order applied to all future `list` commands.

**Format:**
```
sort [FIELD [DIRECTION]]
sort reset
sort
```

| Usage              | Effect                                         |
|--------------------|------------------------------------------------|
| `sort`             | Shows the current global sort setting          |
| `sort amount`      | Sort by amount ascending (default direction)   |
| `sort date desc`   | Sort by date descending                        |
| `sort reset`       | Clears global sort; reverts to insertion order |
|--------------------|------------------------------------------------|

**Examples:**
```
sort date asc
sort amount desc
sort reset
```

> **Note:** A `--sort` flag in a `list` command overrides the global sort for that command only.

---

### 4.6 `summarize` — Financial Summary

Displays totals and a breakdown by category. Accepts the same filter flags as `list`.

**Format:**
```
summarize [--type TYPE] [--category CATEGORY] [--date-from DATE] [--date-to DATE]
```

**Examples:**
```
summarize
summarize --date-from 2026-01-01 --date-to 2026-03-31
summarize --type debit
```

**Expected Output:**
```
===== Financial Summary =====
  Total Credit : $3000.00
  Total Debit  : $15.50
  Net Balance  : $2984.50

--- Category Breakdown ---
  salary:              $3000.00
  food:                $15.50
=============================
```

---

### 4.7 `budget` — Budget Management

Manage monthly spending budgets per category. Uses numeric category codes (see Section 5).

#### 4.7.1 Set a Budget

```
budget set --month YYYY-MM --category CODE --amount AMOUNT
```

**Example:**
```
budget set --month 2026-03 --category 1 --amount 500.00
```

Sets a $500.00 budget for Food in March 2026.

**Expected Output:**
```
✅ Budget set successfully for 2026-03
   Category [1]: $500.00
```

#### 4.7.2 View Budgets

```
budget view [--month YYYY-MM]
```

Without `--month`, all months with budgets are listed. With `--month`, a detailed table is shown for that month including a progress bar, amount spent, and remaining balance.

**Example:**
```
budget view --month 2026-03
```

**Expected Output:**
```
=== BUDGET SUMMARY FOR 2026-03 ===
Category                  |     Budget |      Spent |  Remaining | Progress
---------------------------+------------+------------+------------+----------------------
[1] Food                   |   $500.00  |   $120.00  |   $380.00  | ████░░░░░░░░░░░░░░░░  24%
---------------------------+------------+------------+------------+----------------------
Disposable Income          |  $1500.00  |  $2880.00  | -$1380.00  | ████████████████████ 192%
TOTAL                      |  $2000.00  |  $3000.00  | -$1000.00  | ████████████████████ 150%
---------------------------+------------+------------+------------+----------------------

```

#### 4.7.3 Edit a Budget

```
budget edit --month YYYY-MM --category CODE --amount AMOUNT
```

The budget for the specified month and category must already exist. Use `budget set` to create a new one.

**Example:**
```
budget edit --month 2026-03 --category 1 --amount 600.00
```

#### 4.7.4 Delete a Budget

```
budget delete --month YYYY-MM --category CODE
```

**Example:**
```
budget delete --month 2026-03 --category 1
```

**Expected Output:**
```
✅ Budget deleted for 2026-03
   Category [1]
```

---

### 4.8 `export` — Export to CSV

Exports all transactions to a CSV file compatible with Excel and Google Sheets.

**Format:**
```
export [--file FILENAME] [--path DIRECTORY]
```

| Parameter | Description                                    | Default                        |
|-----------|------------------------------------------------|--------------------------------|
| `--file`  | Output filename                                | `transactions_YYYY-MM-DD.csv`  |
| `--path`  | Directory to save the file                     | Current working directory      |
|-----------|------------------------------------------------|--------------------------------|

**Examples:**
```
export
export --file march_backup.csv
export --file backup.csv --path /Users/me/Documents/
```

**Expected Output:**
```
Exported 15 transactions to: ./transactions_2026-04-02.csv
```

**CSV format written:**
```
HashID,Type,Category,Amount,Date,Description
a7b2c3,credit,salary,3000.00,2026-03-01,March salary
d4e5f6,debit,food,15.50,2026-03-05,"Chicken rice at Clementi"
```

---

### 4.9 `import` — Import from CSV

Imports transactions from a CSV file. By default, all existing data is **replaced**. Use `--merge` to add to existing transactions instead.

**Format:**
```
import --file FILEPATH [--merge]
```

| Parameter | Required | Description                                   |
|-----------|----------|-----------------------------------------------|
| `--file`  | Yes      | Path to the CSV file                          |
| `--merge` | No       | Add to existing data instead of replacing it  |

**Examples:**
```
import --file transactions_2026-03-15.csv
import --file backup.csv --merge
```

**Expected Output:**
```
Import complete: 23 succeeded, 2 failed.
```

> **Warning:** Without `--merge`, all existing transactions are permanently deleted before import.

> **Note:** HashIDs are regenerated for imported transactions to avoid collisions.

---

### 4.10 `clear` — Clear All Data

Permanently deletes **all** transactions. This action cannot be undone.

**Format:**
```
clear [--force]
```

Without `--force`, RLAD will prompt for confirmation:

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

> **Tip:** Run `export` before `clear` to keep a backup of your data.

---

### 4.11 `help` — Get Help

Displays usage instructions. With no argument, shows all available commands. With a command name, shows detailed usage for that command.

**Format:**
```
help [COMMAND]
```

**Examples:**
```
help
help add
help list
help budget
```

---

### 4.12 `exit` — Exit RLAD

Exits the application.

```
exit
```

### 4.13 Advanced Filtering: Tips & Tricks

RLAD’s filtering engine supports complex queries using logical **AND** and **OR** operations.

* **Logical AND:** Providing multiple flags (e.g., `--type debit --amount -gt 50`) only shows transactions meeting **all** criteria.
* **Logical OR:** For the `--category` flag, use commas for multiple matches: `list --category "food,transport"`.
* **Relative Dates:** Instead of full dates, use: `today`, `yesterday`, `this-week`, `last-month`, `this-month`.

### 4.14 Troubleshooting: Common "Bad" Commands

| Bad Command | Why it Fails | The Fix |
|:---|:---|:---|
| `add --amount 50` | Missing required flags. | Add `--type` and `--date`. |
| `add --type pizza --amount 10` | Invalid value. | Type must be `credit` or `debit`. |
| `list --category salary food` | Multiple values without commas. | Use `list --category "salary,food"`. |
| `delete a7b2c3` | Missing flag name. | Use `delete --hashID a7b2c3`. |

---

## 5. Budget Categories

Use these numeric codes for the `budget` command:

| Code | Category                         |
|------|----------------------------------|
| 1    | Food                             |
| 2    | Transport                        |
| 3    | Utilities                        |
| 4    | Housing                          |
| 5    | Health & Insurance               |
| 6    | Debt & Financial Obligation      |
| 7    | Child & Financial Dependent Care |
| 8    | Shopping & Personal Care         |
| 9    | Gifts & Donations                |
| 10   | Investments                      |
| 11   | Emergency Fund                   |
| 12   | Savings                          |
|------|----------------------------------|

> **Note:** The `--category` flag in `add` and `list` accepts free-text strings (e.g., `food`, `transport`). Budget categories are a separate, fixed set used only in the `budget` command.

---

## 6. Date & Amount Formats

### Dates

All date fields accept: `yyyy-MM-dd`

Examples: `2026-03-15`, `2026-01-01`

### Amounts

- Must be a positive number.
- Up to 2 decimal places (e.g., `15.50`, `3000`, `0.99`).
- Do not include currency symbols (`$`) in the input.

---

## 7. FAQ

**Q: What happens if I forget a transaction's HashID?**
> Use `list` to find it. The HashID appears in the leftmost column.

**Q: Can I have multiple budgets for the same category in one month?**
> No. Each category can have at most one budget per month. Use `budget edit` to update an existing one.

**Q: What is "Disposable Income" in the budget view?**
> It is the portion of your recorded income (credits) not allocated to any budget category. A negative value means your allocated budgets exceed your recorded income for that month.

**Q: Can I import a CSV I edited in Excel?**
> Yes, as long as the column headers match exactly: `HashID,Type,Category,Amount,Date,Description`. Rows with invalid data are skipped with a warning.

**Q: Does RLAD save data automatically between sessions?**
> No. Data is held in memory during the session. **Always run `export` before exiting** to save your work to a CSV file.

**Q: What happens to my budgets when I use `clear`?**
> Budget definitions are preserved. Only transaction data is cleared. Budget tracking (spending amounts) will reset to zero since there are no transactions to count.

**Q: Why can't I use two or more dashes "--" in my description?**
> It confuses the parser into thinking a new flag has started. The devs were too lazy to fix it—please don't break the code. Use a single dash `-` instead!

**Q: I typed `list --category none` but it didn't show my salary.**
> `none` and `(none)` are magic keywords that look for transactions with **no category**. Use `list --category salary` to find your paycheck.

---

## 8. Command Summary

| Command    | Format (simplified)                                                        |
|------------|----------------------------------------------------------------------------|
| add        | `add --type TYPE --amount AMT --date DATE [--category C] [--description D]`|
| delete     | `delete --hashID ID`                                                       |
| modify     | `modify --hashID ID [--type T] [--amount A] [--date D] [--category C] ...` |
| list       | `list [--type T] [--category C] [--amount A] [--date D] [--sort F [DIR]]`  |
| sort       | `sort [FIELD [DIR]]` or `sort reset`                                       |
| summarize  | `summarize [--type T] [--category C] [--date-from D] [--date-to D]`        |
| budget     | `budget set/view/edit/delete --month M [--category C] [--amount A]`        |
| export     | `export [--file F] [--path P]`                                             |
| import     | `import --file F [--merge]`                                                |
| clear      | `clear [--force]`                                                          |
| help       | `help [command]`                                                           |
| exit       | `exit`                                                                     |
|------------|----------------------------------------------------------------------------|