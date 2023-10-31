package ru.matyasov.app.accounting.dto;

public abstract class BaseEntityDto {

    protected Integer id;

    public BaseEntityDto() {
    }

    public BaseEntityDto(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
