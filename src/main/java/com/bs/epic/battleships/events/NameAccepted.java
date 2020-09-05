package com.bs.epic.battleships.events;

public class NameAccepted {
    public String code;
    public String uid;
    public String name;

    public NameAccepted(String code, String uid, String name) {
        this.code = code;
        this.uid = uid;
        this.name = name;
    }
}
