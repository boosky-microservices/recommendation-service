package com.booksy.recommendationservice.events.payloads;

import com.booksy.recommendationservice.models.RecommendedBook;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SendBulkInteraction implements Payload {
    private List<RecommendedBook> books;
    private int totalItems;
}
