package uk.gov.cabinetoffice.csl.client.courseCatalogue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCoursesResponse {
    List<Course> results;
}
