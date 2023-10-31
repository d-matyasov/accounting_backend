package ru.matyasov.app.accounting.models.references.user;

import jakarta.persistence.*;
import ru.matyasov.app.accounting.models.references.BaseReference;

import java.util.List;

@Entity
@Table(name = "operation_category")
public class OperationCategory extends BaseReference {

    @ManyToMany
    @JoinTable(
            name = "operation_category_quick_addon",
            joinColumns = @JoinColumn(name = "operation_category_id"),
            inverseJoinColumns = @JoinColumn(name = "quick_addon_id")
    )
    private List<QuickAddon> quickAddonList;

    public OperationCategory() {
    }

    public OperationCategory(int id, Boolean isActual, String code, String name, String comment, List<QuickAddon> quickAddonList) {
        super(id, isActual, code, name, comment);
        this.quickAddonList = quickAddonList;
    }

    public List<QuickAddon> getQuickAddonList() {
        return quickAddonList;
    }

    public void setQuickAddonList(List<QuickAddon> quickAddonList) {
        this.quickAddonList = quickAddonList;
    }

    @Override
    public String toString() {
        return "OperationCategory{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ",  quickAddonList=" + quickAddonList +
                '}';
    }
}
