package ru.bank.transfer;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class BankAccount {
    private final UUID id;
    private BigDecimal balance;

    BankAccount(BigDecimal balance) {
        id = UUID.randomUUID();
        this.balance = balance;
    }

    synchronized void deposit(BigDecimal depositAmount) {
        balance = balance.add(depositAmount);
    }

    synchronized void withdraw(BigDecimal debitAmount) {
        balance = balance.subtract(debitAmount);
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
