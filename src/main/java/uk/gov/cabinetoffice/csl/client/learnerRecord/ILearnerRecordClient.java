package uk.gov.cabinetoffice.csl.client.learnerRecord;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecordId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventStatusDto;

import java.util.List;

public interface ILearnerRecordClient {

    List<CourseRecord> getCourseRecordsForUser(String userId);

    List<CourseRecord> getCourseRecords(List<CourseRecordId> courseRecordIds);

    List<CourseRecord> createCourseRecords(List<CourseRecord> body);

    List<CourseRecord> updateCourseRecords(List<CourseRecord> input);

    BookingDto bookEvent(String eventId, BookingDto booking);

    BookingDto updateBooking(String userId, String eventId, BookingDto cancellationDto);

    BookingDto updateBookingWithId(String eventId, String bookingId, BookingDto bookingDto);

    EventDto updateEvent(String eventId, EventStatusDto dto);

    List<BookingDto> getBookings(String eventId);
}
