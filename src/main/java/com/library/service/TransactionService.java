package com.library.service;

import com.library.dao.TransactionDAO;
import com.library.model.Transaction;

import java.util.List;

public class TransactionService {

    private final TransactionDAO transactionDAO;

    public TransactionService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    public List<Transaction> getOverdueTransactions() {
        return transactionDAO.getOverdueTransactions();
    }

    public List<Transaction> getActiveTransactionsForMember(int memberId) {
        return transactionDAO.getActiveTransactionsForMember(memberId);
    }
}
