package com.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnectionManager {

    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static DatabaseConnectionManager instance;
    private Connection connection;

    private DatabaseConnectionManager() {

    }

    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
        }
        return connection;
    }

    public void initializeSchema() {
        String books = "CREATE TABLE IF NOT EXISTS books (" +
                "book_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "author TEXT NOT NULL," +
                "isbn TEXT UNIQUE," +
                "category TEXT," +
                "total_copies INTEGER NOT NULL," +
                "available_copies INTEGER NOT NULL)";

        String members = "CREATE TABLE IF NOT EXISTS members (" +
                "member_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT," +
                "phone TEXT," +
                "membership_id TEXT UNIQUE NOT NULL," +
                "max_books_allowed INTEGER NOT NULL DEFAULT 3," +
                "borrowed_books_count INTEGER NOT NULL DEFAULT 0)";

        String transactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "book_id INTEGER NOT NULL," +
                "member_id INTEGER NOT NULL," +
                "issue_date TEXT NOT NULL," +
                "due_date TEXT NOT NULL," +
                "return_date TEXT," +
                "fine_amount REAL DEFAULT 0.0," +
                "status TEXT NOT NULL," +
                "FOREIGN KEY(book_id) REFERENCES books(book_id)," +
                "FOREIGN KEY(member_id) REFERENCES members(member_id))";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(books);
            stmt.execute(members);
            stmt.execute(transactions);
        } catch (SQLException e) {
            System.err.println("Failed to initialize schema: " + e.getMessage());
        }
    }

    public synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
