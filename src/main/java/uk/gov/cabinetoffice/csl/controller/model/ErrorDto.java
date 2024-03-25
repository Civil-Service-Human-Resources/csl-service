package uk.gov.cabinetoffice.csl.controller.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ErrorDto {
    private Instant timestamp = Instant.now();
    private List<String> errors = new ArrayList<>();
    private int status;
    private String message;
}
