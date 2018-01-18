package com.schedules.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class EchoController {

    @GetMapping(value = "/echo", produces = APPLICATION_JSON_VALUE)
    public String echo() {
        return "hello!";
    }
}
