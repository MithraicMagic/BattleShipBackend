package com.bs.epic.battleships.events;

public class Reconnect {
    public String me;
    public String code;

    public Reconnect(String me, String code) {
        this.me = me;
        this.code = code;
    }
}
