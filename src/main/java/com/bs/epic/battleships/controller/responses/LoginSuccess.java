package com.bs.epic.battleships.controller.responses;

import com.bs.epic.battleships.documentation.annotations.Doc;

public class LoginSuccess {
    @Doc("The JSON Web Token that was generated for the login attempt")
    public String jwt;

    public LoginSuccess(String jwt) {
        this.jwt = jwt;
    }
}
