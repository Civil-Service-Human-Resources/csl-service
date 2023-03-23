package uk.gov.cabinetoffice.csl.domain.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String timestamp;
    private String status;
    private String error;
    private String message;
    private String path;
    private String[] messages;
}
