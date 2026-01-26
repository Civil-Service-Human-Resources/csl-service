package uk.gov.cabinetoffice.csl.domain.learning.learningPlan;

import lombok.*;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class LearningPlanCourse {
    private String id;
    private String title;
    private String shortDescription;
    private String type;
    private Integer duration;
    private Integer moduleCount;
    private Integer costInPounds;
    private State status;
}
