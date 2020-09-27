package com.bs.epic.battleships.rest.requestbodies;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class Login {
    @Doc("The user's email address")
    public String email;
    @Doc("The user's password")
    public String password;

    public Login() {}

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
