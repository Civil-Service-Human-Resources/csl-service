package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("content")
    @JsonAlias("content")
    private List<CourseLearningTagResponse> results;
    private Integer page;
    private Long totalResults;
    private Integer size;
    private Integer totalElements;
    private Integer totalPages;
    private List<Object> sort;
}
