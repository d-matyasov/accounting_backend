package ru.matyasov.app.accounting.models.references;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotEmpty;
import ru.matyasov.app.accounting.models.BaseEntity;


@MappedSuperclass
public abstract class BaseReference extends BaseEntity {

    @Column(name = "is_actual", columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    protected Boolean isActual;

    @Column(name = "code", columnDefinition = "VARCHAR UNIQUE")
    protected String code;

    @NotEmpty(message = "Атрибут name обязательный")
    @Column(name = "name", columnDefinition = "VARCHAR NOT NULL UNIQUE")
    protected String name;

    @Column(name = "comment")
    protected String comment;

    public BaseReference() {
    }

    public BaseReference(int id, Boolean isActual, String code, String name, String comment) {
        super(id);
        this.isActual = isActual;
        this.code = code;
        this.name = name;
        this.comment = comment;
    }

    public Boolean getIsActual() {
        return isActual;
    }

    public void setIsActual(Boolean isActual) {
        this.isActual = isActual;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
