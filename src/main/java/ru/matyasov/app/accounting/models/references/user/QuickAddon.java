package ru.matyasov.app.accounting.models.references.user;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import ru.matyasov.app.accounting.models.references.BaseReference;

import java.util.List;

@Entity
@Table(name = "quick_addon")
public class QuickAddon extends BaseReference {

    @ManyToMany(mappedBy = "quickAddonList")
    private List<OperationCategory> operationCategoryList;
    public QuickAddon() {
    }

    public QuickAddon(int id, Boolean isActual, String code, String name, String comment, List<OperationCategory> operationCategoryList) {
        super(id, isActual, code, name, comment);
        this.operationCategoryList = operationCategoryList;
    }

    @Override
    public String toString() {
        return "QuickAddon {\n{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
