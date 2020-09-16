package com.bs.epic.battleships.controller.requestbodies;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Register {
    @Doc("The user's username")
    public String username;
    @Doc("The user's email address")
    public String email;
    @Doc("The user's password")
    public String password;

    public Register() {}
}
