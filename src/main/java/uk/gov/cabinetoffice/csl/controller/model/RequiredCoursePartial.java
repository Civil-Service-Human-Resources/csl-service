package uk.gov.cabinetoffice.csl.controller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequiredCoursePartial {

    private String id;
    private String title;
    private LocalDateTime learningPeriodStartTimestamp;
    private LocalDateTime learningPeriodEndTimestamp;

}
