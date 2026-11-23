package uk.gov.cabinetoffice.csl.service.job.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobResult<T> {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private T result;

}
