package com.bs.epic.battleships.rest.responses;

import com.bs.epic.battleships.rest.repository.dto.User;

public class UserResponse {
    public User user;

    public UserResponse(User user) {
        this.user = user;
    }
}
