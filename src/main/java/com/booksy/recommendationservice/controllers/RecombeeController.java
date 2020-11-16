package com.booksy.recommendationservice.controllers;


import com.booksy.recommendationservice.models.Book;
import com.booksy.recommendationservice.services.RecombeeService;
import com.recombee.api_client.RecombeeClient;
import com.recombee.api_client.exceptions.ApiException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/recommendation")
public class RecombeeController{

    private final RecombeeService recombeeService;

    @PostMapping
    public ResponseEntity<String> postBook(@RequestBody Book book){
        try {
            System.err.println(book);
            String result = recombeeService.sendBook(book);
            return new ResponseEntity<String>(result, HttpStatus.OK);
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Internal error !", HttpStatus.INTERNAL_SERVER_ERROR);
    }



}