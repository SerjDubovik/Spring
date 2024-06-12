package ru.pufr.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "bot")
public class BotTable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "pressureLeft")
    private String pressureLeft;

    @Column(name = "pressureRight")
    private String pressureRight;

    @Column(name = "rhythm")
    private String rhythm;

    public BotTable() {
    }

    public BotTable(String name, String date, String time, String pressureLeft, String pressureRight, String rhythm) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.pressureLeft = pressureLeft;
        this.pressureRight = pressureRight;
        this.rhythm = rhythm;
    }
}
