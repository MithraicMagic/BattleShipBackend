package com.bs.epic.battleships.rest.responses;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.documentation.RestEntries;

import java.util.Collection;

public class RestApi {
    @Doc("The entries in the REST API")
    public Collection<RestEntries> entries;

    public RestApi(Collection<RestEntries> entries) {
        this.entries = entries;
    }
}
