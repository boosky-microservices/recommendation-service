package com.booksy.recommendationservice.models;


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
    private int rating;
}
