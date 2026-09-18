package com.library.util;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public final class ReportGenerator {

    private ReportGenerator() {
    }

    public static void generateBookReport(Collection<Book> books, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Book Inventory Report - Generated " + timestamp());
            writer.newLine();
            writer.write("BookID,Title,Author,ISBN,Category,Available,Total");
            writer.newLine();
            for (Book b : books) {
                writer.write(String.format("%d,%s,%s,%s,%s,%d,%d",
                        b.getBookId(), escape(b.getTitle()), escape(b.getAuthor()),
                        b.getIsbn(), b.getCategory(), b.getAvailableCopies(), b.getTotalCopies()));
                writer.newLine();
            }
            AppLogger.info("Book report written to " + filePath);
        } catch (IOException e) {
            AppLogger.error("Failed to write book report: " + e.getMessage());
        }
    }

    public static void generateTransactionReport(List<Transaction> transactions, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Transaction Report - Generated " + timestamp());
            writer.newLine();
            writer.write("TransactionID,BookID,MemberID,IssueDate,DueDate,ReturnDate,Fine,Status");
            writer.newLine();
            for (Transaction t : transactions) {
                writer.write(String.format("%d,%d,%d,%s,%s,%s,%.2f,%s",
                        t.getTransactionId(), t.getBookId(), t.getMemberId(),
                        t.getIssueDate(), t.getDueDate(),
                        t.getReturnDate() == null ? "" : t.getReturnDate(),
                        t.getFineAmount(), t.getStatus()));
                writer.newLine();
            }
            AppLogger.info("Transaction report written to " + filePath);
        } catch (IOException e) {
            AppLogger.error("Failed to write transaction report: " + e.getMessage());
        }
    }

    public static void generateMemberReport(Collection<Member> members, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Member Report - Generated " + timestamp());
            writer.newLine();
            writer.write("MemberID,Name,MembershipID,Email,Phone,BorrowedCount,MaxAllowed");
            writer.newLine();
            for (Member m : members) {
                writer.write(String.format("%d,%s,%s,%s,%s,%d,%d",
                        m.getId(), escape(m.getName()), m.getMembershipId(),
                        m.getEmail(), m.getPhone(), m.getBorrowedBooksCount(), m.getMaxBooksAllowed()));
                writer.newLine();
            }
            AppLogger.info("Member report written to " + filePath);
        } catch (IOException e) {
            AppLogger.error("Failed to write member report: " + e.getMessage());
        }
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace(",", ";");
    }

    private static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
