package uk.gov.cabinetoffice.csl.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;


@Slf4j
@RestController
public class CslServiceTestController {

    @GetMapping(path = "/test/{input}", produces = "application/json")
    public ResponseEntity<String> test(@PathVariable("input") String input) {
        log.info("Input: {}", input);
        return new ResponseEntity<>(input, OK);
    }
}
