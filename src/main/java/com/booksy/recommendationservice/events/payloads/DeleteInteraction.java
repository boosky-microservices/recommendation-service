package com.booksy.recommendationservice.events.payloads;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DeleteInteraction implements Payload {
    private String bookId;
    private String userId;
}
