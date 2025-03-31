package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingCancellationReason;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDto;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.booking.BookingDtoFactory;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;

import java.util.List;


@Service
@Slf4j
public class BookingService {

    private final BookingDtoFactory dtoFactory;
    private final ILearnerRecordClient learnerRecordClient;

    public BookingService(BookingDtoFactory dtoFactory, ILearnerRecordClient learnerRecordClient) {
        this.dtoFactory = dtoFactory;
        this.learnerRecordClient = learnerRecordClient;
    }

    public void createBooking(String learnerUid, CourseWithModuleWithEvent courseWithModuleWithEvent,
                              BookEventDto dto) {
        BookingDto booking = dtoFactory.createBooking(learnerUid, courseWithModuleWithEvent, dto);
        learnerRecordClient.bookEvent(courseWithModuleWithEvent.getEvent().getId(), booking);
    }

    public BookingDto approveBookingWithId(String eventId, String bookingId) {
        BookingDto dto = dtoFactory.createApprovedBooking();
        return learnerRecordClient.updateBookingWithId(eventId, bookingId, dto);
    }

    public void cancelBooking(String learnerUid, String eventId, BookingCancellationReason reason) {
        BookingDto cancellationDto = dtoFactory.createCancellation(reason);
        learnerRecordClient.updateBooking(learnerUid, eventId, cancellationDto);
    }

    public BookingDto cancelBookingWithId(String eventId, String bookingId, BookingCancellationReason reason) {
        BookingDto cancellationDto = dtoFactory.createCancellation(reason);
        return learnerRecordClient.updateBookingWithId(eventId, bookingId, cancellationDto);
    }

    public List<BookingDto> getBookings(String eventId) {
        return learnerRecordClient.getBookings(eventId);
    }
}
