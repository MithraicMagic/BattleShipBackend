package com.bs.epic.battleships.documentation;

import java.util.ArrayList;
import java.util.Collection;

public class RestOutputs {
    public Collection<RestOutput> restOutputs;

    public RestOutputs() {
        restOutputs = new ArrayList<>();
    }

    public void add(RestOutput r) {
        restOutputs.add(r);
    }
}
