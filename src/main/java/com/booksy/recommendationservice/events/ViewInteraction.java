package com.booksy.recommendationservice.events;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ViewInteraction implements Payload {
    private String userId;
    private String bookId;
}
