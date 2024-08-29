package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@NoArgsConstructor
@Data
public class PagedCourses {
    private Collection<Course> results;
    private int page;
    private int totalResults;
    private int size;
}
