package com.booksy.recommendationservice.events.payloads;

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
