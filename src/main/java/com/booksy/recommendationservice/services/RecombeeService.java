package com.booksy.recommendationservice.services;

import com.booksy.recommendationservice.models.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.api_requests.AddItemProperty;
import com.recombee.api_client.api_requests.Batch;
import com.recombee.api_client.api_requests.Request;
import com.recombee.api_client.api_requests.SetItemValues;
import com.recombee.api_client.exceptions.ApiException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;


@AllArgsConstructor
@NoArgsConstructor
@Service
public class RecombeeService {

    @Value("${recombee.token}")
    private String token;

    private RecombeeClient recombeeClient;

    @PostConstruct
    public void init(){
        recombeeClient = new RecombeeClient("booksy-api-dev", token);
    }

    public String sendBook(Book book) throws ApiException {
        System.out.println(token);
        addPropreties();
       return recombeeClient.send(createItemValues(book));
    }

    public void addPropreties(){
        try {
            recombeeClient.send(new Batch(new Request[] {new AddItemProperty("title", "string"),
                    new AddItemProperty("subtitle", "string"),
                    new AddItemProperty("publisher", "string"),
                    new AddItemProperty("description", "string"),
                    new AddItemProperty("pageCount", "int"),
                    new AddItemProperty("authors", "set"),
                    new AddItemProperty("categories", "set"),
                    new AddItemProperty("thumbnail", "image"),
                    new AddItemProperty("publishedDate", "string"),}));
        }catch (Exception e){
            e.printStackTrace();
        }
        }

    public SetItemValues createItemValues(Book book){
        String thumbnail = book.getThumbnail().equals("assets/img/no_book_cover.jpg") ? book.getThumbnail() : null;
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> booktMap = oMapper.convertValue(book, Map.class);
        booktMap.remove("id");

        return new SetItemValues(book.getId(), booktMap).setCascadeCreate(true);

    }


}
