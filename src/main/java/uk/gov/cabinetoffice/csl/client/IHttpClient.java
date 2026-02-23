package uk.gov.cabinetoffice.csl.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;

import java.util.List;
import java.util.Map;

public interface IHttpClient {
    <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass);

    <T, R> T executeTypeReferenceRequest(RequestEntity<R> request, ParameterizedTypeReference<T> parameterizedTypeReference);

    <R> DownloadableFile executeFileDownloadRequest(RequestEntity<R> request);

    <I, T, R extends PagedResponse<T>> List<T> postPaginatedRequest(Class<R> pagedResponseClass, I body, UriComponentsBuilder url, Integer maxPageSize);

    <T, R extends PagedResponse<T>> List<T> getPaginatedRequest(Class<R> pagedResponseClass, UriComponentsBuilder url, Integer maxPageSize);

    <T, R> Map<String, T> executeMapRequest(RequestEntity<R> request, ParameterizedTypeReference<Map<String, T>> parameterizedTypeReference);
}
