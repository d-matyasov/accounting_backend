package ru.matyasov.app.accounting.dto;

import jakarta.validation.constraints.NotEmpty;

public abstract class BaseReferenceDto extends BaseEntityDto {

    protected Boolean isActual;

    protected String code;

    @NotEmpty(message = "Атрибут name обязательный")
    protected String name;

    protected String comment;

    public BaseReferenceDto() {
    }

    public BaseReferenceDto(int id, Boolean isActual, String code, String name, String comment) {
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
