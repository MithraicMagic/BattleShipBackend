package com.bs.epic.battleships.util.result;

import com.bs.epic.battleships.events.ErrorEvent;

public class Error extends Result {
    public Error(String eventName, String error) {
        super(false, new ErrorEvent(eventName, error));
    }
}
