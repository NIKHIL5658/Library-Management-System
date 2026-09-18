

CREATE TABLE IF NOT EXISTS books (
    book_id           INTEGER PRIMARY KEY AUTOINCREMENT,
    title             TEXT NOT NULL,
    author            TEXT NOT NULL,
    isbn              TEXT UNIQUE,
    category          TEXT,
    total_copies      INTEGER NOT NULL,
    available_copies  INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS members (
    member_id             INTEGER PRIMARY KEY AUTOINCREMENT,
    name                  TEXT NOT NULL,
    email                 TEXT,
    phone                 TEXT,
    membership_id         TEXT UNIQUE NOT NULL,
    max_books_allowed     INTEGER NOT NULL DEFAULT 3,
    borrowed_books_count  INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id  INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id         INTEGER NOT NULL,
    member_id       INTEGER NOT NULL,
    issue_date      TEXT NOT NULL,
    due_date        TEXT NOT NULL,
    return_date     TEXT,
    fine_amount     REAL DEFAULT 0.0,
    status          TEXT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (member_id) REFERENCES members(member_id)
);


