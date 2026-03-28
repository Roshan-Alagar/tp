# Developer Guide

## Acknowledgements

- Architecture inspired by [AddressBook-Level2](https://github.com/se-edu/addressbook-level2)
- Java `Predicate` chaining pattern adapted from Java 8 Streams documentation

## Design & Implementation

### Architecture Overview

RLAD follows the **MVC (Model-View-Controller)** pattern combined with the **Command Design Pattern**.

The main loop in `RLAD.java` drives the application:
1. Read input via `Ui.readCommand()`
2. Parse input via `Parser.parse()`
3. Execute the returned `Command` object
4. Display results via `Ui`

### ListCommand Implementation

`ListCommand` allows users to view, filter, and sort transactions without modifying data in `TransactionManager`.

1. Delegates flag parsing to `FilterCommand.parseFlags()`
2. Validates the `--sort` flag (must be `date` or `amount`)
3. Calls `FilterCommand.buildPredicate()` to build a composite `Predicate<Transaction>`
4. Applies the predicate using Java Streams
5. Sorts and displays results in a formatted table

### DeleteCommand Implementation

`DeleteCommand` removes a transaction by hash ID:
1. Validates `--hashID` flag is present
2. Checks transaction exists via `findTransaction()`
3. Removes it via `deleteTransaction()`
4. Shows success or error message


## Product scope
### Target user profile

NUS students or young adults managing personal finances from the command line who prefer a lightweight tool over GUI apps or spreadsheets.

### Value proposition

RLAD lets users record, filter, sort, and summarize financial transactions entirely from the terminal — faster than GUI apps for keyboard-driven users.

## User Stories

|Version| As a ... | I want to ... | So that I can ...|
|--------|----------|---------------|------------------|
|v1.0|new user|see usage instructions|refer to them when I forget how to use the application|
|v2.0|user|find a to-do item by name|locate a to-do without having to go through the entire list|

## Non-Functional Requirements

{Give non-functional requirements}

## Glossary

* **Transaction** — A financial record with type, amount, date, optional category and description.
* **HashID** — A 4-character unique identifier auto-generated for each transaction (e.g. `a7b2`).
* **Credit** — An income transaction (money in).
* **Debit** — An expense transaction (money out).
* **Predicate** — A filter condition that returns true/false for a given transaction.

## Instructions for Manual Testing

1. Run `java -jar duke.jar`
2. Add a transaction: `add --type credit --amount 3000.00 --date 2026-01-15 --category salary`
3. List all: `list`
4. Filter by type: `list --type credit`
5. Sort by amount: `list --sort amount`
6. Delete by ID (use HashID from list output): `delete --hashID a7b2`
7. Summarize: `summarize`
8. Exit: `exit`
