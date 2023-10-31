package ru.matyasov.app.accounting.models.references.system;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import ru.matyasov.app.accounting.models.BaseEntity;

import java.util.Date;

@Entity
@Table(name="calendar", indexes = {@Index(name="date_index", columnList = "date")})
public class Calendar extends BaseEntity {

    @NotNull(message = "Дата является обязательной.")
    @Column(name = "date", columnDefinition = "DATE NOT NULL UNIQUE")
    @Temporal(TemporalType.DATE)
    private Date date;

    @Column(name = "month_short_name", columnDefinition = "VARCHAR NOT NULL")
    private String monthShortName;

    @Column(name = "week_number", columnDefinition = "INTEGER NOT NULL")
    private int weekNumber;

    @Column(name = "day_short_name", columnDefinition = "VARCHAR NOT NULL")
    private String dayShortName;

    @Column(name = "day_type", columnDefinition = "VARCHAR NOT NULL")
    private String dayType;

    public Calendar() {
    }

    public Calendar(Date date, String monthShortName, int weekNumber, String dayShortName, String dayType) {
        this.date = date;
        this.monthShortName = monthShortName;
        this.weekNumber = weekNumber;
        this.dayShortName = dayShortName;
        this.dayType = dayType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMonthShortName() {
        return monthShortName;
    }

    public void setMonthShortName(String monthShortName) {
        this.monthShortName = monthShortName;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getDayShortName() {
        return dayShortName;
    }

    public void setDayShortName(String dayShortName) {
        this.dayShortName = dayShortName;
    }

    public String getDayType() {
        return dayType;
    }

    public void setDayType(String dayType) {
        this.dayType = dayType;
    }

    @Override
    public String toString() {
        return "Calendar{" +
                "date=" + date +
                ", monthShortName='" + monthShortName + '\'' +
                ", weekNumber=" + weekNumber +
                ", dayShortName='" + dayShortName + '\'' +
                ", dayType='" + dayType + '\'' +
                '}';
    }
}
