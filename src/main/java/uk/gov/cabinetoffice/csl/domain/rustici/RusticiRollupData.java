package uk.gov.cabinetoffice.csl.domain.rustici;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RusticiRollupData {

    private String id;

    //This will be mapped to the <ModuleRecord.state>
    private String registrationCompletion;

    //This will be mapped to the <ModuleRecord.result>
    private String registrationSuccess;

    private Course course;

    private Learner learner;

    //This will be mapped to the <ModuleRecord.updatedAt>
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updated;

    //This will be mapped to the <ModuleRecord.completionDate>
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completedDate;

    @JsonIgnore
    public boolean isRollUpValid() {
        if (getLearner() != null && getLearner().getId() != null
                && getCourse() != null && getCourse().getId() != null) {
            return course.getId().matches(".+\\..+");
        } else {
            return false;
        }
    }
}
