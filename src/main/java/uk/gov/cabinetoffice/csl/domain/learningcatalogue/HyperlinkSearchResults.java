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
public class HyperlinkSearchResults {

    @JsonProperty("results")
    @JsonAlias("content")
    private List<HyperlinkDto> results;
    private Integer page;
    private Integer size;
    private Integer totalResults;
    private Integer totalPages;
    private Integer totalElements;
    private Integer numberOfElements;
    private boolean last;
    private boolean first;
}
