package com.bs.epic.battleships.controller;

import com.bs.epic.battleships.controller.responses.RestApi;
import com.bs.epic.battleships.controller.responses.SocketApi;
import com.bs.epic.battleships.documentation.Documentation;
import com.bs.epic.battleships.documentation.annotations.OnError;
import com.bs.epic.battleships.documentation.annotations.Returns;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentation")
public class DocumentationController {

    public DocumentationController() {
        Documentation.get().addController(this.getClass());
    }

    @GetMapping("/sockets")
    @Returns(SocketApi.class)
    public ResponseEntity<?> getSockets() {
        return ResponseEntity.ok(new SocketApi(Documentation.get().getSocketApi()));
    }

    @GetMapping("/rest")
    @Returns(RestApi.class)
    public ResponseEntity<?> getRest() {
        return ResponseEntity.ok(new RestApi(Documentation.get().getRestApi()));
    }
}
