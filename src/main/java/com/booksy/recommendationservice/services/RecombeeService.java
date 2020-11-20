package com.booksy.recommendationservice.services;

import com.booksy.recommendationservice.events.payloads.*;
import com.booksy.recommendationservice.models.Book;
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

    public void sendBook(SendBookInteraction bookInteraction) {
        LOGGER.info("send book item value event {}", bookInteraction);
        addProperties();
        try {
            recombeeClient.send(createItemValues(bookInteraction));
        } catch (ApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void sendInBulk(SendBulkInteraction bulkInteraction) {
        LOGGER.info("send bulk book event, {} books sent", bulkInteraction.getBooks().size());
        try {
            recombeeClient.send(new Batch(bulkInteraction.getBooks().stream().map(this::createItemValues).collect(Collectors.toList())));
        } catch (ApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void sendUserRatingInteraction(UserInteraction userInteraction) {
        LOGGER.info("send user rating interaction event {}", userInteraction);
        double rating = userInteraction.getRating() / 2.5 - 1;
        try {
            recombeeClient.send(new AddRating(userInteraction.getUserId(), userInteraction.getBookId(), rating)
                    .setCascadeCreate(true)
                    .setRecommId(userInteraction.getRecommId())
            );
        } catch (ApiException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public void deleteRatingInteraction(DeleteInteraction deleteInteraction)  {
        LOGGER.info("send delete interaction event {}", deleteInteraction);
        try {
            recombeeClient.send(new DeleteBookmark(deleteInteraction.getUserId(), deleteInteraction.getBookId()));
        } catch (ApiException ignored) { }
    }

    public void sendViewInteraction(ViewInteraction viewInteraction)  {
        LOGGER.info("send view interaction event {}", viewInteraction);
        try {
            recombeeClient.send(new AddDetailView(viewInteraction.getUserId(), viewInteraction.getBookId()));
        } catch (ApiException ignored) { }
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

    private SetItemValues createItemValues(RecommendedBook recommendedBook) {
        Book book = recommendedBook.getVolumeInfo();
        String thumbnail = book.getThumbnail().equals("assets/img/no_book_cover.jpg") ? book.getThumbnail() : null;
        book.setThumbnail(thumbnail);
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> bookMap = oMapper.convertValue(book, Map.class);
        bookMap.remove("id");
        return new SetItemValues(recommendedBook.getId(), bookMap).setCascadeCreate(true);
    }
}
