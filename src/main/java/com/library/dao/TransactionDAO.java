package com.library.dao;

import com.library.model.Transaction;
import java.util.List;

public interface TransactionDAO {

    int addTransaction(Transaction transaction);

    boolean updateTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    List<Transaction> getActiveTransactionsForMember(int memberId);

    List<Transaction> getOverdueTransactions();
}
