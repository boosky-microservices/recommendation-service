package com.booksy.recommendationservice.configuration;

import com.booksy.recommendationservice.events.Event;
import com.booksy.recommendationservice.events.EventRegistry;
import com.booksy.recommendationservice.events.EventTypes;
import com.booksy.recommendationservice.events.payloads.Payload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class EventDeserializer implements Deserializer<Event<?>> {
    ObjectMapper om = new ObjectMapper();

    public Event<? extends Payload> deserializes(JsonNode node) throws IOException {
        String type = node.get("type").asText();
        String date = node.get("date").asText();
        EventTypes foundEventType = null;
        try{
            foundEventType = EventTypes.valueOf(type);
        } catch (IllegalArgumentException ignored) { }

        Class<? extends Payload> payloadClass = EventRegistry.EVENT_NAME_TO_PAYLOAD.get(foundEventType);
        Payload payload = payloadClass != null ? new ObjectMapper().readValue(node.get("payload").traverse(), payloadClass) : null;
        return new Event<>(type, date, payload);
    }

    @SneakyThrows
    @Override
    public Event<? extends Payload> deserialize(String s, byte[] bytes) {
        ObjectReader reader = om.reader();
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        JsonNode node = reader.readTree(new ByteArrayInputStream(bytes));
        byteIn.close();
        return deserializes(node);
    }
}
