package com.bs.epic.battleships.rest.repository;

import com.bs.epic.battleships.rest.repository.dto.AiMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

public interface AiMessageRepository extends JpaRepository<AiMessage, Long>{
}
