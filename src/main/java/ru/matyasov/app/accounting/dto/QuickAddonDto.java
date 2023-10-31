package ru.matyasov.app.accounting.dto;

public class QuickAddonDto extends BaseEntityDto {

    private Boolean isActual;

    private String name;

    public QuickAddonDto() {
    }

    public QuickAddonDto(int id, Boolean isActual, String name) {
        super(id);
        this.isActual = isActual;
        this.name = name;
    }

    // IsActual
    public Boolean getIsActual() {
        return isActual;
    }

    public void setIsActual(Boolean isActual) {
        this.isActual = isActual;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
