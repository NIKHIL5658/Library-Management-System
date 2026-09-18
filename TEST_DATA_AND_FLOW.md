# Sample Test Data & Run-Through Guide (IntelliJ IDEA)

This file gives you ready-to-type sample data and the exact sequence of menu
choices to exercise every part of the Library Management System after you
run `Main.java` in IntelliJ.

> **Is this project runnable in IntelliJ IDEA?**
> Yes — verified end-to-end in IntelliJ IDEA 2026.2 on JDK 21/25, and it also
> works in VS Code with the "Extension Pack for Java". The project is a
> standard Maven project (see `pom.xml`), so opening the folder as a Maven
> project lets your IDE download the `sqlite-jdbc` driver automatically —
> no manual jar downloading or classpath editing required. See
> **README.md → "Steps to Install & Run"** for IDE-specific steps. Once
> Maven has resolved the dependency, right-click `Main.java` (under
> `src/main/java/com/library/`) → **Run 'Main.main()'** and the console
> menu below appears immediately.

---

## 1. Sample Books to add (menu option 1)

| # | Title | Author | ISBN | Category | Copies |
|---|---|---|---|---|---|
| 1 | Effective Java | Joshua Bloch | 9780134685991 | Programming | 3 |
| 2 | Clean Code | Robert C. Martin | 9780132350884 | Programming | 2 |
| 3 | The Pragmatic Programmer | Andrew Hunt | 9780135957059 | Programming | 1 |
| 4 | Introduction to Algorithms | Cormen et al. | 9780262046305 | Computer Science | 2 |

## 2. Sample Members to add (menu option 2)

| # | Name | Email | Phone | Membership ID | Max Books Allowed |
|---|---|---|---|---|---|
| 1 | Asha Rao | asha.rao@example.com | 9998887777 | MEM001 | 3 |
| 2 | Rahul Verma | rahul.verma@example.com | 9876543210 | MEM002 | 2 |
| 3 | Priya Nair | priya.nair@example.com | 9123456780 | MEM003 | 1 |

---

## 3. Step-by-step run-through

Run `Main.java`. You'll see the menu:

```
===== Library Management System =====
1. Add Book
2. Add Member
3. Issue Book
4. Return Book
5. List All Books
6. List All Members
7. Generate Reports
8. Exit
Enter choice:
```

### Step 1 — Add the sample books (repeat 4 times)
Choose **1**, then type each row from the Books table above when prompted
(Title, Author, ISBN, Category, Number of copies). Each time, the console
confirms with the generated Book, e.g.:
```
Book added: Book[ID=1, Title='Effective Java', Author='Joshua Bloch', ISBN=9780134685991, Category=Programming, Available=3/3]
```
Note the **Book ID** printed for each — you'll need these IDs in Step 3.

### Step 2 — Add the sample members (repeat 2–3 times)
Choose **2**, then type each row from the Members table above. The console
confirms with the generated Member, e.g.:
```
Member added: [Member] ID=1, Name=Asha Rao, ... MembershipID=MEM001, Borrowed=0/3
```
Note the **Member ID** printed for each.

### Step 3 — Issue a book
Choose **3**, then enter a **Book ID** and **Member ID** from Steps 1–2
(e.g. Book ID `1`, Member ID `1`). Expected result:
```
Book issued successfully: Transaction[ID=1, BookID=1, MemberID=1, Issued=<today>, Due=<today+14>, ...]
```

**To test the exception-handling paths on purpose:**
- Issue Book ID `99` (doesn't exist) → `InvalidMemberException` message.
- Issue every copy of a book (e.g. issue Book ID 3, which has only 1 copy,
  then try to issue it again) → `BookNotAvailableException` message.
- Issue books to Member ID `3` (max 1 book) twice in a row →
  `MemberLimitExceededException` message on the second attempt.

### Step 4 — Check the state changed
Choose **5** (List All Books) — the issued book's available copies should
have decreased by 1. Choose **6** (List All Members) — the member's
borrowed count should have increased by 1.

### Step 5 — Return the book
Choose **4**, enter the Member ID you issued to (e.g. `1`). The console
lists that member's active loans with their Transaction IDs — enter the
Transaction ID to return it. Expected result:
```
Book returned. Fine due: 0.00
```
(Fine will be non-zero only if you test this more than 14 days after
issuing — see Section 4 below for how to test that without waiting.)

### Step 6 — Generate reports
Choose **7**. Confirm `book_report.csv`, `member_report.csv`, and
`transaction_report.csv` appear in your project's working directory with
the data you just entered.

### Step 7 — Exit
Choose **8** to shut down cleanly (stops the background thread and closes
the database connection).

---

## 4. Testing the fine calculation without waiting 14 days

`FineCalculator` compares the transaction's `due_date` (stored in the
database) against today's date. To test a non-zero fine quickly:

1. Add a book and member, then issue the book as in Step 3.
2. Close the app, and using any SQLite browser (e.g. "DB Browser for
   SQLite", or IntelliJ's built-in Database tool: **View → Tool Windows →
   Database → + → Data Source → SQLite**, pointing at your `library.db`),
   open the `transactions` table and edit that row's `due_date` to a date a
   few days in the past.
3. Re-run the app, return that transaction (Step 5) — the fine should now
   show as `days_late × 5`.

---

## 5. Code flow reference: what happens when you "Add Book" / "Add Member"

**Add Book (menu option 1):**
```
Main.addBookFlow()
  → reads Title/Author/ISBN/Category/Copies from Scanner
  → LibraryService.addBook(...)
      → constructs a Book object
      → BookDAOImpl.addBook(book)
          → PreparedStatement: INSERT INTO books (...) VALUES (...)
          → returns the auto-generated book_id from the database
      → puts the saved Book into LibraryService's in-memory bookCache (HashMap)
      → AppLogger logs the addition
  → Main prints the returned Book's toString()
```

**Add Member (menu option 2)** follows the identical pattern through
`LibraryService.addMember()` → `MemberDAOImpl.addMember()` → `INSERT INTO
members`.

Both operations are a straight vertical path down the architecture:
**Main → Service layer → DAO layer → JDBC → library.db** — the same flow
diagrammed in Section 5 of the project report.
