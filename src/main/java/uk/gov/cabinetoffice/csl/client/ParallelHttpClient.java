package uk.gov.cabinetoffice.csl.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;
import uk.gov.cabinetoffice.csl.service.auth.IBearerTokenService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class ParallelHttpClient extends HttpClient {

    private final IBearerTokenService bearerTokenService;

    public ParallelHttpClient(RestTemplate restTemplate, IBearerTokenService bearerTokenService) {
        super(restTemplate);
        this.bearerTokenService = bearerTokenService;
    }

    @Override
    public <T, R extends PagedResponse<T>> List<T> getPaginatedRequest(Class<R> pagedResponseClass, UriComponentsBuilder url, Integer maxPageSize) {
        List<Exception> errors = new ArrayList<>();
        String token = bearerTokenService.getBearerToken();
        Consumer<HttpHeaders> headersConsumer = httpHeaders -> httpHeaders.add("Authorization", "Bearer " + token);
        final List<T> results = new ArrayList<>();
        url.queryParam("size", 1).queryParam("page", 0);
        R initialResponse = executeRequest(RequestEntity.get(url.build().toUriString()).headers(headersConsumer).build(), pagedResponseClass);
        if (initialResponse.getTotalElements() >= 1) {
            url.replaceQueryParam("size", maxPageSize);
            IntStream.range(0, (int) Math.ceil((double) initialResponse.getTotalElements() / maxPageSize))
                    .parallel()
                    .boxed()
                    .forEach(page -> {
                        url.replaceQueryParam("page", page);
                        try {
                            results.addAll(executeRequest(RequestEntity.get(url.build().toUriString()).headers(headersConsumer).build(), pagedResponseClass).getContent());
                        } catch (Exception e) {
                            errors.add(e);
                        }
                    });
        }
        if (!errors.isEmpty()) {
            String message = "There were exceptions executing the paginated request: " + errors.stream().map(Exception::getMessage).collect(Collectors.joining("\n"));
            throw new RuntimeException(message);
        }
        return results;
    }

}
