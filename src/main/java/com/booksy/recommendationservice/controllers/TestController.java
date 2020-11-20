package com.booksy.recommendationservice.controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/")
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
