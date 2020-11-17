package com.booksy.recommendationservice.models;

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
