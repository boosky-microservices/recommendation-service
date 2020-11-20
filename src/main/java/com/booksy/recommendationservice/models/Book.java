package com.booksy.recommendationservice.models;

import lombok.*;

import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Book {
    private String id;
    private String title;
    private String subtitle;
    private String publisher;
    private String description;
    private int pageCount;
    private Set<String> authors;
    private Set<String> categories;
    private String thumbnail;
    private String publishedDate;
}

