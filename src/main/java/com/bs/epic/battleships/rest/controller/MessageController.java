package com.bs.epic.battleships.rest.controller;

import com.bs.epic.battleships.documentation.Controller;
import com.bs.epic.battleships.documentation.annotations.OnError;
import com.bs.epic.battleships.documentation.annotations.Returns;
import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import com.bs.epic.battleships.rest.requestbodies.AddMessage;
import com.bs.epic.battleships.rest.responses.Messages;
import com.bs.epic.battleships.rest.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController extends Controller {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping("/all")
    @Returns(Messages.class)
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(messageService.getAiMessages());
    }

    @PostMapping("/add")
    @Returns(AiMessage.class)
    @OnError(value = String.class, code = 422, desc = "Message already exists")
    public ResponseEntity<?> add(@RequestBody AddMessage message) {
        var allMessages = messageService.getAiMessages();
        for (var m : allMessages) {
            if (m.text.equals(message.text)) {
                return ResponseEntity.status(422).body("Message already exists");
            }
        }

        return ResponseEntity.ok(messageService.add(message.text));
    }

    @PostMapping("/remove/{id}")
    @Returns(String.class)
    public ResponseEntity<?> remove(@PathVariable long id) {
        messageService.remove(id);
        return ResponseEntity.ok("Successfully removed message with id = " + id);
    }
}
