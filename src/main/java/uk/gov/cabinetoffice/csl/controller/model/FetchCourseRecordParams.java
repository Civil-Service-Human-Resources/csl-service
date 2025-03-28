package uk.gov.cabinetoffice.csl.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchCourseRecordParams {
    @NotNull
    List<String> userIds;
    List<String> courseIds;

    @JsonIgnore
    public List<CourseRecordId> getAsCourseRecordIds() {
        List<CourseRecordId> recordIds = new ArrayList<>();
        userIds.forEach(userId -> courseIds.forEach(courseId -> recordIds.add(new CourseRecordId(userId, courseId))));
        return recordIds;
    }
}
