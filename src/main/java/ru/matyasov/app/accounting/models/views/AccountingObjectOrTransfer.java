package ru.matyasov.app.accounting.models.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "accounting_objects_and_transfers_view")
@Immutable
public class AccountingObjectOrTransfer {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "is_actual")
    private Boolean isActual;

    @Column(name = "accounting_object_id")
    private int accountingObjectId;

    @Column(name = "pair_accounting_object_id")
    private Integer pairAccountingObjectId;

    @Column(name = "name")
    private String name;

    public AccountingObjectOrTransfer() {
    }

    public AccountingObjectOrTransfer(String id, Boolean isActual, int accountingObjectId, Integer pairAccountingObjectId, String name) {
        this.id = id;
        this.isActual = isActual;
        this.accountingObjectId = accountingObjectId;
        this.pairAccountingObjectId = pairAccountingObjectId;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsActual() {
        return isActual;
    }

    public void setIsActual(Boolean isActual) {
        this.isActual = isActual;
    }

    public int getAccountingObjectId() {
        return accountingObjectId;
    }

    public void setAccountingObjectId(int accountingObjectId) {
        this.accountingObjectId = accountingObjectId;
    }

    public Integer getPairAccountingObjectId() {
        return pairAccountingObjectId;
    }

    public void setPairAccountingObjectId(Integer pairAccountingObjectId) {
        this.pairAccountingObjectId = pairAccountingObjectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
