package uk.gov.cabinetoffice.csl.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResponse<T> {
    protected List<T> content;
    protected boolean last;
    protected Integer number;
    protected Integer totalPages;
    protected Integer totalElements;
    protected Integer size;

    public List<T> getContent() {
        return Objects.requireNonNullElse(content, new ArrayList<>());
    }
    
    public Integer getNumber() {
        return Objects.requireNonNullElse(number, 0);
    }

    public Integer getTotalPages() {
        return Objects.requireNonNullElse(totalPages, 0);
    }

    public Integer getTotalElements() {
        return Objects.requireNonNullElse(totalElements, 0);
    }

    public Integer getSize() {
        return Objects.requireNonNullElse(size, 0);
    }
}
