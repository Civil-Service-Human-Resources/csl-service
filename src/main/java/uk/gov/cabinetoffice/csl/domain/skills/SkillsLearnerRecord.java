package uk.gov.cabinetoffice.csl.domain.skills;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkillsLearnerRecord {

    private String emailAddress;
    private String contentId;
    private Integer progress;
    private boolean isCompleted;
    private String result;
    private Integer timeSpent;
    private LocalDate enrollmentDate;
    private LocalDate completionDate;

}
