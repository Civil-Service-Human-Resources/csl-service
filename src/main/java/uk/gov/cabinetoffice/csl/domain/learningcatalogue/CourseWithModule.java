package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseWithModule {
    private Course course;
    private Module module;
}
