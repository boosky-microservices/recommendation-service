package com.booksy.recommendationservice.controllers;


import com.booksy.recommendationservice.events.Event;
import com.booksy.recommendationservice.events.EventHandler;
import com.booksy.recommendationservice.events.payloads.Payload;
import com.booksy.recommendationservice.models.*;
import com.booksy.recommendationservice.services.EventHandlerRegistry;
import com.booksy.recommendationservice.services.RecombeeService;
import com.recombee.api_client.exceptions.ApiException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/recommendation")
public class RecombeeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecombeeController.class);

    private final EventHandlerRegistry eventHandlerRegistry;
    private final RecombeeService recombeeService;

    @GetMapping("/books")
    public ResponseEntity<List<RecommendedBook>> getRecommendedBooks(@AuthenticationPrincipal Jwt jwt,
                                                                     @RequestParam String bookId,
                                                                     @RequestParam int count
    ) throws ApiException {
        String userId = jwt != null && jwt.getClaim("sub") != null ? jwt.getClaim("sub").toString().split("\\|")[1] : "noid";
        return new ResponseEntity<>(recombeeService.getRecommendedBooksFromBook(bookId, userId, count), HttpStatus.OK);
    }

    @KafkaListener(topics = "ap8dmjx0-recommendation-events", groupId = "ap8dmjx0-consumers")
    public void receiveEvent(Event<? extends Payload> domainEvent) {
        LOGGER.info("receive {} event {}", domainEvent.getType(), domainEvent);
        Payload payload = domainEvent.getPayload();
        String eventType = domainEvent.getType();
        EventHandler eventHandler = eventHandlerRegistry.getEventHandler(eventType);
        if(eventHandler != null) {
            eventHandler.handleEvent(payload);
        }
    }
}
