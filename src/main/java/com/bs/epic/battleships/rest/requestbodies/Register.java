package com.bs.epic.battleships.rest.requestbodies;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Register {
    @Doc("The user's username")
    public String username;
    @Doc("The user's email address")
    public String email;
    @Doc("The user's password")
    public String password;

    public Register() {}

    public Register(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
