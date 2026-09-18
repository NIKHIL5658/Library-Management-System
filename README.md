# Library Management System with Automated Fine Tracking & Multithreaded Reminders

A console-based Library Management System built in core Java, demonstrating object-oriented
design, custom exception handling, multithreading, the Java Collections Framework, I/O streams,
and JDBC-based persistence — developed for **CSE2006 Programming in Java**.

## Overview

Manual library record-keeping is slow and error-prone: tracking who has which book, computing
overdue fines, and remembering to remind members are all manual, ad-hoc tasks. This system
automates the full lifecycle — cataloguing books, registering members, issuing/returning books,
calculating fines automatically, and running a background thread that periodically scans for
and reports overdue loans — all backed by a real SQLite database via JDBC.

## Features

- **Book & Member Management** — add, update, and look up books and members (CRUD via DAO pattern)
- **Issue / Return workflow** — validates availability and borrowing limits using custom checked
  exceptions before any transaction is recorded
- **Automatic fine calculation** — computes overdue fines based on due date vs. return date
- **Background overdue-notification thread** — a daemon thread that wakes up on an interval,
  queries the database for overdue loans, and logs reminders — independent of the main menu loop
- **Report generation** — exports book, member, and transaction data to CSV files using Java I/O
  streams
- **Persistent storage** — all data is stored in a real SQLite database file (`library.db`), not
  in memory, so data survives across runs

## Technologies / Tools Used

| Category | Technology |
|---|---|
| Language | Java (JDK 21+) |
| Persistence | SQLite via JDBC (`org.xerial:sqlite-jdbc`) |
| Design patterns | DAO, Singleton, layered (Service/DAO) architecture |
| Concurrency | `java.lang.Thread` / `Runnable`, `synchronized` |
| Collections | `HashMap`, `ArrayList` (in-memory caching) |
| I/O | `BufferedWriter`, `FileWriter` (CSV report export) |
| Logging | `java.util.logging` |
| IDE | IntelliJ IDEA |
| Version control | Git / GitHub |

## Project Structure

```
library-management-system/
├── README.md
├── statement.md
├── src/main/java/com/library/
│   ├── model/          # Person, Member, Librarian, Book, Transaction, TransactionStatus
│   ├── exceptions/     # BookNotAvailableException, MemberLimitExceededException, InvalidMemberException
│   ├── db/             # DatabaseConnectionManager (JDBC singleton)
│   ├── dao/            # BookDAO, MemberDAO, TransactionDAO + JDBC implementations
│   ├── service/        # LibraryService, TransactionService, FineCalculator
│   ├── thread/         # OverdueNotificationService (background reminder thread)
│   ├── util/           # AppLogger, ReportGenerator
│   └── Main.java       # Console entry point
├── docs/                # Architecture, UML, and ER diagrams
└── Library_Management_System_Project_Report.pdf
```

## Steps to Install & Run

This project is a standard Maven project (source under `src/main/java`, dependencies declared
in `pom.xml`), so Maven-aware IDEs resolve the SQLite JDBC driver automatically — no manual jar
downloading or classpath editing required.

### Prerequisites
- JDK 17 or later
- Apache Maven (bundled with IntelliJ; install separately for VS Code / command line)
- IntelliJ IDEA or VS Code (with the "Extension Pack for Java")

### Option A — IntelliJ IDEA (recommended)
1. **File → Open**, select the project's root folder (the one containing `pom.xml`).
2. IntelliJ detects `pom.xml` automatically and prompts to load it as a Maven project — click
   **Load Maven Project** (or the small "m" icon that appears in the bottom-right notification).
   It will download `sqlite-jdbc` automatically; no manual jar/Project Structure steps needed.
3. Once indexing finishes, open `src/main/java/com/library/Main.java`, right-click →
   **Run 'Main.main()'**.

### Option B — VS Code
1. Install the **"Extension Pack for Java"** (includes Maven for Java support) from the
   Extensions marketplace if you haven't already.
2. Open the project's root folder (**File → Open Folder**, select the folder containing
   `pom.xml`).
3. VS Code detects the Maven project automatically. If `Main.java` still shows a
   "not on the classpath" warning, run **Java: Clean the Java language server workspace**
   from the Command Palette (Ctrl+Shift+P) and reload the window.
4. Open `Main.java` and click **Run** above the `main` method, or use the terminal:
   ```bash
   mvn compile exec:java
   ```

### Option C — Command line (Maven)
```bash
mvn compile exec:java
```
This compiles the project and runs it with the SQLite driver automatically on the classpath
(Maven downloads it into your local `~/.m2` repository on first run).

To build a standalone runnable jar with the driver bundled inside it:
```bash
mvn package
java -jar target/library-management-system.jar
```

### Option D — Plain `javac`/`java` (no Maven, no IDE)
If you'd rather not use Maven at all, you can still compile and run manually — you'll just need
to download the driver jar yourself first (e.g. `sqlite-jdbc-3.53.4.0.jar` from
[Maven Central](https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/)):
```bash
# Compile
javac -d out $(find src/main/java -name "*.java")

# Run (with the driver jar on the classpath)
java -cp "out:sqlite-jdbc-3.53.4.0.jar" com.library.Main
```
> On Windows, replace the `:` in the classpath with `;`.

On first run, the application automatically creates `library.db` in the working directory and
initializes the required tables.

## Instructions for Testing

1. Launch the application and use **option 1** to add at least one book, and **option 2** to add
   at least one member.
2. Use **option 3 (Issue Book)** with the Book ID and Member ID you just created — you should see
   a confirmation message and the book's available copy count decrease (verify with **option 5**).
3. Try issuing a book with an invalid ID, or issuing more books than a member's limit allows, to
   confirm the custom exceptions (`BookNotAvailableException`, `MemberLimitExceededException`,
   `InvalidMemberException`) are triggered with clear error messages instead of crashing.
4. Use **option 4 (Return Book)** to return an active loan and confirm the fine calculation
   (0 if returned on time, otherwise `days_late × 5` currency units).
5. Use **option 7 (Generate Reports)** and confirm `book_report.csv`, `member_report.csv`, and
   `transaction_report.csv` appear in the project directory with correct data.
6. Leave the application running and observe the console — every 60 seconds the background
   thread logs an overdue scan (`"Overdue scan complete: no overdue books."` or a list of
   reminders if a loan's due date has passed).

## Screenshots

See Section 10 of the project report for sample console output, including a verified run
showing successful database initialization and the background notification thread starting
correctly.

## Author
Nikhil Kumar <br>
25BAI10465

