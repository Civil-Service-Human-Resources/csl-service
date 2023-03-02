package uk.gov.cabinetoffice.csl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course {

    //This will be mapped to the <CourseRecord.courseId>.<ModuleRecord.moduleId>
    private String id;

    //This will be mapped to the <CourseRecord.courseTitle>
    private String title;

    private Integer version;
}
