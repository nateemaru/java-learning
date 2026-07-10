package ru.bank.transfer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConcurrentBank {
    private final List<BankAccount> accounts;
    private final Object accountsMonitor = new Object();
    private volatile BigDecimal totalBalance;

    public ConcurrentBank() {
        this.accounts = new ArrayList<>();
        this.totalBalance = BigDecimal.ZERO;
    }

    public BankAccount createAccount(BigDecimal initialBalance) {
        Objects.requireNonNull(initialBalance, "ConcurrentBank: Initial balance must not be null");

        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("ConcurrentBank: Initial balance must not be negative");
        }

        BankAccount newAccount = new BankAccount(initialBalance);
        synchronized (accountsMonitor) {
            accounts.add(newAccount);
            totalBalance = totalBalance.add(initialBalance);
        }
        return newAccount;
    }

    public void transfer(BankAccount from, BankAccount to, BigDecimal transferAmount) {
        Objects.requireNonNull(from, "ConcurrentBank: 'from' must not be null");
        Objects.requireNonNull(to, "ConcurrentBank: 'to' must not be null");
        Objects.requireNonNull(transferAmount, "ConcurrentBank: 'transferAmount' must not be null");

        if (from.equals(to)) {
            throw new IllegalArgumentException("ConcurrentBank: can't transfer to the same account.");
        }

        BankAccount first;
        BankAccount second;
        if (from.getId().compareTo(to.getId()) < 0) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {
                if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("ConcurrentBank: 'transferAmount' must be greater than 0");
                }
                if (from.getBalance().compareTo(transferAmount) < 0) {
                    throw new IllegalStateException("Insufficient funds");
                }

                from.withdraw(transferAmount);
                to.deposit(transferAmount);
            }
        }
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }
}
