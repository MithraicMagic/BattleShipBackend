package com.bs.epic.battleships.controller.responses;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.documentation.SocketEntry;

import java.util.Collection;

public class SocketApi {
    @Doc("The entries in the WebSocket API")
    public Collection<SocketEntry> entries;

    public SocketApi(Collection<SocketEntry> entries) {
        this.entries = entries;
    }
}
