package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CourseWithModule {
    private final Course course;
    private final Module module;
}
