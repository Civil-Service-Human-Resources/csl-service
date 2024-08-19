package uk.gov.cabinetoffice.csl.client.mdoel;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Data
public class PagedResponse<T> {

    private Collection<T> results;
    private int page;
    private int totalResults;
    private int size;

    public Collection<T> getResults() {
        return results == null ? List.of() : results;
    }

}
