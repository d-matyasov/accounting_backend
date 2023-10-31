package ru.matyasov.app.accounting.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AccountingObjectDto extends BaseReferenceDto {

    private String nameShort;

    private Integer ordinalNumber;

    @NotNull(message = "Дата начала является обязательной.")
    @Temporal(TemporalType.DATE)
    private LocalDate openDate;

    @Temporal(TemporalType.DATE)
    private LocalDate closeDate;

    @NotNull(message = "Начальная сумма является обязательной.")
    private long startAmount;

    @NotNull(message = "Текущая сумма является обязательной.")
    private long currentAmount;

    @NotNull(message = "Валюта является обязательной.")
    private int currencyId;

    @NotNull(message = "Тип объекта учёта является обязательным.")
    private int typeId;

    public AccountingObjectDto() {
    }

    public AccountingObjectDto(int id, Boolean isActual, String code, String name, String comment, String nameShort, Integer ordinalNumber, LocalDate openDate, LocalDate closeDate, long startAmount, long currentAmount, int currencyId, int typeId) {
        super(id, isActual, code, name, comment);
        this.nameShort = nameShort;
        this.ordinalNumber = ordinalNumber;
        this.openDate = openDate;
        this.openDate = closeDate;
        this.startAmount = startAmount;
        this.currentAmount = currentAmount;
        this.currencyId = currencyId;
        this.typeId = typeId;
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

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
