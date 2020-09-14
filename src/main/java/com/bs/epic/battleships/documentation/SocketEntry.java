package com.bs.epic.battleships.documentation;

public class SocketEntry {
    public String path;
    public Fields input;
    public Fields output;
    public Fields onError;

    public SocketEntry(String path) {
        this.path = path;
        this.input = null;
        this.output = null;
        this.onError = null;
    }
}
