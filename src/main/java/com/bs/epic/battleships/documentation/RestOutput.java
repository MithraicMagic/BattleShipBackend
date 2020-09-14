package com.bs.epic.battleships.documentation;

import java.util.ArrayList;
import java.util.Collection;

public class RestOutput {
    public int responseCode;
    public Collection<Tuple> fields;

    public RestOutput(int responseCode) {
        this.responseCode = responseCode;
    }

    public RestOutput(int responseCode, Collection<Tuple> fields) {
        this.responseCode = responseCode;
        this.fields = fields;
    }
}
