package uk.gov.cabinetoffice.csl.client.learnerRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.client.model.PagedResponse;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.bulk.BulkCreateOutput;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.bulk.FailedResource;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventStatusDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Slf4j
@Component
public class LearnerRecordClient implements ILearnerRecordClient {

    @Value("${learnerRecord.eventsUrl}")
    private String event;
    @Value("${learnerRecord.bookingsUrl}")
    private String booking;
    @Value("${learnerRecord.learnerRecordsUrl}")
    private String learnerRecordsUrl;
    @Value("${learnerRecord.learnerRecordsMaxPageSize}")
    private Integer learnerRecordsMaxPageSize;
    @Value("${learnerRecord.moduleRecordsForLearnerUrl}")
    private String moduleRecordsUrl;
    @Value("${learnerRecord.learnerRecordEventsUrl}")
    private String learnerRecordEventsUrl;

    private final IHttpClient httpClient;
    private final LearnerRecordFactory learnerRecordFactory;

    public LearnerRecordClient(@Qualifier("learnerRecordHttpClient") IHttpClient httpClient, LearnerRecordFactory learnerRecordFactory) {
        this.httpClient = httpClient;
        this.learnerRecordFactory = learnerRecordFactory;
    }

    @Override
    public BookingDto bookEvent(String eventId, BookingDto booking) {
        log.debug("Booking event {} with data {}", eventId, booking);
        String url = String.format("%s/%s/booking/", event, eventId);
        RequestEntity<BookingDto> request = RequestEntity
                .post(url).body(booking);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public BookingDto updateBooking(String userId, String eventId, BookingDto bookingDto) {
        log.debug("Updating booking for event {} with data {}", eventId, bookingDto);
        String url = String.format("%s/%s/learner/%s", event, eventId, userId);
        RequestEntity<BookingDto> request = RequestEntity
                .patch(url).body(bookingDto);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public BookingDto updateBookingWithId(String eventId, String bookingId, BookingDto bookingDto) {
        log.debug("Updating booking {} with data {}", bookingId, bookingDto);
        String url = String.format("%s/%s%s/%s", event, eventId, booking, bookingId);
        RequestEntity<BookingDto> request = RequestEntity.patch(url).body(bookingDto);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public EventDto updateEvent(String eventId, EventStatusDto dto) {
        log.debug("Updating event {} with data {}", eventId, dto);
        String url = String.format("%s/%s", event, eventId);
        RequestEntity<EventStatusDto> request = RequestEntity.patch(url).body(dto);
        return httpClient.executeRequest(request, EventDto.class);
    }

    @Override
    public List<BookingDto> getBookings(String eventId) {
        log.debug("Fetching bookings for event {}", eventId);
        String url = String.format("%s/%s/booking", event, eventId);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    private <T, R extends PagedResponse<T>> List<T> getPaginatedRequest(Class<R> pagedResponseClass, UriComponentsBuilder url, Integer maxPageSize) {
        List<T> results = new ArrayList<>();
        int totalPages = 1;
        url.queryParam("size", maxPageSize).queryParam("page", 0);
        for (int i = 0; i < totalPages; i++) {
            RequestEntity<Void> request = RequestEntity.get(url.build().toUriString()).build();
            R response = httpClient.executeRequest(request, pagedResponseClass);
            results.addAll(response.getContent());
            totalPages = response.getTotalPages();
            url.replaceQueryParam("page", i+1);
        }
        return results;
    }

    @Override
    public List<LearnerRecord> getLearnerRecords(LearnerRecordQuery query) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(learnerRecordsUrl);
        if (!isEmpty(query.getLearnerRecordTypes())) {
            uriBuilder.queryParam("learnerRecordTypes", query.getLearnerRecordTypes());
        }
        if (!isEmpty(query.getResourceIds())) {
            uriBuilder.queryParam("resourceIds", query.getResourceIds());
        }
        if (!isEmpty(query.getLearnerIds())) {
            uriBuilder.queryParam("learnerIds", query.getLearnerIds());
        }
        return getPaginatedRequest(LearnerRecordPagedResponse.class, uriBuilder, learnerRecordsMaxPageSize)
                .stream().map(learnerRecordFactory::transformLearnerRecord).toList();
    }

    private <Output, Input> List<Output> processBulkResourceOutput(BulkCreateOutput<Output, Input> response) {
        if (!response.getFailedResources().isEmpty()) {
            String message = response.getFailedResources().stream()
                    .map(FailedResource::getReason).collect(Collectors.joining(", "));
            message = String.format("%s resources failed to update. Reasons: %s", response.getFailedResources().size(), message);
            throw new RuntimeException(message);
        }
        return response.getSuccessfulResources();
    }

    @Override
    public List<LearnerRecord> createLearnerRecords(List<LearnerRecordDto> newLearnerRecords) {
        log.debug("Creating learner records {}", newLearnerRecords);
        RequestEntity<List<LearnerRecordDto>> request = RequestEntity.post(learnerRecordsUrl + "/bulk").body(newLearnerRecords);
        List<LearnerRecord> records = processBulkResourceOutput(httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        }));
        return records.stream().map(learnerRecordFactory::transformLearnerRecord).toList();
    }

    @Override
    public List<LearnerRecordEvent> createLearnerRecordEvents(List<LearnerRecordEventDto> body) {
        log.debug("Creating learner record events {}", body);
        RequestEntity<List<LearnerRecordEventDto>> request = RequestEntity.post(learnerRecordEventsUrl).body(body);
        List<LearnerRecordEvent> events = processBulkResourceOutput(httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        }));
        return events.stream().map(learnerRecordFactory::applyLearnerRecordEventData).toList();
    }

    @Override
    public List<ModuleRecord> createModuleRecords(List<ModuleRecord> newRecords) {
        log.debug("Creating module records '{}'", newRecords);
        RequestEntity<List<ModuleRecord>> request = RequestEntity.post(String.format("%s/bulk", moduleRecordsUrl)).body(newRecords);
        return httpClient.executeRequest(request, ModuleRecords.class).getModuleRecords();
    }

    @Override
    public List<ModuleRecord> getModuleRecords(List<ModuleRecordResourceId> moduleRecordIds) {
        log.debug("Getting module records with ids '{}'", moduleRecordIds);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(moduleRecordsUrl);
        uriBuilder.queryParam("userIds", moduleRecordIds.stream().map(ModuleRecordResourceId::getLearnerId).collect(Collectors.toSet()));
        uriBuilder.queryParam("moduleIds", moduleRecordIds.stream().map(ModuleRecordResourceId::getResourceId).collect(Collectors.toSet()));
        RequestEntity<Void> request = RequestEntity.get(uriBuilder.build().toUriString()).build();
        ModuleRecords moduleRecords = httpClient.executeRequest(request, ModuleRecords.class);
        return moduleRecords.getModuleRecords();
    }

    @Override
    public List<ModuleRecord> updateModuleRecords(List<ModuleRecord> input) {
        log.debug("Updating module records '{}'", input);
        RequestEntity<List<ModuleRecord>> request = RequestEntity.put(String.format("%s/bulk", moduleRecordsUrl)).body(input);
        return httpClient.executeRequest(request, ModuleRecords.class).getModuleRecords();
    }

}
