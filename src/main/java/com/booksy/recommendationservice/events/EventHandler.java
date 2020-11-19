package com.booksy.recommendationservice.events;

public interface EventHandler {
    void handleEvent(Payload payload);
}
