package com.bs.epic.battleships.unit.verification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bs.epic.battleships.rest.requestbodies.Login;
import com.bs.epic.battleships.rest.requestbodies.Register;
import com.bs.epic.battleships.verification.AuthValidator;
import org.junit.jupiter.api.Test;

public class AuthValidatorTest {
    private AuthValidator authValidator = new AuthValidator();

    @Test
    public void testIsValidLogin() {
        var login = new Login("test@gmail.com", "password");
        assertTrue(authValidator.isValidLogin(login).success);
    }

    @Test
    public void testIsValidLoginIncorrectEmail() {
        var login = new Login("test", "password");
        var result = authValidator.isValidLogin(login);

        assertFalse(result.success);
        assertEquals("Invalid email", result.message);
    }

    @Test
    public void testIsValidLoginNoPass() {
        var login = new Login("test@gmail.com", null);
        var result = authValidator.isValidLogin(login);

        assertFalse(result.success);
        assertEquals("Missing password", result.message);
    }

    @Test
    public void testIsValidLoginPassTooShort() {
        var login = new Login("test@gmail.com", "pass");
        var result = authValidator.isValidLogin(login);

        assertFalse(result.success);
        assertEquals("Password is too short", result.message);
    }

    @Test
    public void testIsValidLoginPassTooLong() {
        var login = new Login("test@gmail.com", "A".repeat(70));
        var result = authValidator.isValidLogin(login);

        assertFalse(result.success);
        assertEquals("Password is too long", result.message);
    }

    @Test
    public void testIsValidLoginPassWithOnlySpaces() {
        var login = new Login("test@gmail.com", " ".repeat(10));
        var result = authValidator.isValidLogin(login);

        assertFalse(result.success);
        assertEquals("Password needs to contain actual characters", result.message);
    }

    @Test
    public void testIsValidRegister() {
        var register = new Register("rens", "rens@gmail.com", "password");
        assertTrue(authValidator.isValidRegister(register).success);
    }

    @Test
    public void testIsValidRegisterWithNoUsername() {
        var register = new Register(null, "rens@gmail.com", "password");
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Missing username", result.message);
    }

    @Test
    public void testIsValidRegisterWithUsernameTooShort() {
        var register = new Register("a", "rens@gmail.com", "password");
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Username is too short", result.message);
    }

    @Test
    public void testIsValidRegisterWithUsernameTooLong() {
        var register = new Register("A".repeat(32), "rens@gmail.com", "password");
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Username is too long", result.message);
    }

    @Test
    public void testIsValidRegisterWithUsernameOnlySpaces() {
        var register = new Register(" ".repeat(10), "rens@gmail.com", "password");
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Username needs to contain actual characters", result.message);
    }

    @Test
    public void testIsValidRegisterWithInvalidEmail() {
        var register = new Register("rens", "rens", "password");
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Invalid email", result.message);
    }

    @Test
    public void testIsValidRegisterWithNoPassword() {
        var register = new Register("rens", "rens@gmail.com", null);
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Missing password", result.message);
    }

    @Test
    public void testIsValidRegisterWithPasswordTooShort() {
        var register = new Register("rens", "rens@gmail.com", "a");
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Password is too short", result.message);
    }

    @Test
    public void testIsValidRegisterWithPasswordTooLong() {
        var register = new Register("rens", "rens@gmail.com", "A".repeat(80));
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Password is too long", result.message);
    }

    @Test
    public void testIsValidRegisterWithPasswordOnlySpaces() {
        var register = new Register("rens", "rens@gmail.com", " ".repeat(10));
        var result = authValidator.isValidRegister(register);

        assertFalse(result.success);
        assertEquals("Password needs to contain actual characters", result.message);
    }

    @Test
    public void testVerifyUsername() {
        var result = authValidator.verifyUsername("rens");
        assertTrue(result.success);
        assertEquals(null, result.getError());
    }

    @Test
    public void testVerifyUsernameWithIncorrect() {
        var result = authValidator.verifyUsername("");
        assertFalse(result.success);
        assertEquals("Username is too short", result.getError().reason);
    }
}
