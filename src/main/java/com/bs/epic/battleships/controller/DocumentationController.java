package com.bs.epic.battleships.controller;

import com.bs.epic.battleships.controller.responses.RestApi;
import com.bs.epic.battleships.controller.responses.SocketApi;
import com.bs.epic.battleships.documentation.Documentation;
import com.bs.epic.battleships.documentation.Returns;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documentation")
public class DocumentationController {

    public DocumentationController() {
        Documentation.get().addController(this.getClass());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/sockets")
    @Returns(object = SocketApi.class)
    public ResponseEntity<?> getSockets() {
        return ResponseEntity.ok(new SocketApi(Documentation.get().getSocketApi()));
    }

    @RequestMapping(method = RequestMethod.GET, path="/rest")
    @Returns(object = RestApi.class)
    public ResponseEntity<?> getRest() {
        return ResponseEntity.ok(new RestApi(Documentation.get().getRestApi()));
    }
}
