package uk.gov.cabinetoffice.csl.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;

import java.util.ArrayList;
import java.util.List;

public interface IHttpClient {
    <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass);

    <T, R> T executeTypeReferenceRequest(RequestEntity<R> request, ParameterizedTypeReference<T> parameterizedTypeReference);

    <R> DownloadableFile executeFileDownloadRequest(RequestEntity<R> request);

    default <T, R extends PagedResponse<T>> List<T> getPaginatedRequest(Class<R> pagedResponseClass, UriComponentsBuilder url, Integer maxPageSize) {
        List<T> results = new ArrayList<>();
        int totalPages = 1;
        url.queryParam("size", maxPageSize).queryParam("page", 0);
        for (int i = 0; i < totalPages; i++) {
            RequestEntity<Void> request = RequestEntity.get(url.build().toUriString()).build();
            R response = executeRequest(request, pagedResponseClass);
            results.addAll(response.getContent());
            totalPages = response.getTotalPages();
            url.replaceQueryParam("page", i + 1);
        }
        return results;
    }
}
