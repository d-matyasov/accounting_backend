package ru.matyasov.app.accounting.models.references.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ru.matyasov.app.accounting.models.references.BaseReference;
import ru.matyasov.app.accounting.models.references.system.AccountingObjectType;
import ru.matyasov.app.accounting.models.references.system.Currency;

import java.time.LocalDate;

@Entity
@Table(name = "accounting_object")
public class AccountingObject extends BaseReference {

    @Column(name = "name_short", columnDefinition = "VARCHAR UNIQUE")
    private String nameShort;

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    @NotNull(message = "Дата начала является обязательной.")
    @Column(name = "open_date")
    @Temporal(TemporalType.DATE)
    private LocalDate openDate;

    @Column(name = "close_date")
    @Temporal(TemporalType.DATE)
    private LocalDate closeDate;

    @NotNull(message = "Начальная сумма является обязательной.")
    @Column(name = "start_amount")
    private long startAmount;

    @NotNull(message = "Текущая сумма является обязательной.")
    @Column(name = "current_amount")
    private long currentAmount;

    @NotNull(message = "Валюта является обязательной.")
    @ManyToOne
    @JoinColumn(name = "currency_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_currency_id"), columnDefinition = "INTEGER NOT NULL")
    private Currency currency;

    @NotNull(message = "Тип объекта учёта является обязательным.")
    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_type_id"), columnDefinition = "INTEGER NOT NULL")
    private AccountingObjectType type;

    public AccountingObject() {
    }

    public AccountingObject(int id, Boolean isActual, String code, String name, String comment, String nameShort, Integer ordinalNumber, LocalDate openDate, LocalDate closeDate, long startAmount, long currentAmount, Currency currency, AccountingObjectType type) {
        super(id, isActual, code, name, comment);
        this.nameShort = nameShort;
        this.ordinalNumber = ordinalNumber;
        this.openDate = openDate;
        this.closeDate = closeDate;
        this.startAmount = startAmount;
        this.currentAmount = currentAmount;
        this.currency = currency;
        this.type = type;
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public Integer getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(Integer ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public long getStartAmount() {
        return startAmount;
    }

    public void setStartAmount(long startAmount) {
        this.startAmount = startAmount;
    }

    public long getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(long currentAmount) {
        this.currentAmount = currentAmount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public AccountingObjectType getType() {
        return type;
    }

    public void setType(AccountingObjectType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AccountingObject{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", nameShort='" + nameShort + '\'' +
                ", openDate=" + openDate +
                ", startAmount=" + startAmount +
                ", currentAmount=" + currentAmount +
                ", currency=" + currency +
                ", type=" + type +
                '}';
    }
}
