package com.bs.epic.battleships.controller;

import com.bs.epic.battleships.documentation.Documentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentation")
public class DocumentationController {

    @GetMapping("")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(Documentation.get().getApi());
    }
}
