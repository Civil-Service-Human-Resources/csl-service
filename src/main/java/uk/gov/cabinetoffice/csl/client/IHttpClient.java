package uk.gov.cabinetoffice.csl.client;

import org.springframework.http.RequestEntity;
import uk.gov.cabinetoffice.csl.client.model.DownloadableFile;

public interface IHttpClient {
    <T, R> T executeRequest(RequestEntity<R> request, Class<T> responseClass);

    <R> DownloadableFile executeFileDownloadRequest(RequestEntity<R> request);

    <T, R> T executeTypeReferenceRequest(RequestEntity<R> request);
}
