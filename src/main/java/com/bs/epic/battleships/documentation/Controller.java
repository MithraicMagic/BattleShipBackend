package com.bs.epic.battleships.documentation;

public abstract class Controller {
    public Controller() {
        Documentation.get().addController(this.getClass());
    }
}
