package com.booksy.recommendationservice.services;

import com.booksy.recommendationservice.events.*;
import com.booksy.recommendationservice.events.payloads.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@AllArgsConstructor
public class EventHandlerRegistry {

    private final RecombeeService recombeeService;

    private Map<EventTypes, EventHandler> eventHandlerMap;

    @PostConstruct
    public void init() {
        eventHandlerMap.put(EventTypes.UPDATE_BOOK_RATING, payload -> recombeeService.sendUserRatingInteraction((UserInteraction) payload));
        eventHandlerMap.put(EventTypes.DELETE_BOOK_RATING, payload -> recombeeService.deleteRatingInteraction((DeleteInteraction) payload));
        eventHandlerMap.put(EventTypes.VIEW_INTERACTION, payload -> recombeeService.sendViewInteraction((ViewInteraction) payload));
        eventHandlerMap.put(EventTypes.SEND_BOOK, payload -> recombeeService.sendBook((SendBookInteraction) payload));
        eventHandlerMap.put(EventTypes.SEND_BOOK_BULK, payload -> recombeeService.sendInBulk((SendBulkInteraction) payload));
    }

    public EventHandler getEventHandler(String eventTypeName) {
        EventTypes eventType = null;
        try {
            eventType = EventTypes.valueOf(eventTypeName);
        } catch (IllegalArgumentException ignored) { }
        return eventHandlerMap.get(eventType);
    }
}
