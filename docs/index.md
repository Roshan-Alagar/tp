<style>
@media print {
  .gh-header-actions, .file-navigation, .repository-content > .Box,
  .markdown-body .zeroclipboard-container, button.js-clipboard-copy,
  .position-relative button, .copilot-notice, nav, footer,
  .Header, .js-header-wrapper, .flash, .pagehead { display: none !important; }
  h2 { page-break-before: always; }
  h3 { page-break-after: avoid; }
  pre, table { page-break-inside: avoid; }
}
</style>

# RLAD User Guide

> **RLAD** — *Record Losses And Debt* — is a fast, keyboard-driven personal finance manager for the command line.

---

## Table of Contents

1. [Introduction](#introduction)
2. [Quick Start](#quick-start)
3. [Command Summary](#command-summary)
4. [FAQ](#faq)

---

## Introduction

Record Losses And Debt (RLAD) is a lightweight, terminal-native personal finance tracker built for users who prefer typed commands over spreadsheets and GUIs. Track your income and expenses, sort and filter transactions, set monthly budgets, and get quick summaries of where your money is going — all from your terminal.

---

## Quick Start

1. Ensure that you have **Java 17** or above installed (`java --version` to verify).
2. Download the latest `RLAD.jar` from the [Releases](https://github.com/AY2526S2-CS2113-W13-4/tp/releases) page.
3. Copy the file to the folder you want to use as the home folder for RLAD.
4. Open a command terminal, `cd` into the folder, and run:
   ```
   java -jar RLAD.jar
   ```
5. Try these commands to get started:
   ```
   add credit 3000.00 2026-03-01 salary "March salary"
   add debit 15.50 2026-03-05 food "Chicken rice"
   list
   summarize
   exit
   ```

For the full command reference, see the [User Guide](UserGuide.md).

---

## Command Summary

| Command     | Syntax                                                    |
|-------------|-----------------------------------------------------------|
| `add`       | `add <type> <amount> <date> [category] [description]`     |
| `delete`    | `delete <hashID>`                                         |
| `modify`    | `modify <hashID> [field=value ...]`                       |
| `list`      | `list [type:T] [cat:C] [from:D] [to:D] [min:A] [max:A]`  |
| `sort`      | `sort [field [direction]]` \| `sort reset`                |
| `summarize` | `summarize [type:T] [cat:C] [from:D] [to:D]`             |
| `budget`    | `budget <set\|view\|edit\|delete\|yearly> [args...]`      |
| `export`    | `export [filename]`                                       |
| `import`    | `import <filename> [merge]`                               |
| `clear`     | `clear [--force]`                                         |
| `search`    | `search <keyword>`                                        |
| `help`      | `help [command]`                                          |
| `exit`      | `exit`                                                    |

---

## FAQ

**Q: How do I find the HashID of a transaction?**

Use the `list` command. Each transaction is displayed with its 6-character HashID in the leftmost column.

**Q: Is my data saved when I close the app?**

Yes. RLAD automatically saves your data after every change. Your transactions and budgets are restored when you reopen the app from the same directory.

**Q: Can I export my data to Excel?**

Yes. Use `export backup.csv` to create a CSV file that can be opened in Excel or Google Sheets.
