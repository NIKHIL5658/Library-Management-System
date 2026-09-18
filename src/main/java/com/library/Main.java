package com.library;

import com.library.dao.*;
import com.library.exceptions.BookNotAvailableException;
import com.library.exceptions.InvalidMemberException;
import com.library.exceptions.MemberLimitExceededException;
import com.library.db.DatabaseConnectionManager;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.service.LibraryService;
import com.library.service.TransactionService;
import com.library.thread.OverdueNotificationService;
import com.library.util.AppLogger;
import com.library.util.ReportGenerator;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();
        dbManager.initializeSchema();

        BookDAO bookDAO = new BookDAOImpl();
        MemberDAO memberDAO = new MemberDAOImpl();
        TransactionDAO transactionDAO = new TransactionDAOImpl();

        LibraryService libraryService = new LibraryService(bookDAO, memberDAO, transactionDAO);
        TransactionService transactionService = new TransactionService(transactionDAO);

        OverdueNotificationService notifier = new OverdueNotificationService(transactionService, 60_000);
        Thread notifierThread = new Thread(notifier, "OverdueNotifier");
        notifierThread.setDaemon(true);
        notifierThread.start();

        try (Scanner scanner = new Scanner(System.in)) {
            boolean exit = false;
            while (!exit) {
                printMenu();
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> addBookFlow(scanner, libraryService);
                        case "2" -> addMemberFlow(scanner, libraryService);
                        case "3" -> issueBookFlow(scanner, libraryService);
                        case "4" -> returnBookFlow(scanner, libraryService, transactionService);
                        case "5" -> listBooks(libraryService);
                        case "6" -> listMembers(libraryService);
                        case "7" -> generateReports(libraryService, transactionService);
                        case "8" -> exit = true;
                        default -> System.out.println("Invalid choice. Please try again.");
                    }
                } catch (Exception e) {
                    AppLogger.error("Unexpected error: " + e.getMessage());
                }
            }
        } finally {
            notifier.stop();
            dbManager.closeConnection();
            System.out.println("Goodbye!");
        }
    }

    private static void printMenu() {
        System.out.println("\n===== Library Management System =====");
        System.out.println("1. Add Book");
        System.out.println("2. Add Member");
        System.out.println("3. Issue Book");
        System.out.println("4. Return Book");
        System.out.println("5. List All Books");
        System.out.println("6. List All Members");
        System.out.println("7. Generate Reports");
        System.out.println("8. Exit");
        System.out.print("Enter choice: ");
    }

    private static void addBookFlow(Scanner scanner, LibraryService service) {
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Category: ");
        String category = scanner.nextLine();
        System.out.print("Number of copies: ");
        int copies = Integer.parseInt(scanner.nextLine().trim());

        Book book = service.addBook(title, author, isbn, category, copies);
        System.out.println(book != null ? "Book added: " + book : "Failed to add book.");
    }

    private static void addMemberFlow(Scanner scanner, LibraryService service) {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Membership ID: ");
        String membershipId = scanner.nextLine();
        System.out.print("Max books allowed: ");
        int maxBooks = Integer.parseInt(scanner.nextLine().trim());

        Member member = service.addMember(name, email, phone, membershipId, maxBooks);
        System.out.println(member != null ? "Member added: " + member : "Failed to add member.");
    }

    private static void issueBookFlow(Scanner scanner, LibraryService service) {
        System.out.print("Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());

        try {
            Transaction t = service.issueBook(bookId, memberId);
            System.out.println("Book issued successfully: " + t);
        } catch (BookNotAvailableException | MemberLimitExceededException | InvalidMemberException e) {
            System.out.println("Could not issue book: " + e.getMessage());
        }
    }

    private static void returnBookFlow(Scanner scanner, LibraryService service, TransactionService txService) {
        System.out.print("Member ID: ");
        int memberId = Integer.parseInt(scanner.nextLine().trim());
        List<Transaction> active = txService.getActiveTransactionsForMember(memberId);
        if (active.isEmpty()) {
            System.out.println("No active loans found for this member.");
            return;
        }
        System.out.println("Active loans:");
        for (Transaction t : active) {
            System.out.println("  " + t);
        }
        System.out.print("Transaction ID to return: ");
        int txId = Integer.parseInt(scanner.nextLine().trim());

        Transaction target = active.stream()
                .filter(t -> t.getTransactionId() == txId)
                .findFirst()
                .orElse(null);

        if (target == null) {
            System.out.println("Transaction not found among active loans.");
            return;
        }

        try {
            double fine = service.returnBook(target);
            System.out.printf("Book returned. Fine due: %.2f%n", fine);
        } catch (InvalidMemberException e) {
            System.out.println("Could not process return: " + e.getMessage());
        }
    }

    private static void listBooks(LibraryService service) {
        service.refreshCaches();
        service.getBookCache().values().forEach(System.out::println);
    }

    private static void listMembers(LibraryService service) {
        service.refreshCaches();
        service.getMemberCache().values().forEach(System.out::println);
    }

    private static void generateReports(LibraryService service, TransactionService txService) {
        service.refreshCaches();
        ReportGenerator.generateBookReport(service.getBookCache().values(), "book_report.csv");
        ReportGenerator.generateMemberReport(service.getMemberCache().values(), "member_report.csv");
        ReportGenerator.generateTransactionReport(txService.getAllTransactions(), "transaction_report.csv");
        System.out.println("Reports generated: book_report.csv, member_report.csv, transaction_report.csv");
    }
}
