package com.booksy.recommendationservice.services;

import com.booksy.recommendationservice.models.Book;
import com.booksy.recommendationservice.models.UserInteraction;
import com.booksy.recommendationservice.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.*;
import com.recombee.api_client.bindings.RecommendationResponse;
import com.recombee.api_client.exceptions.ApiException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor
@Service
public class RecombeeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecombeeService.class);

    @Value("${recombee.token}")
    private String token;

    private RecombeeClient recombeeClient;

    @PostConstruct
    public void init() {
        recombeeClient = new RecombeeClient("booksy-api-dev", token);
    }

    public String sendBook(Book book) throws ApiException {
        addProperties();
        return recombeeClient.send(createItemValues(book));
    }

    private void addProperties() {
        try {
            recombeeClient.send(new Batch(new Request[]{
                    new AddItemProperty("title", "string"),
                    new AddItemProperty("subtitle", "string"),
                    new AddItemProperty("publisher", "string"),
                    new AddItemProperty("description", "string"),
                    new AddItemProperty("pageCount", "int"),
                    new AddItemProperty("authors", "set"),
                    new AddItemProperty("categories", "set"),
                    new AddItemProperty("thumbnail", "image"),
                    new AddItemProperty("publishedDate", "string"),
            }));
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    private SetItemValues createItemValues(Book book) {
        String thumbnail = book.getThumbnail().equals("assets/img/no_book_cover.jpg") ? book.getThumbnail() : null;
        book.setThumbnail(thumbnail);
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> bookMap = oMapper.convertValue(book, Map.class);
        bookMap.remove("id");
        return new SetItemValues(book.getId(), bookMap).setCascadeCreate(true);
    }

    public void sendUserRatingInteraction(UserInteraction userInteraction) throws ApiException {
        double rating = userInteraction.getRating() / 2.5 - 1;
        recombeeClient.send(new AddRating(userInteraction.getUserId(), userInteraction.getBookId(), rating)
                .setCascadeCreate(true)
                .setRecommId(userInteraction.getRecommId())
        );
    }

    public void sendInBulk(List<Book> books) throws ApiException {
        recombeeClient.send(new Batch(books.stream().map(this::createItemValues).collect(Collectors.toList())));
    }

    public void deleteRatingInteraction(DeleteInteraction deleteInteraction) {
        try {
            recombeeClient.send(new DeleteBookmark(deleteInteraction.getUserId(), deleteInteraction.getBookId()));
        } catch (ApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void sendViewInteraction(UserInteraction userInteraction) throws ApiException {
        recombeeClient.send(new AddDetailView(userInteraction.getUserId(), userInteraction.getBookId()));
    }

    public List<RecommendedBook> getRecommendedBooksFromBook(String bookId, String userId, int count) throws ApiException {
        List<RecommendedBook> recommendedBooks = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper();
        RecommendationResponse recommendations = recombeeClient.send(new RecommendItemsToItem(bookId, userId, count).setCascadeCreate(true).setReturnProperties(true));
        recommendations.forEach(recommendation -> {
            Book book = mapper.convertValue(recommendation.getValues(), Book.class);
            recommendedBooks.add(new RecommendedBook(recommendation.getId(), book));
        });
        return recommendedBooks;
    }

    public void mergeUsers(String targetUserId, String sourceUserId) throws ApiException {
        recombeeClient.send(new MergeUsers(targetUserId, sourceUserId).setCascadeCreate(true));
    }
}
