package com.bs.epic.battleships.util;
import com.bs.epic.battleships.util.result.Error;
import com.bs.epic.battleships.util.result.Result;
import com.bs.epic.battleships.util.result.Success;

import java.util.Random;

public class Util {
    static public String generateNewCode(int length) {
        int leftLimit = 48; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    static public Result verifyUsername(String name) {
        if (name.length() < 4) return new Error("inputUsername", "Username is too short");
        if (name.length() > 20) return new Error("inputUsername", "Username is too long");
        if (name.isBlank()) return new Error("inputUsername", "Username must contain valid characters");

        return new Success();
    }

    static public int randomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    static public boolean randomBool() {
        return new Random().nextBoolean();
    }
}
