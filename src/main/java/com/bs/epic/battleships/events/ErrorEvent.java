package com.bs.epic.battleships.events;

public class ErrorEvent {
    public String event;
    public String reason;

    public ErrorEvent(String event, String reason) {
        this.event = event;
        this.reason = reason;
    }
}
