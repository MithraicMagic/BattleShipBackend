package com.bs.epic.battleships.documentation;

import java.util.ArrayList;
import java.util.Collection;

public class RestOutput {
    public int responseCode;
    public String description;
    public Collection<Tuple> fields;

    public RestOutput(int responseCode) {
        this.responseCode = responseCode;
        this.fields = new ArrayList<>();

        if (responseCode == 200) description = "Successfully returned";
    }

    public RestOutput(int responseCode, String description, Collection<Tuple> fields) {
        this.responseCode = responseCode;
        this.description = description;
        this.fields = fields;
    }
}
