package com.bs.epic.battleships.unit.user;

import com.bs.epic.battleships.stubs.StubSocket;
import com.bs.epic.battleships.user.User;
import com.bs.epic.battleships.user.UserState;
import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    public void testSetStateSameState() {
        User user = new User(new StubSocket());

        user.setState(UserState.EnterName);
    }
}
