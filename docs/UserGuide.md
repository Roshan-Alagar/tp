# RLAD User Guide

## Table of Contents
- [Introduction](#introduction)
- [Quick Start](#quick-start)
- [Commands](#commands)
  - [Adding a transaction: `add`](#adding-a-transaction-add)
  - [Listing transactions: `list`](#listing-transactions-list)
  - [Filtering transactions: `filter`](#filtering-transactions-filter)
  - [Sorting transactions: `sort`](#sorting-transactions-sort)
  - [Deleting a transaction: `delete`](#deleting-a-transaction-delete)
  - [Modifying a transaction: `modify`](#modifying-a-transaction-modify)
  - [Viewing a summary: `summarize`](#viewing-a-summary-summarize)
  - [Getting help: `help`](#getting-help-help)
  - [Exiting the app: `exit`](#exiting-the-app-exit)
- [UI Examples](#ui-examples)
- [FAQ](#faq)
- [Known Issues](#known-issues)
- [Command Summary](#command-summary)

## Introduction

Record Losses And Debt (RLAD) is a minimalist CLI finance tracker for users who want to manage their
spending without the overhead of spreadsheets or bloated apps. Track your income and expenses, sort and
filter transactions, and get quick summaries of where your money is going -- all from your terminal.

## Quick Start

1. Ensure that you have **Java 17** or above installed.
2. Download the latest `RLAD.jar` from the [Releases](https://github.com/AY2526S2-CS2113-W13-4/tp/releases) page.
3. Copy the file to the folder you want to use as the home folder for RLAD.
4. Open a command terminal, `cd` into the folder, and run:
   ```
   java -jar RLAD.jar
   ```
5. You should see the RLAD welcome screen:
   ```
   ╔════════════════════════════════════════════════╗
   ║       ██████╗  ██╗       █████╗  ██████╗       ║
   ║       ██╔══██╗ ██║      ██╔══██╗ ██╔══██╗      ║
   ║       ██████╔╝ ██║      ███████║ ██║  ██║      ║
   ║       ██╔══██╗ ██║      ██╔══██║ ██║  ██║      ║
   ║       ██║  ██║ ███████╗ ██║  ██║ ██████╔╝      ║
   ║       ╚═╝  ╚═╝ ╚══════╝ ╚═╝  ╚═╝ ╚═════╝       ║
   ║              Record Losses And Debt            ║
   ╚════════════════════════════════════════════════╝

   Hello and welcome to RLAD!
   Handle your financial life from one spot without the spreadsheet headaches
   ```
6. Type a command at the `>` prompt and press Enter. Refer to [Commands](#commands) below for details on each command.

## Commands

> **Notes about the command format:**
> - Words in `UPPER_CASE` are parameters to be supplied by the user.
>   e.g. in `add --type TYPE`, `TYPE` is a parameter: `add --type credit`.
> - Items in square brackets are optional.
>   e.g. `list [--sort FIELD]` can be used as `list` or `list --sort amount`.
> - Flags can be provided in any order.
>   e.g. `--type credit --amount 15.00` and `--amount 15.00 --type credit` are both valid.
> - Extra whitespace between flags is ignored.

### Adding a transaction: `add`

Adds a new credit (income) or debit (expense) entry.

**Format:**
```
add --type TYPE --amount AMOUNT --date DATE [--category CATEGORY] [--description DESCRIPTION]
```

| Flag | Required | Description |
|------|----------|-------------|
| `--type` | Yes | `credit` (income) or `debit` (expense) |
| `--amount` | Yes | Dollar amount (e.g. `15.00`) |
| `--date` | Yes | Date in `YYYY-MM-DD` format (e.g. `2026-02-18`) |
| `--category` | No | Category label (e.g. `food`, `transport`) |
| `--description` | No | Short description of the transaction |

> **Tip:** Always include a category and description -- it makes filtering and summarizing much more useful later.

**Example:**
```
> add --type credit --category food --amount 15.00 --date 2026-02-18 --description Hawker stall lunch set
```

### Listing transactions: `list`

Displays all your recorded transactions. Uses the global sort order by default, or you can override
with `--sort` for a one-time sort.

**Format:**
```
list [--sort FIELD [DIRECTION]]
```

| Flag | Required | Description |
|------|----------|-------------|
| `--sort` | No | Override sort by `amount` or `date`. Optionally add `asc` or `desc` (default: `asc`) |

- If no `--sort` flag is provided, the global sort order is used (set via the `sort` command).
- If no global sort is set either, transactions are shown in insertion order.
- The `--sort` flag only affects this command -- it does not change the global sort setting.

**Examples:**

List all transactions:
```
> list
```

List with a one-time sort override (does not change global sort):
```
> list --sort amount desc
```

**Sample output:**
```
[e9d4] CREDIT | 2026-01-01 | $3000.00 | salary | Monthly salary
[f1c3] DEBIT | 2026-02-15 | $25.00 | food | Lunch
[a7b2] DEBIT | 2026-02-10 | $5.50 | transport | Bus fare
```

### Filtering transactions: `filter`

Searches transactions by type, category, amount, or date. Multiple flags are combined to narrow results.

**Format:**
```
filter --FLAG VALUE [--FLAG VALUE ...] [--sort FIELD [DIRECTION]]
```

| Flag | Required | Description |
|------|----------|-------------|
| `--type` | No | Filter by `credit` or `debit` |
| `--category` | No | Filter by category name (case-insensitive) |
| `--amount` | No | Filter by amount with optional operator (`-gt`, `-gte`, `-eq`, `-lt`, `-leq`). Defaults to `-eq` if no operator given |
| `--date` | No | Filter by exact date (`YYYY-MM-DD`) |
| `--date-from` | No | Filter for transactions on or after this date (inclusive) |
| `--date-to` | No | Filter for transactions on or before this date (inclusive) |
| `--sort` | No | Override sort for this command only. Same as `list --sort` |

- At least one filter flag must be provided.
- Multiple `--amount` flags can be used for range queries (e.g. `--amount -gte 50 --amount -lt 150`).
- `--date-from` and `--date-to` can be combined for date ranges.
- Uses the global sort order by default. Use `--sort` to override for this command only.

**Examples:**

Filter by type:
```
> filter --type debit
```

Filter by category:
```
> filter --category food
```

Filter by amount range:
```
> filter --amount -gte 50 --amount -lt 150
```

Filter by date range:
```
> filter --date-from 2024-01-01 --date-to 2024-06-30
```

Combine multiple filters with a sort override:
```
> filter --type debit --amount -gt 10 --sort amount desc
```

### Sorting transactions: `sort`

Sets or views the global sort order. This affects how `list` and `filter` display transactions by default.

**Format:**
```
sort [FIELD [DIRECTION]]
sort reset
```

| Argument | Required | Description |
|----------|----------|-------------|
| `FIELD` | No | Sort by `amount` or `date` |
| `DIRECTION` | No | `asc` (ascending, default) or `desc` (descending) |

- `sort` with no arguments shows the current sort setting.
- `sort reset` clears the global sort, returning to insertion order.
- The global sort persists for the session and is applied to both `list` and `filter`.
- Use `--sort` on `list` or `filter` to override the global sort for a single command.

**Examples:**

Set global sort to amount descending:
```
> sort amount desc
Sort order set: amount (desc)
```

View current sort setting:
```
> sort
Current sort: amount (desc)
```

Clear the sort order:
```
> sort reset
Sort order cleared. Transactions will be shown in insertion order.
```

### Deleting a transaction: `delete`

Removes a transaction from the records permanently using its hash ID.

**Format:**
```
delete <id>
```

- `id` is the 4-character identifier shown in square brackets when you run `list` (e.g. `a7b2`).

> **Caution:** This action is irreversible. Once deleted, the transaction cannot be recovered.

> **Caution:** `delete` is currently under development. The command is recognised but execution logic is not
> yet implemented.

**Example:**
```
> delete a7b2
```

### Modifying a transaction: `modify`

Updates specific fields of an existing entry via its hash ID. Only the fields you specify will be changed;
all other fields remain unchanged.

**Format:**
```
modify <id> --amount <new amount>
```

- `id` is the 4-character identifier shown in square brackets when you run `list` (e.g. `a7b2`).

**Example:**
```
> modify a7b2 --amount 20.00
```

> **Caution:** `modify` is currently under development. The command is recognised but execution logic is not
> yet implemented.

### Viewing a summary: `summarize`

Provides a statistical overview of your finances.

**Format:**
```
summarize [--by GROUPING]
```

| Flag | Required | Description |
|------|----------|-------------|
| `--by` | No | Group by `category`, `month`, or `type` |

**Example:**
```
> summarize --by category
```

> **Caution:** `summarize` is currently under development. The command is recognised but execution logic is
> not yet implemented.

### Getting help: `help`

Displays available commands and their usage. Use `help` on its own to see all commands, or specify a command
name to see its detailed manual.

**Format:**
```
help [COMMAND_NAME]
```

**Examples:**

View all available commands:
```
> help
```

View the manual for a specific command:
```
> help add
> help list
```

### Exiting the app: `exit`

Exits the application.

```
> exit
Thank you for abusing me!
 See you next time...
```

> **Tip:** Your data is not saved between sessions in this version. Persistent storage is planned for a
> future release.

## UI Examples

Here is what a typical session looks like:

```
▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
Hello and welcome to RLAD!
Handle your financial life from one spot without the spreadsheet headaches
Available actions:
  add       : Record a new transaction
  modify    : Edit an existing entry
  delete    : Remove an entry
  filter    : Search transactions by type, category, amount, or date
  sort      : Set or view the global sort order (amount/date, asc/desc)
  list      : View your transaction history
  summarize : Get a high-level breakdown of your spending

Format:
	$action --option_0 $argument_0 ... --option_k $argument_k
Type 'help' for the full list or '$action help' for specific argument details.
▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
> list --sort amount
Your wallet is empty! Use 'add' to record a transaction.
▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀
> exit
Thank you for abusing me!
 See you next time...
```

## FAQ

**Q**: How do I find the hash ID of a transaction?

**A**: Use the `list` command. Each transaction is displayed with its 4-character hash ID in square brackets
at the start, e.g. `[a7b2]`.

**Q**: Can I sort transactions in descending order?

**A**: Yes. Use `sort amount desc` or `sort date desc` to set descending order globally, or use
`list --sort amount desc` / `filter --sort date desc` for a one-time override.

**Q**: What happens if I enter an invalid command?

**A**: RLAD will show an error message and display the list of available commands to guide you.

**Q**: Is my data saved when I close the app?

**A**: Not in the current version. Data persistence is planned for a future release.

## Known Issues

1. **No persistent storage** -- All transaction data is lost when the app exits. A file-based save/load
   system is planned.
2. **Hash ID collisions** -- The 4-character hash IDs have a small chance of collision. Collision detection
   and regeneration is not yet implemented.
3. **Commands under development** -- `delete`, `modify`, and `summarize` are recognised by the parser
   but their execution logic is not yet implemented. They will print placeholder messages.

## Command Summary

| Command | Format | Status |
|---------|--------|--------|
| **add** | `add --type TYPE --amount AMOUNT --date DATE [--category CAT] [--description DESC]` | Working |
| **list** | `list [--sort FIELD [DIRECTION]]` | Working |
| **filter** | `filter --FLAG VALUE [--FLAG VALUE ...] [--sort FIELD [DIRECTION]]` | Working |
| **sort** | `sort [FIELD [DIRECTION]]` / `sort reset` | Working |
| **delete** | `delete <id>` | Planned |
| **modify** | `modify <id> --amount <new amount>` | Planned |
| **summarize** | `summarize [--by category\|month\|type]` | Planned |
| **help** | `help [COMMAND]` | Planned |
| **exit** | `exit` | Working |
