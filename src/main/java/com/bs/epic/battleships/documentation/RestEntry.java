package com.bs.epic.battleships.documentation;

import java.util.ArrayList;
import java.util.Collection;

public class RestEntry {
    public String httpVerb;
    public String path;
    public Fields pathVariables;
    public Fields body;

    public RestOutput output;
    public Collection<RestOutput> onError;

    public RestEntry() {
        pathVariables = new Fields();
        body = new Fields();
        onError = new ArrayList<>();
    }
}
