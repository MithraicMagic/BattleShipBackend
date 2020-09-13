package com.bs.epic.battleships.documentation;

public class Entry {
    public String path;
    public Input input;
    public Tuple output;

    public Entry(String path) {
        this.path = path;
        this.input = new Input();
    }
}
