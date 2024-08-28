package dev.mochahaulier.clientproductservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "loan_product")
@DiscriminatorValue("LOAN")
public class LoanProduct extends ClientProduct {

    @Column(name = "loan_amount")
    private BigDecimal originalAmount = BigDecimal.ZERO;

    @Column(name = "loan_installment")
    private BigDecimal fixedInstallment = BigDecimal.ZERO;

    private LocalDate startDate;
    private LocalDate endDate;

    // public int calculateNumberOfDueDates() {
    // // Check for null values
    // if (endDate == null || startDate == null) {
    // return 0;
    // }

    // // Calculate the number of days between the start and end dates
    // long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate,
    // endDate);

    // // Get the pay rate unit and value from the product definition
    // ProductDefinition productDefinition =
    // this.getProduct().getProductDefinition();
    // // Assuming that each month has 30 days...
    // int intervalDays = productDefinition.getPayRateUnit() == PayRateUnit.DAY ?
    // productDefinition.getPayRateValue()
    // : productDefinition.getPayRateValue() * 30;

    // // Calculate and return the number of due dates
    // return (int) (daysBetween / intervalDays);
    // }

    public BigDecimal getOriginalAmount() {
        return this.originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getFixedInstallment() {
        return this.fixedInstallment;
    }

    public void setFixedInstallment(BigDecimal fixedInstallment) {
        this.fixedInstallment = fixedInstallment;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
