package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    private LocalDateTime timestamp;
    private String timestamp;
    private String status;
    private String message;
    private String error;
    private String path;
}
