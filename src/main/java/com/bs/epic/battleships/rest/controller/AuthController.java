package com.bs.epic.battleships.rest.controller;

import com.bs.epic.battleships.documentation.Controller;
import com.bs.epic.battleships.documentation.annotations.OnError;
import com.bs.epic.battleships.rest.requestbodies.Login;
import com.bs.epic.battleships.rest.requestbodies.Register;
import com.bs.epic.battleships.rest.responses.LoginSuccess;
import com.bs.epic.battleships.documentation.annotations.Returns;
import com.bs.epic.battleships.rest.responses.Response;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.verification.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController extends Controller {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        super();
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Returns(LoginSuccess.class)
    @OnError(code = 422, value = String.class, desc = "Invalid input")
    @OnError(code = 403, value = String.class, desc = "Email or password is incorrect")
    public ResponseEntity<?> login(@RequestBody Login login) {
        var result= authService.login(login);
        if (result.success) {
            var jwt = jwtUtil.generateToken(result.user);
            return ResponseEntity.ok(new LoginSuccess(jwt));
        }
        return ResponseEntity.status(result.code).body(result.message);
    }

    @PostMapping("/register")
    @Returns(String.class)
    @OnError(code = 422, value = String.class, desc = "Invalid or already existing account")
    public ResponseEntity<?> register(@RequestBody Register register) {
        var result = authService.register(register);
        if (result.success) {
            return ResponseEntity.ok("Successfully registered your account");
        }
        return ResponseEntity.status(result.code).body(result.message);
    }
}
