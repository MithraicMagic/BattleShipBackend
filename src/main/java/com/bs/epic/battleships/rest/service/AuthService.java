package com.bs.epic.battleships.rest.service;

import com.bs.epic.battleships.rest.repository.dto.User;
import com.bs.epic.battleships.rest.requestbodies.Login;
import com.bs.epic.battleships.rest.repository.AuthRepository;
import com.bs.epic.battleships.rest.requestbodies.Register;
import com.bs.epic.battleships.verification.AuthValidator;
import com.bs.epic.battleships.verification.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final AuthValidator authValidator;
    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(AuthValidator authValidator, AuthRepository authRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authValidator = authValidator;
        this.authRepository = authRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Result login(Login login) {
        var verifyLogin = authValidator.isValidLogin(login);
        if (!verifyLogin.success) return verifyLogin;

        var oUser = authRepository.findByEmail(login.email);
        if (oUser.isPresent()) {
            var user = oUser.get();
            if (bCryptPasswordEncoder.matches(login.password, user.getPassword())) {
                return Result.success(user);
            }
        }

        return Result.error(403, "Email or password is incorrect");
    }

    public Result register(Register register) {
        var verifyRegister = authValidator.isValidRegister(register);
        if (!verifyRegister.success) return verifyRegister;

        var oUser = authRepository.findByEmail(register.email);
        if (oUser.isPresent()) return Result.error(422, "This email address is already in use");

        oUser = authRepository.findByUsername(register.username);
        if (oUser.isPresent()) return Result.error(422, "This username is already in use");

        var encodedPassword = bCryptPasswordEncoder.encode(register.password);
        var user = new User(register.username, register.email, encodedPassword);

        authRepository.save(user);
        return Result.success();
    }

    public Optional<User> getByUsername(String username) {
        return authRepository.findByUsername(username);
    }

    public Optional<User> getUser() {
        var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.of((User) principal);
    }

    public void saveUser(User u) {
        authRepository.save(u);
    }
}
