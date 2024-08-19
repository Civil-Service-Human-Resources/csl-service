package uk.gov.cabinetoffice.csl.client.learnerRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;

import java.util.List;

@Slf4j
@Component
public class LearnerRecordClient implements ILearnerRecordClient {

    @Value("${learnerRecord.courseRecordsForLearnerUrl}")
    private String courseRecords;
    @Value("${learnerRecord.moduleRecordsForLearnerUrl}")
    private String moduleRecords;
    @Value("${learnerRecord.eventsUrl}")
    private String event;
    @Value("${learnerRecord.bookingsUrl}")
    private String booking;

    private final IHttpClient httpClient;

    public LearnerRecordClient(@Qualifier("learnerRecordHttpClient") IHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public CourseRecord getCourseRecord(String userId, String courseId) {
        List<CourseRecord> courseRecords = getCourseRecords(userId, List.of(courseId));
        if (courseRecords.size() == 0) {
            log.warn("Course record with userID '{}' and courseId '{}' was not found.", userId, courseId);
            return null;
        } else {
            return courseRecords.get(0);
        }
    }

    @Override
    public List<CourseRecord> getCourseRecords(String userId, List<String> courseIds) {
        log.debug("Getting course records with ids '{}' for user '{}'", courseIds, userId);
        String courseIdList = String.join(",", courseIds);
        String url = String.format("%s?userId=%s&courseIds=%s", courseRecords, userId, courseIdList);
        RequestEntity<Void> request = RequestEntity.get(url).build();
        CourseRecords courseRecords = httpClient.executeRequest(request, CourseRecords.class);
        return courseRecords.getCourseRecords();
    }

    @Override
    public CourseRecord createCourseRecord(CourseRecord body) {
        log.debug("Creating course record '{}'", body);
        RequestEntity<CourseRecord> request = RequestEntity.post(courseRecords).body(body);
        return httpClient.executeRequest(request, CourseRecord.class);
    }

    @Override
    public CourseRecord updateCourseRecord(CourseRecord body) {
        log.debug("Updating course record '{}'", body);
        RequestEntity<CourseRecord> request = RequestEntity.put(courseRecords).body(body);
        return httpClient.executeRequest(request, CourseRecord.class);
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
}
