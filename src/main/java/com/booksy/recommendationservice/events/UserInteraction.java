package com.booksy.recommendationservice.events;


import com.booksy.recommendationservice.events.Payload;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserInteraction implements Payload {

    private String userId;
    private String bookId;
    private String recommId;
    private double rating;
}
