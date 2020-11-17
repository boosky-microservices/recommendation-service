package com.booksy.recommendationservice.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DeleteInteraction implements Payload {
    private String id;
}
