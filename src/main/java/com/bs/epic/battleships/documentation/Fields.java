package com.bs.epic.battleships.documentation;

import java.util.Collection;

public class Fields {
    public Collection<Tuple> fields;

    public Fields(Collection<Tuple> fields) {
        this.fields = fields;
    }

    public void add(Tuple t) { fields.add(t); }
}
