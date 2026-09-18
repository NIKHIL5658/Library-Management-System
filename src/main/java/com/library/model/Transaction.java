package com.library.model;

import java.time.LocalDate;

public class Transaction {

    private int transactionId;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;
    private TransactionStatus status;

    public Transaction(int transactionId, int bookId, int memberId,
                        LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fineAmount = 0.0;
        this.status = TransactionStatus.ISSUED;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public boolean isOverdue(LocalDate referenceDate) {
        return status == TransactionStatus.ISSUED && referenceDate.isAfter(dueDate);
    }

    @Override
    public String toString() {
        return String.format(
                "Transaction[ID=%d, BookID=%d, MemberID=%d, Issued=%s, Due=%s, Returned=%s, Fine=%.2f, Status=%s]",
                transactionId, bookId, memberId, issueDate, dueDate,
                returnDate == null ? "—" : returnDate.toString(), fineAmount, status);
    }
}
