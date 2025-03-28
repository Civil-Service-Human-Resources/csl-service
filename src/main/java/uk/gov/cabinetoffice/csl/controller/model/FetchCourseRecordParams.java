package uk.gov.cabinetoffice.csl.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchCourseRecordParams {
    @NotNull
    String userId;
    List<String> courseIds = Collections.emptyList();

    @JsonIgnore
    public List<CourseRecordId> getAsCourseRecordIds() {
        return courseIds.stream().map(courseId -> new CourseRecordId(userId, courseId)).toList();
    }
}
