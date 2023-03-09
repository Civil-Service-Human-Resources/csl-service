package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRecords {
    public List<CourseRecord> courseRecords;

    public CourseRecord getCourseRecord(String courseId){
        if (courseRecords != null ) {
            return this.courseRecords
                    .stream()
                    .filter(c -> c.getCourseId().equals(courseId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
