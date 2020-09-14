package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.Doc;

public class ErrorEvent {
    @Doc("EventName during which the error occurred")
    public String event;
    @Doc("Description of the error")
    public String reason;

    public ErrorEvent(String event, String reason) {
        this.event = event;
        this.reason = reason;
    }
}
