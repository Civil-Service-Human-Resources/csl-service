package uk.gov.cabinetoffice.csl.client.learnerRecord;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.*;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;

import java.util.List;

public interface ILearnerRecordClient {

    CourseRecords getCourseRecord(String userId, String courseId);

    CourseRecords getCourseRecords(String userId, List<String> courseId);

    CourseRecord createCourseRecord(CourseRecordInput body);

    ModuleRecord createModuleRecord(ModuleRecordInput body);

    CourseRecord updateCourseRecord(String userId, String courseId, List<PatchOp> patches);

    ModuleRecord updateModuleRecord(Long moduleRecordId, List<PatchOp> patches);

    BookingDto bookEvent(String eventId, BookingDto booking);

    BookingDto updateBooking(String userId, String eventId, BookingDto cancellationDto);

    BookingDto updateBooking(String bookingId, BookingDto bookingDto);
}
