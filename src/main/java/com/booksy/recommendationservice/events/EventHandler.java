package com.booksy.recommendationservice.events;

import com.booksy.recommendationservice.events.payloads.Payload;

public interface EventHandler {
    void handleEvent(Payload payload);
}
