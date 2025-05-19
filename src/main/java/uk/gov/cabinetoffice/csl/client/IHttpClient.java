package uk.gov.cabinetoffice.csl.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;

public interface IHttpClient {
    <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass);

    <T, R> T executeTypeReferenceRequest(RequestEntity<R> request, ParameterizedTypeReference<T> parameterizedTypeReference);

    <R> DownloadableFile executeFileDownloadRequest(RequestEntity<R> request);
}
