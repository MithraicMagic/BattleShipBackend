package com.bs.epic.battleships.unit.util;

import com.bs.epic.battleships.util.Util;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class UtilTest {
    @Test
    public void testGenerateNewCode() {
        String randomString = Util.generateNewCode(20);
        String secondRandomString = Util.generateNewCode(20);

        Assert.assertEquals(20, randomString.length());
        Assert.assertEquals(20, secondRandomString.length());
        Assert.assertNotEquals(randomString, secondRandomString);
    }

    @Test
    public void testRandomInt() {
        int randomInt = Util.randomInt(5, 10);
        Assert.assertTrue(randomInt >= 5 && randomInt <= 10);
    }

    @Test
    public void testRandomIntMinBiggerThanMax() {
        Assert.assertThrows(IllegalArgumentException.class, () -> Util.randomInt(10, 5));
    }

    @Test
    public void testRandomBool() {
        Boolean randomBool = Util.randomBool();
        Assert.assertNotNull(randomBool);
    }
}
