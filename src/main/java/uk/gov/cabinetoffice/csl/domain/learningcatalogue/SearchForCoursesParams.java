package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchForCoursesParams {
    String query;

    Collection<CourseStatus> status;
    String visibility;
    Collection<String> types;
    String cost;

    Collection<String> courseIds;
    Collection<String> departments;
    Collection<String> areasOfWork;
    Collection<String> interests;
}
