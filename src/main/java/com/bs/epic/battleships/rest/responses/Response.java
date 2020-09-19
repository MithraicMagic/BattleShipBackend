package com.bs.epic.battleships.rest.responses;

import com.bs.epic.battleships.documentation.Documentation;
import org.springframework.http.ResponseEntity;

public class Response <T> {
    private ResponseEntity<T> responseEntity;

    public static ResponseEntity<?> success(Object object) {
        //Documentation.get().
        return ResponseEntity.ok(object);
    }

    public static ResponseEntity<?> error(int code, String message) {
        return ResponseEntity.status(code).body(message);
    }
}
