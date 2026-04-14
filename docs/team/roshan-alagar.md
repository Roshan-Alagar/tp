---
layout: page
title: Roshan Alagar - Project Portfolio Page
---

# Roshan Alagar - Project Portfolio Page

## **Overview**

**Project:** Record Losses And Debt (RLAD)

RLAD is a minimalist **Command-Line Interface (CLI) finance tracker** built for power users who prefer the speed and
efficiency of a terminal. Unlike bloated apps or complex spreadsheets, RLAD allows users to track income/expenses,
set budget goals, and get instant summaries with simple, intuitive commands.

My primary role was to implement the **data retrieval and reporting engine**, including the transaction listing,
filtering, and searching features. I also led the effort in **Data Integrity**, implementing guard clauses and
input validation to ensure the reliability of the financial data stored by the application.

## **Summary of Contributions**

**Primary Role:** Developer for Data Retrieval, Reporting, and Input Validation.

**Code contributed:** [RepoSense Link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=roshan-alagar&breakdown=true)

---

### **Enhancements Implemented**

| Enhancement | Description | Key Technical Highlights |
| :--- | :--- | :--- |
| **Transaction Filtering & Listing** | Implemented the full `list` command allowing users to query their financial history. | - Supports multi-flag filtering: `--type`, `--cat`, `--min`, `--max`, `--from`, and `--to`.<br>- Integrated a secondary sorting layer (`--sort`) to prioritize specific views during listing. |
| **Shared Filtering Logic (`FilterCommand`)** | Architected the predicate-building engine used by both `ListCommand` and `SummarizeCommand`. | - Used **Java Predicate chaining** (`.and()`) to create composable, highly efficient filter conditions.<br>- Implemented **Amount Comparison Operators** (e.g., `-gt`, `-leq`) for flexible numeric queries. |
| **Data Integrity & Guard Clauses** | Implemented strict validation for user input across `Add`, `Modify`, and `Budget` commands. | - Created **Regex-based guard clauses** (`matches("-?\\d+(\\.\\d+)?")`) to prevent purely numerical categories (e.g., "1111"), improving UX and data clarity.<br>- Resolved scope collision bugs in the `BudgetCommand` to prevent compilation failures. |
| **Budget Yearly Summary** | Built a 12-month financial reporting tool. | - Developed a tabular yearly view showing budget allocations vs. actual spending.<br>- Created a **dynamic ASCII progress bar** to visualize budget utilization across the fiscal year. |

---
<div style="page-break-after: always;"></div>

### **Contributions to User Experience Design**

I focused on ensuring that RLAD handles user errors gracefully through **defensive programming**:

*   **Input Sanitization:** I implemented validation logic to ensure categories remain descriptive. This prevents users from accidentally creating confusing data like `add debit 10 2026-03-05 1234`, where a user might mistake the category "1234" for an amount or ID.
*   **Search Alias System:** I implemented the `find` alias for the `search` command, accommodating different user mental models for data retrieval.

---

### **Contributions to the User Guide (UG)**

I authored and maintained the documentation for the reporting features:

- **ListCommand (Section 4.4):** Documented the complex filtering syntax, including date range keywords (`today`, `this-month`) and amount operators.
- **Budget Yearly Summary (Section 4.7.5):** Wrote the technical documentation for interpreting the yearly summary, explaining the "Disposable Income" calculation.
- **Validation Rules:** Added documentation regarding restricted category names to help users avoid input errors.

---

### **Contributions to the Developer Guide (DG)**

| Section | Contribution |
|---------|-------------|
| **3. Design** | Authored the **MVC Architecture Diagram** showing the flow from the `Parser` to the `Command` and `Model` layers. |
| **4.3 List Implementation** | Documented the technical sequence of the filtering engine, including how the `FilterCommand` composes predicates dynamically. |
| **4.4 Filter Logic** | Provided a deep dive into the **Predicate Chaining** pattern used to enable multi-flag filtering. |
| **Manual Testing** | Authored the comprehensive testing suite for `list`, `search`, and `delete` commands, covering edge cases like empty result sets and invalid regex patterns. |

---

### **Reviewing Contributions**

I served as a core reviewer for the team, ensuring that all code entering the `master` branch met our quality standards:

*   **PR Approvals:** Actively reviewed and approved Pull Requests for the `SummarizeCommand`, `Export/Import` logic, and `BudgetManager` updates.
*   **Bug Detection:** During a code review of the `BudgetCommand` PR, I identified a critical **variable redefinition bug** that would have caused a build failure. I provided the fix to resolve the compilation error and unblock the team.
*   **Consistency Checks:** Verified that new features implemented by teammates adhered to the project's design patterns, such as ensuring all new commands correctly implemented the `hasValidArgs()` contract.

---

### **Team-Based & Beyond-Project Contributions**

- **Project Infrastructure:** Added `java.util.logging.Logger` to core parser classes and enabled assertions (`-ea`) in the `build.gradle` to ensure the CI pipeline catches logic errors during PRs.
- **Quality Assurance:** Maintained a test suite of over 30 unit tests, ensuring that new features (like position-based parsing) did not break existing filtering logic.
- **Documentation Coordination:** Standardized the format for the Command Summary tables across both the UG and DG to ensure a consistent look and feel for the project documentation.
