package com.booksy.recommendationservice.events;

import com.booksy.recommendationservice.events.payloads.*;

import java.util.HashMap;
import java.util.Map;

public class EventRegistry {

    private EventRegistry() { }

    public static final Map<EventTypes, Class<? extends Payload>> EVENT_NAME_TO_PAYLOAD = new HashMap<>();

    static {
        EVENT_NAME_TO_PAYLOAD.put(EventTypes.UPDATE_BOOK_RATING, UserInteraction.class);
        EVENT_NAME_TO_PAYLOAD.put(EventTypes.DELETE_BOOK_RATING, DeleteInteraction.class);
        EVENT_NAME_TO_PAYLOAD.put(EventTypes.VIEW_INTERACTION, ViewInteraction.class);
        EVENT_NAME_TO_PAYLOAD.put(EventTypes.SEND_BOOK, SendBookInteraction.class);
        EVENT_NAME_TO_PAYLOAD.put(EventTypes.SEND_BOOK_BULK, SendBulkInteraction.class);
    }
}
