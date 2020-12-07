package com.bs.epic.battleships.events;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Command {
    @Doc("The name of the command")
    public String commandName;
    @Doc("The name of the person that initiated the command")
    public String sender;
    @Doc("The parameters for the command")
    public String[] params;

    public Command() {}

    public Command(String commandName, String sender, String... params) {
        this.commandName = commandName;
        this.sender = sender;
        this.params = params;
    }
}
