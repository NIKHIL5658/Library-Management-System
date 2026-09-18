package com.library.dao;

import com.library.db.DatabaseConnectionManager;
import com.library.model.Transaction;
import com.library.model.TransactionStatus;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {

    private final DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();

    @Override
    public int addTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (book_id, member_id, issue_date, due_date, return_date, fine_amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getBookId());
            ps.setInt(2, t.getMemberId());
            ps.setString(3, t.getIssueDate().toString());
            ps.setString(4, t.getDueDate().toString());
            ps.setString(5, t.getReturnDate() == null ? null : t.getReturnDate().toString());
            ps.setDouble(6, t.getFineAmount());
            ps.setString(7, t.getStatus().name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("addTransaction failed: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean updateTransaction(Transaction t) {
        String sql = "UPDATE transactions SET return_date=?, fine_amount=?, status=? WHERE transaction_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getReturnDate() == null ? null : t.getReturnDate().toString());
            ps.setDouble(2, t.getFineAmount());
            ps.setString(3, t.getStatus().name());
            ps.setInt(4, t.getTransactionId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateTransaction failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("getAllTransactions failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Transaction> getActiveTransactionsForMember(int memberId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE member_id=? AND status != 'RETURNED'";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("getActiveTransactionsForMember failed: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Transaction> getOverdueTransactions() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE status != 'RETURNED' AND due_date < ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, LocalDate.now().toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("getOverdueTransactions failed: " + e.getMessage());
        }
        return list;
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        Transaction t = new Transaction(
                rs.getInt("transaction_id"),
                rs.getInt("book_id"),
                rs.getInt("member_id"),
                LocalDate.parse(rs.getString("issue_date")),
                LocalDate.parse(rs.getString("due_date"))
        );
        String returnDateStr = rs.getString("return_date");
        if (returnDateStr != null) {
            t.setReturnDate(LocalDate.parse(returnDateStr));
        }
        t.setFineAmount(rs.getDouble("fine_amount"));
        t.setStatus(TransactionStatus.valueOf(rs.getString("status")));
        return t;
    }
}
