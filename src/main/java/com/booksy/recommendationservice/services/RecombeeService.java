package com.booksy.recommendationservice.services;

import com.booksy.recommendationservice.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.*;
import com.recombee.api_client.bindings.Recommendation;
import com.recombee.api_client.bindings.RecommendationResponse;
import com.recombee.api_client.exceptions.ApiException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
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

    @Value("${recombee.token}")
    private String token;

    private RecombeeClient recombeeClient;

    @PostConstruct
    public void init() {
        recombeeClient = new RecombeeClient("booksy-api-dev", token);
    }

    public String sendBook(Book book) throws ApiException {
        addPropreties();
        return recombeeClient.send(createItemValues(book));
    }

    public void addPropreties() {
        try {
            recombeeClient.send(new Batch(new Request[]{new AddItemProperty("title", "string"),
                    new AddItemProperty("subtitle", "string"),
                    new AddItemProperty("publisher", "string"),
                    new AddItemProperty("description", "string"),
                    new AddItemProperty("pageCount", "int"),
                    new AddItemProperty("authors", "set"),
                    new AddItemProperty("categories", "set"),
                    new AddItemProperty("thumbnail", "image"),
                    new AddItemProperty("publishedDate", "string"),}));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SetItemValues createItemValues(Book book) {
        String thumbnail = book.getThumbnail().equals("assets/img/no_book_cover.jpg") ? book.getThumbnail() : null;
        book.setThumbnail(thumbnail);
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> booktMap = oMapper.convertValue(book, Map.class);
        booktMap.remove("id");
        return new SetItemValues(book.getId(), booktMap).setCascadeCreate(true);

    }

    public void sendUserRatingInteraction(Event domainEvent) throws ApiException {
        double rating = domainEvent.getPayload().getRating() / 2.5 - 1;
        domainEvent.getPayload().setRating(rating);
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> userInteractiontMap = oMapper.convertValue(domainEvent.getPayload(), Map.class);
        userInteractiontMap.remove("recommId");
        SetItemValues userInteractionRequest = new SetItemValues(domainEvent.getPayload().getRecommId() , userInteractiontMap).setCascadeCreate(true);
        recombeeClient.send(userInteractionRequest);

    }

    public void sendInBulk(List<Book> books) throws ApiException{
       recombeeClient.send(new Batch(books.stream().map(this::createItemValues).collect(Collectors.toList())));
    }

    public void deleteRatingInteraction( UserInteraction userInteraction) throws ApiException {
        recombeeClient.send(new DeleteBookmark(userInteraction.getUserId(),userInteraction.getBookId()));
    }

    public void sendViewInteraction(UserInteraction userInteraction) throws ApiException{
        recombeeClient.send(new AddDetailView(userInteraction.getUserId(),userInteraction.getBookId()));
    }

    public List<RecommendedBook> getRecommendedBooksToUser(String userId, String category, int count) throws ApiException{
        List books = new ArrayList<RecommendedBook>();
        final ObjectMapper mapper = new ObjectMapper();
        RecommendationResponse recommendedBooks = recombeeClient.send(new RecommendItemsToUser(userId,count).setCascadeCreate(true).setReturnProperties(true));
        for(Recommendation rec: recommendedBooks){
            Book book = mapper.convertValue(rec.getValues(),Book.class);
            RecommendedBook recommendedBook = new RecommendedBook(rec.getId(),book);
            books.add(recommendedBook);
        }
        return books;
    }

    public List<RecommendedBook> getRecommendedBooksFromBook(String bookId, String userId, int count) throws ApiException{
        List books = new ArrayList<RecommendedBook>();
        final ObjectMapper mapper = new ObjectMapper();
        RecommendationResponse recommendedBooks = recombeeClient.send(new RecommendItemsToItem(bookId,userId,count).setCascadeCreate(true).setReturnProperties(true));
        for(Recommendation rec: recommendedBooks){
            Book book = mapper.convertValue(rec.getValues(),Book.class);
            RecommendedBook recommendedBook = new RecommendedBook(rec.getId(),book);
            books.add(recommendedBook);
        }
        return books;
    }

    public void mergeUsers(String targetUserId, String sourceUserId) throws ApiException{
        recombeeClient.send(new MergeUsers(targetUserId,sourceUserId).setCascadeCreate(true));
    }

}
