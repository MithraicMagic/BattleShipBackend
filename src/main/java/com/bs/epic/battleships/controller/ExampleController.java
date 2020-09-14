package com.bs.epic.battleships.controller;

import com.bs.epic.battleships.documentation.annotations.Doc;
import com.bs.epic.battleships.documentation.Documentation;
import com.bs.epic.battleships.documentation.annotations.OnError;
import com.bs.epic.battleships.documentation.annotations.Returns;
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
    @OnError(code = 404, value = PlaceShip.class, desc = "Oh oh alles is kapot")
    public ResponseEntity<?> getAardappel() {
        return ResponseEntity.ok("Test");
    }

    @RequestMapping(method = RequestMethod.POST, path = "/fiets/{id}/{rens}")
    @Returns(GameData.class)
    @OnError(code = 422, value = HitMissData.class, desc = "HELP ER ONTBREEKT VAN ALLES")
    @OnError(code = 500, value = String.class, desc = "BIG ERROR :(")
    public ResponseEntity<?> postAardappel(@PathVariable @Doc("Aardappel's id") int id,
                                           @PathVariable @Doc("RENS????") boolean rens, @RequestBody SetupData data) {
        return ResponseEntity.ok("aardappel");
    }
}
