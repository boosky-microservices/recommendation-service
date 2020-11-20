package com.booksy.recommendationservice.events;

import com.booksy.recommendationservice.events.payloads.Payload;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Event<T extends Payload> {
    private String type;
    private String date;
    private T payload;
}
