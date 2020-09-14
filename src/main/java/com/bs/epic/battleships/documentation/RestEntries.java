package com.bs.epic.battleships.documentation;

import java.util.ArrayList;
import java.util.Collection;

public class RestEntries {
    public String controller;
    public String basePath;

    public Collection<RestEntry> entries;

    public RestEntries(String controllerName) {
        this.controller = controllerName;
        this.entries = new ArrayList<>();
    }
}
