package ru.matyasov.app.accounting.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.query.Procedure;
import ru.matyasov.app.accounting.models.references.user.AccountingObject;
import ru.matyasov.app.accounting.models.references.user.OperationCategory;
import ru.matyasov.app.accounting.models.views.AccountingObjectOrTransfer;

import java.time.LocalDate;
import java.util.Date;

public class OperationDto extends BaseEntityDto {

    @NotNull(message = "Дата является обязательной.")
    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull(message = "Индекс по дате является обязательным.")
    private int indexByDate;

    private Long amountPlus;

    private Long amountMinus;

    private Boolean isFact;

    private Integer categoryId;

    private String categoryAddon;

    @NotNull(message = "Объект учёта является обязательным.")
    private String accountingObjectOrTransferId;

    private Integer pairOperationId;

    private String comment;

    public OperationDto() {
    }

    public OperationDto(int id, Date date, int indexByDate, Long amountPlus, Long amountMinus, Boolean isFact, Integer categoryId, String categoryAddon, String accountingObjectOrTransferId, Integer pairOperationId, String comment) {
        super(id);
        this.date = date;
        this.indexByDate = indexByDate;
        this.amountPlus = amountPlus;
        this.amountMinus = amountMinus;
        this.isFact = isFact;
        this.categoryId = categoryId;
        this.categoryAddon = categoryAddon;
        this.accountingObjectOrTransferId = accountingObjectOrTransferId;
        this.pairOperationId = pairOperationId;
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIndexByDate() {
        return indexByDate;
    }

    public void setIndexByDate(int indexByDate) {
        this.indexByDate = indexByDate;
    }

    public Long getAmountPlus() {
        return amountPlus;
    }

    public void setAmountPlus(Long amountPlus) {
        this.amountPlus = amountPlus;
    }

    public Long getAmountMinus() {
        return amountMinus;
    }

    public void setAmountMinus(Long amountMinus) {
        this.amountMinus = amountMinus;
    }

    public Boolean getIsFact() {
        return isFact;
    }

    public void setIsFact(Boolean isFact) {
        this.isFact = isFact;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryAddon() {
        return categoryAddon;
    }

    public void setCategoryAddon(String categoryAddon) {
        this.categoryAddon = categoryAddon;
    }

    public String getAccountingObjectOrTransferId() {
        return accountingObjectOrTransferId;
    }

    public void setAccountingObjectOrTransferId(String accountingObjectOrTransferId) {
        this.accountingObjectOrTransferId = accountingObjectOrTransferId;
    }

    public Integer getPairOperationId() {
        return pairOperationId;
    }

    public void setPairOperationId(Integer pairOperationId) {
        this.pairOperationId = pairOperationId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
