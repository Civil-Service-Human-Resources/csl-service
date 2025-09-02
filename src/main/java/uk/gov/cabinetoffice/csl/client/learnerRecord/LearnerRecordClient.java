package uk.gov.cabinetoffice.csl.client.learnerRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.bulk.BulkCreateOutput;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.bulk.FailedResource;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventStatusDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.*;
import uk.gov.cabinetoffice.csl.util.IUtilService;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Slf4j
@Component
public class LearnerRecordClient implements ILearnerRecordClient {

    private final IHttpClient httpClient;
    private final LearnerRecordClientConfigParams configParams;
    private final LearnerRecordFactory learnerRecordFactory;
    private final IUtilService utilService;

    public LearnerRecordClient(@Qualifier("learnerRecordHttpClient") IHttpClient httpClient, LearnerRecordClientConfigParams configParams,
                               LearnerRecordFactory learnerRecordFactory, IUtilService utilService) {
        this.httpClient = httpClient;
        this.configParams = configParams;
        this.learnerRecordFactory = learnerRecordFactory;
        this.utilService = utilService;
    }

    @Override
    public BookingDto bookEvent(String eventId, BookingDto booking) {
        log.debug("Booking event {} with data {}", eventId, booking);
        String url = String.format("%s/%s/booking/", configParams.getEventsUrl(), eventId);
        RequestEntity<BookingDto> request = RequestEntity
                .post(url).body(booking);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public BookingDto updateBooking(String userId, String eventId, BookingDto bookingDto) {
        log.debug("Updating booking for event {} with data {}", eventId, bookingDto);
        String url = String.format("%s/%s/learner/%s", configParams.getEventsUrl(), eventId, userId);
        RequestEntity<BookingDto> request = RequestEntity
                .patch(url).body(bookingDto);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public BookingDto updateBookingWithId(String eventId, String bookingId, BookingDto bookingDto) {
        log.debug("Updating booking {} with data {}", bookingId, bookingDto);
        String url = String.format("%s/%s%s/%s", configParams.getEventsUrl(), eventId, configParams.getBookingsUrl(), bookingId);
        RequestEntity<BookingDto> request = RequestEntity.patch(url).body(bookingDto);
        return httpClient.executeRequest(request, BookingDto.class);
    }

    @Override
    public EventDto updateEvent(String eventId, EventStatusDto dto) {
        log.debug("Updating event {} with data {}", eventId, dto);
        String url = String.format("%s/%s", configParams.getEventsUrl(), eventId);
        RequestEntity<EventStatusDto> request = RequestEntity.patch(url).body(dto);
        return httpClient.executeRequest(request, EventDto.class);
    }

    @Override
    public List<BookingDto> getBookings(String eventId) {
        log.debug("Fetching bookings for event {}", eventId);
        String url = String.format("%s/%s/booking", configParams.getEventsUrl(), eventId);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        return httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        });
    }

    @Override
    public List<LearnerRecord> getLearnerRecords(LearnerRecordQuery query) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(configParams.getLearnerRecordsUrl());
        if (!isEmpty(query.getLearnerRecordTypes())) {
            uriBuilder.queryParam("learnerRecordTypes", query.getLearnerRecordTypes());
        }
        if (!isEmpty(query.getResourceIds())) {
            uriBuilder.queryParam("resourceIds", query.getResourceIds());
        }
        if (!isEmpty(query.getLearnerIds())) {
            uriBuilder.queryParam("learnerIds", query.getLearnerIds());
        }
        return httpClient.getPaginatedRequest(LearnerRecordPagedResponse.class, uriBuilder, configParams.getLearnerRecordsMaxPageSize())
                .stream().map(learnerRecordFactory::transformLearnerRecord).toList();
    }

    @Override
    public List<LearnerRecordEvent> getLearnerRecordEvents(LearnerRecordEventQuery query) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(configParams.getLearnerRecordEventsUrl());
        if (!isEmpty(query.getEventTypes())) {
            uriBuilder.queryParam("eventTypes", query.getEventTypes());
        }
        if (!isEmpty(query.getResourceIds())) {
            uriBuilder.queryParam("resourceIds", query.getResourceIds());
        }
        if (!isEmpty(query.getUserId())) {
            uriBuilder.queryParam("userId", query.getUserId());
        }
        return httpClient.getPaginatedRequest(LearnerRecordEventPagedResponse.class, uriBuilder, configParams.getLearnerRecordsMaxPageSize());
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
        RequestEntity<List<LearnerRecordDto>> request = RequestEntity.post(configParams.getLearnerRecordsUrl() + "/bulk").body(newLearnerRecords);
        List<LearnerRecord> records = processBulkResourceOutput(httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        }));
        return records.stream().map(learnerRecordFactory::transformLearnerRecord).toList();
    }

    @Override
    public List<LearnerRecordEvent> createLearnerRecordEvents(List<LearnerRecordEventDto> body) {
        log.debug("Creating learner record events {}", body);
        RequestEntity<List<LearnerRecordEventDto>> request = RequestEntity.post(configParams.getLearnerRecordEventsUrl()).body(body);
        List<LearnerRecordEvent> events = processBulkResourceOutput(httpClient.executeTypeReferenceRequest(request, new ParameterizedTypeReference<>() {
        }));
        return events.stream().map(learnerRecordFactory::applyLearnerRecordEventData).toList();
    }

    @Override
    public List<ModuleRecord> createModuleRecords(List<ModuleRecord> newRecords) {
        log.debug("Creating module records '{}'", newRecords);
        RequestEntity<List<ModuleRecord>> request = RequestEntity.post(String.format("%s/bulk", configParams.getModuleRecordsUrl())).body(newRecords);
        return httpClient.executeRequest(request, ModuleRecords.class).getModuleRecords();
    }

    @Override
    public List<ModuleRecord> getModuleRecords(GetModuleRecordParams query) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(configParams.getModuleRecordsUrl());
        log.debug("Getting module records with params '{}'", query);
        if (!isEmpty(query.getUserIds())) {
            uriBuilder.queryParam("userIds", query.getUserIds());
        }
        if (!isEmpty(query.getModuleIds())) {
            if (query.getModuleIds().size() > configParams.getModuleRecordBatchSize()) {
                return utilService.batchList(query.getModuleIds().stream().toList(), configParams.getModuleRecordBatchSize())
                        .stream().flatMap(moduleIds -> {
                            GetModuleRecordParams params = GetModuleRecordParams.builder()
                                    .userIds(query.getUserIds())
                                    .moduleIds(new HashSet<>(moduleIds))
                                    .build();
                            return getModuleRecords(params).stream();
                        }).toList();
            }
            uriBuilder.queryParam("moduleIds", query.getModuleIds());
        }
        RequestEntity<Void> request = RequestEntity.get(uriBuilder.build().toUriString()).build();
        ModuleRecords moduleRecords = httpClient.executeRequest(request, ModuleRecords.class);
        return moduleRecords.getModuleRecords();
    }

    @Override
    public List<ModuleRecord> updateModuleRecords(List<ModuleRecord> input) {
        log.debug("Updating module records '{}'", input);
        RequestEntity<List<ModuleRecord>> request = RequestEntity.put(String.format("%s/bulk", configParams.getModuleRecordsUrl())).body(input);
        return httpClient.executeRequest(request, ModuleRecords.class).getModuleRecords();
    }

}
