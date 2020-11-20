package com.booksy.recommendationservice.events.payloads;

import com.booksy.recommendationservice.models.RecommendedBook;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendBookInteraction extends RecommendedBook implements Payload {
    private String _id;
}
