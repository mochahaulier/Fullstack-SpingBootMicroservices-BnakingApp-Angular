package dev.mochahaulier.clientproductservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "account_product")
@DiscriminatorValue("ACCOUNT")
public class AccountProduct extends ClientProduct {

    @Column(name = "account_balance")
    private BigDecimal accountBalance = BigDecimal.ZERO;

    private LocalDate startDate;

    public BigDecimal getAccountBalance() {
        return this.accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
