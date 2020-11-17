package com.booksy.recommendationservice.models;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Event {

    private String type;
    private String Date;
    private UserInteraction payload;
}
