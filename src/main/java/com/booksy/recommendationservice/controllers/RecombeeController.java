package com.booksy.recommendationservice.controllers;


import com.booksy.recommendationservice.models.*;
import com.booksy.recommendationservice.services.RecombeeService;
import com.recombee.api_client.exceptions.ApiException;
import lombok.AllArgsConstructor;
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
    public void receiveEvent(Event<? extends Payload> domainEvent) throws ApiException {
        Payload payload = domainEvent.getPayload();
        String eventType = domainEvent.getType();
        if (eventType.equals(EventTypes.UPDATE_BOOK_RATING.toString())) {
            recombeeService.sendUserRatingInteraction((UserInteraction) payload);
        }else if(eventType.equals(EventTypes.DELETE_BOOK_RATING.toString())) {
            recombeeService.deleteRatingInteraction((DeleteInteraction) payload);
        }
    }
}
