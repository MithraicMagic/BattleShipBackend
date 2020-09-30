package com.bs.epic.battleships.rest.repository.dto;

import com.bs.epic.battleships.documentation.annotations.Doc;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
@DynamicUpdate
public class AiMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Doc("The unique id for this message")
    public long id;
    @Doc("The message")
    public String text;

    public AiMessage() {}

    public AiMessage(String text) {
        this.text = text;
    }
}
