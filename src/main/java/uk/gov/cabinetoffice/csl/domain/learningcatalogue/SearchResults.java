package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResults<Dto> {
    @JsonProperty("results")
    @JsonAlias("content")
    private List<Dto> results;
    private Integer page;
    private Integer size;
    private Integer totalResults;
    private Integer totalPages;
    private Integer totalElements;
    private Integer numberOfElements;
    private boolean last;
    private boolean first;

    public SearchResults() {
        this.results = new ArrayList<>();
        this.page = 0;
        this.size = 0;
        this.totalResults = 0;
        this.totalPages = 0;
        this.totalElements = 0;
        this.numberOfElements = 0;
        this.last = false;
        this.first = true;
    }
}
