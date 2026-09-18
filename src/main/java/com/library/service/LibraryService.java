package com.library.service;

import com.library.dao.BookDAO;
import com.library.dao.MemberDAO;
import com.library.dao.TransactionDAO;
import com.library.exceptions.BookNotAvailableException;
import com.library.exceptions.InvalidMemberException;
import com.library.exceptions.MemberLimitExceededException;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.model.TransactionStatus;
import com.library.util.AppLogger;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryService {

    private static final int LOAN_PERIOD_DAYS = 14;

    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final TransactionDAO transactionDAO;

    private final Map<Integer, Book> bookCache = new HashMap<>();
    private final Map<Integer, Member> memberCache = new HashMap<>();

    public LibraryService(BookDAO bookDAO, MemberDAO memberDAO, TransactionDAO transactionDAO) {
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
        this.transactionDAO = transactionDAO;
        refreshCaches();
    }

    public final void refreshCaches() {
        bookCache.clear();
        for (Book b : bookDAO.getAllBooks()) {
            bookCache.put(b.getBookId(), b);
        }
        memberCache.clear();
        for (Member m : memberDAO.getAllMembers()) {
            memberCache.put(m.getId(), m);
        }
    }

    public Book addBook(String title, String author, String isbn, String category, int copies) {
        Book book = new Book(0, title, author, isbn, category, copies);
        int id = bookDAO.addBook(book);
        if (id != -1) {
            Book saved = new Book(id, title, author, isbn, category, copies);
            bookCache.put(id, saved);
            AppLogger.info("Book added: " + saved);
            return saved;
        }
        return null;
    }

    public Member addMember(String name, String email, String phone, String membershipId, int maxBooks) {
        Member member = new Member(0, name, email, phone, membershipId, maxBooks);
        int id = memberDAO.addMember(member);
        if (id != -1) {
            Member saved = new Member(id, name, email, phone, membershipId, maxBooks);
            memberCache.put(id, saved);
            AppLogger.info("Member added: " + saved);
            return saved;
        }
        return null;
    }

    public List<Book> searchBooks(String keyword) {
        return bookDAO.searchByTitleOrAuthor(keyword);
    }

    public Transaction issueBook(int bookId, int memberId)
            throws BookNotAvailableException, MemberLimitExceededException, InvalidMemberException {

        Book book = bookCache.get(bookId);
        if (book == null) {
            book = bookDAO.getBookById(bookId);
        }
        if (book == null) {
            throw new InvalidMemberException("Book with ID " + bookId + " does not exist.");
        }
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(
                    "'" + book.getTitle() + "' currently has no available copies.");
        }

        Member member = memberCache.get(memberId);
        if (member == null) {
            member = memberDAO.getMemberById(memberId);
        }
        if (member == null) {
            throw new InvalidMemberException("Member with ID " + memberId + " does not exist.");
        }
        if (!member.canBorrowMore()) {
            throw new MemberLimitExceededException(
                    member.getName() + " has reached the maximum borrowing limit of " +
                            member.getMaxBooksAllowed() + " books.");
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);
        Transaction transaction = new Transaction(0, bookId, memberId, issueDate, dueDate);
        int txId = transactionDAO.addTransaction(transaction);

        book.decrementAvailableCopies();
        bookDAO.updateBook(book);
        member.incrementBorrowedCount();
        memberDAO.updateMember(member);

        bookCache.put(bookId, book);
        memberCache.put(memberId, member);

        AppLogger.info(String.format("Issued book '%s' to member '%s', due %s",
                book.getTitle(), member.getName(), dueDate));

        Transaction saved = new Transaction(txId, bookId, memberId, issueDate, dueDate);
        return saved;
    }

    public double returnBook(Transaction transaction) throws InvalidMemberException {
        Book book = bookCache.get(transaction.getBookId());
        Member member = memberCache.get(transaction.getMemberId());
        if (book == null || member == null) {
            throw new InvalidMemberException("Cannot process return: book or member not found.");
        }

        LocalDate returnDate = LocalDate.now();
        double fine = FineCalculator.calculateFine(transaction.getDueDate(), returnDate);

        transaction.setReturnDate(returnDate);
        transaction.setFineAmount(fine);
        transaction.setStatus(TransactionStatus.RETURNED);
        transactionDAO.updateTransaction(transaction);

        book.incrementAvailableCopies();
        bookDAO.updateBook(book);
        member.decrementBorrowedCount();
        memberDAO.updateMember(member);

        AppLogger.info(String.format("Returned book '%s' from member '%s'. Fine: %.2f",
                book.getTitle(), member.getName(), fine));

        return fine;
    }

    public Map<Integer, Book> getBookCache() {
        return bookCache;
    }

    public Map<Integer, Member> getMemberCache() {
        return memberCache;
    }
}
