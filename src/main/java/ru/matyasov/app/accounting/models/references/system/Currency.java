package ru.matyasov.app.accounting.models.references.system;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import ru.matyasov.app.accounting.models.references.BaseReference;

@Entity
@Table(name = "Currency")
public class Currency extends BaseReference {

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    @Column(name = "grapheme")
    private String grapheme;

    public Currency() {
    }

    public Currency(int id, Boolean isActual, String code, String name, String comment, Integer ordinalNumber, String grapheme) {
        super(id, isActual, code, name, comment);
        this.ordinalNumber = ordinalNumber;
        this.grapheme = grapheme;
    }

    public Number getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(Integer ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    public String getGrapheme() {
        return grapheme;
    }

    public void setGrapheme(String grapheme) {
        this.grapheme = grapheme;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", code='" + ordinalNumber + '\'' +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", grapheme='" + grapheme + '\'' +
                '}';

    }
}
