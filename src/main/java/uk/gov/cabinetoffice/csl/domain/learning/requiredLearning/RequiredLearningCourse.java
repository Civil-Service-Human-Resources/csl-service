package uk.gov.cabinetoffice.csl.domain.learning.requiredLearning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.State;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.LearningPeriod;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequiredLearningCourse {
    private String id;
    private String title;
    private String shortDescription;
    private String type;
    private Integer duration;
    private Integer moduleCount;
    private State status;

    @JsonIgnore
    private LearningPeriod learningPeriod;

    public LocalDate getDueBy() {
        return learningPeriod.getEndDate();
    }

    @JsonIgnore
    public void setStatusForModules(List<ModuleRecord> moduleRecords) {
        if (moduleRecords.stream().anyMatch(mr -> mr.getUpdatedAt()
                .isAfter(getLearningPeriod().getStartDateAsDateTime()))) {
            setStatus(State.IN_PROGRESS);
        }
    }
}
