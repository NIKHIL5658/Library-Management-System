package com.library.thread;

import com.library.model.Transaction;
import com.library.service.TransactionService;
import com.library.util.AppLogger;

import java.util.List;

public class OverdueNotificationService implements Runnable {

    private final TransactionService transactionService;
    private final long intervalMillis;
    private volatile boolean running = true;

    public OverdueNotificationService(TransactionService transactionService, long intervalMillis) {
        this.transactionService = transactionService;
        this.intervalMillis = intervalMillis;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        AppLogger.info("Overdue notification service started.");
        while (running) {
            scanAndNotify();
            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        AppLogger.info("Overdue notification service stopped.");
    }

    private synchronized void scanAndNotify() {
        List<Transaction> overdue = transactionService.getOverdueTransactions();
        if (overdue.isEmpty()) {
            AppLogger.info("Overdue scan complete: no overdue books.");
            return;
        }
        for (Transaction t : overdue) {
            AppLogger.warn(String.format(
                    "REMINDER: Transaction #%d (Member %d, Book %d) is overdue since %s.",
                    t.getTransactionId(), t.getMemberId(), t.getBookId(), t.getDueDate()));
        }
    }
}
