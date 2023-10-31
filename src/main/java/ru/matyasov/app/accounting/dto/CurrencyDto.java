package ru.matyasov.app.accounting.dto;

import jakarta.persistence.Column;

public class CurrencyDto extends BaseReferenceDto {

    @Column(name = "ordinal_number")
    private Number ordinalNumber;

    private String grapheme;

    public CurrencyDto() {
    }

    public CurrencyDto(int id, Boolean isActual, String code, String name, String comment, Number ordinalNumber, String grapheme) {
        super(id, isActual, code, name, comment);
        this.ordinalNumber = ordinalNumber;
        this.grapheme = grapheme;
    }

    public Number getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(Number ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    public String getGrapheme() {
        return grapheme;
    }

    public void setGrapheme(String grapheme) {
        this.grapheme = grapheme;
    }
}
