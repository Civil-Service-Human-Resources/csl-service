package uk.gov.cabinetoffice.csl.client.learnerRecord;

import uk.gov.cabinetoffice.csl.domain.learnerrecord.ID.ModuleRecordResourceId;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.event.EventStatusDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.record.*;

import java.util.List;

public interface ILearnerRecordClient {

    BookingDto bookEvent(String eventId, BookingDto booking);

    BookingDto updateBooking(String userId, String eventId, BookingDto cancellationDto);

    BookingDto updateBookingWithId(String eventId, String bookingId, BookingDto bookingDto);

    EventDto updateEvent(String eventId, EventStatusDto dto);

    List<BookingDto> getBookings(String eventId);

    List<LearnerRecord> getLearnerRecords(LearnerRecordQuery query);

    List<LearnerRecord> createLearnerRecords(List<LearnerRecordDto> newLearnerRecords);

    List<LearnerRecordEvent> createLearnerRecordEvents(List<LearnerRecordEventDto> newLearnerRecordEvents);

    List<ModuleRecord> createModuleRecords(List<ModuleRecord> newRecords);

    List<ModuleRecord> getModuleRecords(List<ModuleRecordResourceId> missingModuleRecordIds);

    List<ModuleRecord> updateModuleRecords(List<ModuleRecord> input);
}
