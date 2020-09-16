package com.bs.epic.battleships.controller;

import com.bs.epic.battleships.controller.requestbodies.Login;
import com.bs.epic.battleships.controller.requestbodies.Register;
import com.bs.epic.battleships.controller.responses.LoginSuccess;
import com.bs.epic.battleships.documentation.Documentation;
import com.bs.epic.battleships.documentation.annotations.Returns;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    public AuthController() {
        Documentation.get().addController(this.getClass());
    }

    @PostMapping("/login")
    @Returns(LoginSuccess.class)
    public ResponseEntity<?> login(@RequestBody Login login) {
        return ResponseEntity.ok(new LoginSuccess("rens"));
    }

    @PostMapping("/register")
    @Returns(String.class)
    public ResponseEntity<?> register(@RequestBody Register register) {
        return ResponseEntity.ok("Successfully registered your account");
    }
}
