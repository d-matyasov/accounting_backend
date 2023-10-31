package ru.matyasov.app.accounting.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ru.matyasov.app.accounting.models.references.user.AccountingObject;
import ru.matyasov.app.accounting.models.references.user.OperationCategory;

import java.util.Date;

@Entity
@Table(name = "operation", uniqueConstraints = { @UniqueConstraint(name = "uk_date_and_index_by_date", columnNames = { "date", "index_by_date" }) })
public class Operation extends BaseEntity implements Cloneable {

    @NotNull(message = "Дата является обязательной.")
    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull(message = "Индекс по дате является обязательным.")
    @Column(name = "index_by_date")
    private int indexByDate;

    @Column(name = "amount_plus")
    private Long amountPlus;

    @Column(name = "amount_minus")
    private Long amountMinus;

    @Column(name = "is_fact")
    private Boolean isFact;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_category_id"))
    private OperationCategory category;

    @Column(name = "category_addon")
    private String categoryAddon;

    @NotNull(message = "Объект учёта является обязательным.")
    @ManyToOne
    @JoinColumn(name = "accounting_object_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_accounting_object_id"), columnDefinition = "INTEGER NOT NULL")
    private AccountingObject accountingObject;

    @Column(name = "accounting_object_amount")
    private Long accountingObjectAmount;

    @OneToOne
    @JoinColumn(name = "pair_operation_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_pair_operation_id"))
    private Operation pairOperation;

    @Column(name = "comment")
    private String comment;

    public Operation() {
    }

    public Operation(int id, Date date, int indexByDate, Long amountPlus, Long amountMinus, Boolean isFact, OperationCategory category, String categoryAddon, AccountingObject accountingObject, Long accountingObjectAmount, Operation pairOperation, String comment) {
        super(id);
        this.date = date;
        this.indexByDate = indexByDate;
        this.amountPlus = amountPlus;
        this.amountMinus = amountMinus;
        this.isFact = isFact;
        this.category = category;
        this.categoryAddon = categoryAddon;
        this.accountingObject = accountingObject;
        this.accountingObjectAmount = accountingObjectAmount;
        this.pairOperation = pairOperation;
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

    //isFact
    public Boolean getIsFact() {
        return isFact;
    }

    public void setIsFact(Boolean isFact) {
        this.isFact = isFact;
    }

    public OperationCategory getCategory() {
        return category;
    }

    public void setCategory(OperationCategory category) {
        this.category = category;
    }

    public String getCategoryAddon() {
        return categoryAddon;
    }

    public void setCategoryAddon(String categoryAddon) {
        this.categoryAddon = categoryAddon;
    }

    public AccountingObject getAccountingObject() {
        return accountingObject;
    }

    public void setAccountingObject(AccountingObject accountingObject) {
        this.accountingObject = accountingObject;
    }

    public Long getAccountingObjectAmount() {
        return accountingObjectAmount;
    }

    public void setAccountingObjectAmount(Long accountingObjectAmount) {
        this.accountingObjectAmount = accountingObjectAmount;
    }

    public Operation getPairOperation() {
        return pairOperation;
    }

    public void setPairOperation(Operation pairOperation) {
        this.pairOperation = pairOperation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {

        String pairOperationId;

        if (this.pairOperation == null) {
            pairOperationId = null;
        } else {
            pairOperationId = String.valueOf(pairOperation.getId());
        }

        return "Operation{" +
                "id=" + id +
                ", date=" + date +
                ", indexByDate=" + indexByDate +
                ", amountPlus=" + amountPlus +
                ", amountMinus=" + amountMinus +
                ", isFact=" + isFact +
                ", category=" + category +
                ", categoryAddon='" + categoryAddon + '\'' +
                ", accountingObject=" + accountingObject +
                ", pairOperationId='" + pairOperationId + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public Operation clone() throws CloneNotSupportedException {
        return (Operation) super.clone();
    }
}
