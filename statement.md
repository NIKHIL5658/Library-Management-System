# Problem Statement

Manual library management — tracking which books are with which members, due
dates, and fines — is error-prone, time-consuming, and does not scale well
for even a moderately sized library. Librarians need a reliable, automated
system to catalogue books, manage memberships, track issue/return
transactions, and calculate overdue fines without manual bookkeeping.

## Scope of the Project
- Manage a catalogue of books (add, view, track available/total copies)
- Manage library members and their borrowing limits
- Issue and return books with automatic due-date assignment (14-day loan period)
- Automatically calculate fines for overdue returns
- Run a background service that periodically checks for and reports overdue loans
- Generate exportable transaction reports (CSV) for record-keeping
- Persist all data in a relational database via JDBC

**Out of scope:** multi-branch library support, user authentication/login
system, and a graphical/web front end (console-based for this submission).

## Target Users
- **Librarians / Library Staff** — who catalogue books, register members, and
  process issue/return transactions
- **Library Members** — whose borrowing activity and fines are tracked by staff
  on their behalf (no direct login in this version)

## High-Level Features
1. Book catalogue management (add/list books with availability tracking)
2. Member management (add/list members with borrowing limits)
3. Issue/Return workflow with validation (availability check, borrowing-limit check)
4. Automatic fine calculation for overdue returns
5. Background multithreaded reminder service for overdue books
6. CSV transaction report generation
7. Centralized logging of all key system events
8. Persistent storage via JDBC/SQLite with parameterized queries for security
