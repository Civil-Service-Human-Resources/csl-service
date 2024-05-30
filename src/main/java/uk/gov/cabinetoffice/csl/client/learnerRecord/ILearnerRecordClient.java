package uk.gov.cabinetoffice.csl.client.learnerRecord;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.CourseRecords;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;

import java.util.List;

public interface ILearnerRecordClient {

    CourseRecords getCourseRecord(String userId, String courseId);

    CourseRecords getCourseRecords(String userId, List<String> courseId);

    CourseRecord createCourseRecord(CourseRecord body);

    CourseRecord updateCourseRecord(CourseRecord input);

    BookingDto bookEvent(String eventId, BookingDto booking);

    BookingDto updateBooking(String userId, String eventId, BookingDto cancellationDto);

    BookingDto updateBookingWithId(String eventId, String bookingId, BookingDto bookingDto);
}
