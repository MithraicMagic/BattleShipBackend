package com.bs.epic.battleships.controller.responses;

import com.bs.epic.battleships.documentation.Doc;
import com.bs.epic.battleships.documentation.RestEntries;

import java.util.Collection;

public class RestApi {
    @Doc(description = "The entries in the REST API")
    public Collection<RestEntries> entries;

    public RestApi(Collection<RestEntries> entries) {
        this.entries = entries;
    }
}
