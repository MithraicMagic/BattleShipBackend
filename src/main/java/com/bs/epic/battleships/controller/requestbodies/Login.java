package com.bs.epic.battleships.controller.requestbodies;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Login {
    @Doc("The user's email address")
    public String email;
    @Doc("The user's password")
    public String password;

    public Login() {}
}
