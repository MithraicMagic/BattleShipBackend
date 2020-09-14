package com.bs.epic.battleships.documentation;

import java.util.ArrayList;
import java.util.Collection;

public class Fields {
    public Collection<Tuple> fields;

    public Fields(Collection<Tuple> fields) {
        this.fields = fields;
    }

    public Fields(String name) {
        this.fields = new ArrayList<>();
        fields.add(new Tuple("void", "event-only", name));
    }

    public Fields() {
        this.fields = new ArrayList<>();
    }

    public void add(Tuple t) { fields.add(t); }
}
