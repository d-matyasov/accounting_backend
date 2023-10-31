package ru.matyasov.app.accounting.models.references.system;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ru.matyasov.app.accounting.models.references.BaseReference;

@Entity
@Table(name = "accounting_object_type")
public class AccountingObjectType extends BaseReference {

    @Column(name = "name_short", columnDefinition = "VARCHAR UNIQUE")
    private String nameShort;

    @Column(name = "is_credit", columnDefinition = "BOOLEAN NOT NULL DEFAULT FALSE")
    private Boolean isCredit;

    public AccountingObjectType() {
    }

    public AccountingObjectType(int id, Boolean isActual, String code, String name, String comment, String nameShort, Boolean isCredit) {
        super(id, isActual, code, name, comment);
        this.nameShort = nameShort;
        this.isCredit = isCredit;
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public Boolean getIsCredit() {
        return isCredit;
    }

    public void setIsCredit(Boolean isCredit) {
        this.isCredit = isCredit;
    }

    @Override
    public String toString() {
        return "AccountingObjectType{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", nameShort='" + nameShort + '\'' +
                '}';
    }
}
