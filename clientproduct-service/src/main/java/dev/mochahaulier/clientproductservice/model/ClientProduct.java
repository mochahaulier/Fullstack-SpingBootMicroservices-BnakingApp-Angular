package dev.mochahaulier.clientproductservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

// import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class ClientProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne
    // @JoinColumn(name = "client_id", nullable = false)
    // @JsonBackReference
    // private Client client;
    private Long clientId;

    // @ManyToOne
    // @JoinColumn(name = "product_key", nullable = false)
    // private Product product;
    private Long productId;

    @Enumerated(EnumType.STRING)
    private ProductType type;

    private LocalDate lastChargeDate;

    // public ProductDefinition getProductDefinition() {
    // return product.getProductDefinition();
    // }

    // public BigDecimal getRate() {
    // return product.getRate();
    // }

    // public boolean isDueForCharge() {
    // ProductDefinition productDefinition = product.getProductDefinition();
    // if (lastChargeDate == null) {
    // // Should probably set this at creation, but should be fine for now
    // lastChargeDate = java.time.LocalDate.now(); // startDate;
    // }
    // long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(lastChargeDate,
    // LocalDate.now());
    // // assuming every month has 30 days
    // // TODO: could use as follows:
    // // long monthsBetween = ChronoUnit.MONTHS.between(lastChargeDate,
    // currentDate);
    // int intervalDays = productDefinition.getPayRateUnit() == PayRateUnit.DAY
    // ? productDefinition.getPayRateValue()
    // : productDefinition.getPayRateValue() * 30;

    // return daysBetween >= intervalDays;
    // }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        ClientProduct product = (ClientProduct) o;
        return getId() != null && Objects.equals(getId(), product.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
