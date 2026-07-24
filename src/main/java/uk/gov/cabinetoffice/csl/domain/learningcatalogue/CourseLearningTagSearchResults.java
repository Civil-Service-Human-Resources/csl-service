package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseLearningTagSearchResults {

    @JsonAlias("content")
    private List<CourseLearningTagResponse> results;
    private Integer page;
    private Long totalResults;
    private Integer size;
}
