package com.bs.epic.battleships.verification;

import com.bs.epic.battleships.rest.repository.dto.User;

public class Result {
    public boolean success;
    public int code;

    public String message;
    public User user;

    public Result(boolean success, int code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Result(User user) {
        this.success = true;
        this.code = 200;
        this.message = "";
        this.user = user;
    }

    static public Result error(int code, String message) {
        return new Result(false, code, message);
    }

    static public Result success(User user) {
        return new Result(user);
    }

    static public Result success() {
        return new Result(true, 200, "");
    }
}
