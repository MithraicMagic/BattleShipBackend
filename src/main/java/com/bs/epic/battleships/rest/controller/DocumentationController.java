package com.bs.epic.battleships.rest.controller;

import com.bs.epic.battleships.documentation.Controller;
import com.bs.epic.battleships.rest.responses.RestApi;
import com.bs.epic.battleships.rest.responses.SocketApi;
import com.bs.epic.battleships.documentation.Documentation;
import com.bs.epic.battleships.documentation.annotations.Returns;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentation")
public class DocumentationController extends Controller {

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
