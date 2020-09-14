package com.bs.epic.battleships.controller;

import com.bs.epic.battleships.documentation.Doc;
import com.bs.epic.battleships.documentation.Documentation;
import com.bs.epic.battleships.documentation.OnError;
import com.bs.epic.battleships.documentation.Returns;
import com.bs.epic.battleships.events.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/example")
public class ExampleController {

    public ExampleController() {
        Documentation.get().addController(this.getClass());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/aardappel")
    @Returns(Shoot.class)
    @OnError(code = 404, value = PlaceShip.class)
    public ResponseEntity<?> getAardappel() {
        return ResponseEntity.ok("Test");
    }

    @RequestMapping(method = RequestMethod.POST, path = "/fiets/{id}/{rens}")
    @Returns(GameData.class)
    @OnError(code = 422, value = HitMissData.class)
    public ResponseEntity<?> postAardappel(@PathVariable @Doc("Aardappel's id") int id,
                                           @PathVariable @Doc("RENS????") boolean rens, @RequestBody SetupData data) {
        return ResponseEntity.ok("aardappel");
    }
}
