package com.bs.epic.battleships.unit.rest.service;

import com.bs.epic.battleships.rest.repository.AuthRepository;
import com.bs.epic.battleships.rest.repository.dto.User;
import com.bs.epic.battleships.rest.requestbodies.Login;
import com.bs.epic.battleships.rest.requestbodies.Register;
import com.bs.epic.battleships.rest.service.AuthService;
import com.bs.epic.battleships.verification.AuthValidator;
import com.bs.epic.battleships.verification.Result;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;

public class AuthServiceTest {
    AuthValidator authValidator = new AuthValidator();
    AuthRepository authRepository = mock(AuthRepository.class);
    BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    AuthService authService = new AuthService(authValidator, authRepository, bCryptPasswordEncoder);

    @Test
    public void testLoginNotValid() {
        Result res = authService.login(new Login("fouteemail", "wachtwoord"));

        Assert.assertFalse(res.success);
        Assert.assertEquals(422, res.code);
        Assert.assertEquals("Invalid email", res.message);
    }

    @Test
    public void testLoginWrongUsername() {
        Result res = authService.login(new Login("test@gmail.com", "wachtwoord"));

        Assert.assertFalse(res.success);
        Assert.assertEquals(403, res.code);
        Assert.assertEquals("Email or password is incorrect", res.message);
    }

    @Test
    public void testLoginWrongPassword() {
        when(authRepository.findByEmail("test@gmail.com")).thenReturn(java.util.Optional.of(new User("test", "test@gmail.com", "wachtwoord")));
        Result res = authService.login(new Login("test@gmail.com", "wachtwoord"));

        Assert.assertFalse(res.success);
        Assert.assertEquals(403, res.code);
        Assert.assertEquals("Email or password is incorrect", res.message);
    }

    @Test
    public void testLoginSuccess() {
        User user = new User("test", "test@gmail.com", "wachtwoord");

        when(authRepository.findByEmail("test@gmail.com")).thenReturn(java.util.Optional.of(user));
        when(bCryptPasswordEncoder.matches("wachtwoord", "wachtwoord")).thenReturn(true);

        Result res = authService.login(new Login("test@gmail.com", "wachtwoord"));

        Assert.assertTrue(res.success);
        Assert.assertEquals(200, res.code);
        Assert.assertEquals(user, res.user);
    }

    @Test
    public void testRegisterNotValid() {
        Result res = authService.register(new Register("test", "fouteemail", "wachtwoord"));

        Assert.assertFalse(res.success);
        Assert.assertEquals(422, res.code);
        Assert.assertEquals("Invalid email", res.message);
    }

    @Test
    public void testRegisterEmailAlreadyExists() {
        when(authRepository.findByEmail("test@gmail.com")).thenReturn(java.util.Optional.of(new User("test", "test@gmail.com", "wachtwoord")));

        Result res = authService.register(new Register("test", "test@gmail.com", "wachtwoord"));

        Assert.assertFalse(res.success);
        Assert.assertEquals(422, res.code);
        Assert.assertEquals("This email address is already in use", res.message);
    }

    @Test
    public void testRegisterUsernameAlreadyExists() {
        when(authRepository.findByUsername("test")).thenReturn(java.util.Optional.of(new User("test", "test@gmail.com", "wachtwoord")));

        Result res = authService.register(new Register("test", "test@gmail.com", "wachtwoord"));

        Assert.assertFalse(res.success);
        Assert.assertEquals(422, res.code);
        Assert.assertEquals("This username is already in use", res.message);
    }

    @Test
    public void testRegisterSuccess() {
        Result res = authService.register(new Register("test", "test@gmail.com", "wachtwoord"));

        verify(authRepository, times(1)).save(any());
        Assert.assertTrue(res.success);
        Assert.assertEquals(200, res.code);
    }

    @Test
    public void testGetUsername() {
        User user = new User("test", "test@gmail.com", "wachtwoord");
        when(authRepository.findByUsername("test")).thenReturn(java.util.Optional.of(user));

        Assert.assertEquals(user, authService.getByUsername("test").get());
    }

    @Test
    public void testGetUser() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
            User user = new User("test", "test@gmail.com", "wachtwoord");

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(user);

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);

            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            Assert.assertEquals(user, authService.getUser().get());
        }
    }

    @Test
    public void testSave() {
        User user = new User("test", "test@gmail.com", "wachtwoord");
        authService.saveUser(user);
        verify(authRepository, times(1)).save(user);
    }
}
