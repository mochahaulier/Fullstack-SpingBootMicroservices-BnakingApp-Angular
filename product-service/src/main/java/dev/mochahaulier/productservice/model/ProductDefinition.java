package dev.mochahaulier.productservice.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_definition", indexes = {
        @Index(name = "idx_product_key", columnList = "product_key")
})
public class ProductDefinition {
    @Id
    private String productKey;

    private String description;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    private BigDecimal rate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "unit", column = @Column(name = "pay_rate_unit")),
            @AttributeOverride(name = "value", column = @Column(name = "pay_rate_value"))
    })
    @Column(precision = 38, scale = 4)
    private PayRate payRate;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdDate;
    @UpdateTimestamp
    private Instant modifiedDate;

    public PayRateUnit getPayRateUnit() {
        return this.payRate.getUnit();
    }

    public int getPayRateValue() {
        return this.payRate.getValue();
    }

    public RateType getRateType() {
        // If rate bigger than 1 set as fixed, otherwise use percentage
        // Would be better if request had ratetype in it
        return this.rate.compareTo(BigDecimal.ONE) == 1 ? RateType.FIXED : RateType.PERCENTAGE;
    }

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
        ProductDefinition definition = (ProductDefinition) o;
        return getProductKey() != null && Objects.equals(getProductKey(), definition.getProductKey());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
