package ru.matyasov.app.accounting.dto;

import java.util.List;

public class OperationCategoryDto extends BaseEntityDto {

    private Boolean isActual;

    private String name;

    private List<QuickAddonDto> quickAddonList;

    public OperationCategoryDto() {
    }

    public OperationCategoryDto(int id, Boolean isActual, String name, List<QuickAddonDto> quickAddonList) {
        super(id);
        this.isActual = isActual;
        this.name = name;
        this.quickAddonList = quickAddonList;
    }

    // IsActual
    public Boolean getIsActual() {
        return isActual;
    }

    public void setIsActual(boolean isActual) {
        this.isActual = isActual;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<QuickAddonDto> getQuickAddonList() {
        return quickAddonList;
    }

    public void setQuickAddonList(List<QuickAddonDto> quickAddonList) {
        this.quickAddonList = quickAddonList;
    }
}