package uk.gov.cabinetoffice.csl.domain.csrs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Domain {
    Long id;
    String domain;
    LocalDateTime createdTimestamp;
}
