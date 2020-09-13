package com.bs.epic.battleships.documentation;

public class Entry {
    public String path;
    public Fields input;
    public Fields output;
    public Fields onError;

    public Entry(String path) {
        this.path = path;
        this.input = null;
        this.output = null;
        this.onError = null;
    }
}
