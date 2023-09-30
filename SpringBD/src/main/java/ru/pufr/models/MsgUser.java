package ru.pufr.models;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class MsgUser {
    private String name;
    @Enumerated(value = EnumType.STRING)
    private String type;
    private String message;


    public MsgUser(String name, String type, String message) {
        this.name = name;
        this.type = type;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MsgUser{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", message='" + message + '\'' +
                '}';
    }
}

