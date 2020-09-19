package com.bs.epic.battleships.rest.controller;

import com.bs.epic.battleships.documentation.Controller;
import com.bs.epic.battleships.documentation.annotations.OnError;
import com.bs.epic.battleships.documentation.annotations.Returns;
import com.bs.epic.battleships.rest.responses.UserResponse;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController extends Controller {
    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    @Returns(User.class)
    @OnError(value = String.class, code = 404)
    public ResponseEntity<?> getMe() {
        var oUser = authService.getUser();
        if (oUser.isPresent()) return ResponseEntity.ok(new UserResponse(oUser.get()));
        return ResponseEntity.status(404).body("User could not be found");
    }
}
