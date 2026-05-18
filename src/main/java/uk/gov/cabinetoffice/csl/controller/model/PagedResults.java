package uk.gov.cabinetoffice.csl.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResults<T> {
    protected List<T> results;
    protected Integer page;
    protected Integer size;
    protected Integer totalResults;
}
