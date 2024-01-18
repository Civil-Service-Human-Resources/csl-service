package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.*;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class BookingDtoFactoryTest {

    private BookingDtoFactory bookingDtoFactory;

    private final TestDataService testDataService = new TestDataService();

    @BeforeEach
    public void setup() {
        bookingDtoFactory = new BookingDtoFactory("catalogue_url",
                Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London")));
        reset();
    }

    @Test
    public void shouldCreateBookingForFreeEvent() {
        Course course = testDataService.generateCourse(true, true);
        Module module = course.getModules().stream().findFirst().get();
        module.setCost(BigDecimal.valueOf(0L));
        Event event = module.getEvents().stream().findFirst().get();
        CourseWithModuleWithEvent courseWithModuleWithEvent = new CourseWithModuleWithEvent(
                new CourseWithModule(course, module), event
        );
        BookEventDto bookEventDto = new BookEventDto();
        bookEventDto.setLearnerEmail("learner@domain.com");
        BookingDto dto = bookingDtoFactory.createBooking("learnerUID", courseWithModuleWithEvent, bookEventDto);

        assertEquals(dto.getEvent(), URI.create("catalogue_url/courses/courseId/modules/moduleId/events/eventId"));
        assertEquals(dto.getLearner(), "learnerUID");
        assertEquals(dto.getLearnerEmail(), "learner@domain.com");
        assertNull(dto.getAccessibilityOptions());
        assertEquals(dto.getStatus(), BookingStatus.CONFIRMED);
    }

    @Test
    public void shouldCreateBookingForPaidEvent() {
        Course course = testDataService.generateCourse(true, true);
        Module module = course.getModules().stream().findFirst().get();
        module.setCost(BigDecimal.valueOf(5L));
        Event event = module.getEvents().stream().findFirst().get();
        CourseWithModuleWithEvent courseWithModuleWithEvent = new CourseWithModuleWithEvent(
                new CourseWithModule(course, module), event
        );
        BookEventDto bookEventDto = new BookEventDto();
        bookEventDto.setAccessibilityOptions(List.of("accessibilityOption1", "accessibilityOption2"));
        bookEventDto.setLearnerEmail("learner@domain.com");
        BookingDto dto = bookingDtoFactory.createBooking("learnerUID", courseWithModuleWithEvent, bookEventDto);

        assertEquals(dto.getEvent(), URI.create("catalogue_url/courses/courseId/modules/moduleId/events/eventId"));
        assertEquals(dto.getLearner(), "learnerUID");
        assertEquals(dto.getLearnerEmail(), "learner@domain.com");
        assertEquals(dto.getAccessibilityOptions(), "accessibilityOption1,accessibilityOption2");
        assertEquals(dto.getStatus(), BookingStatus.REQUESTED);
    }

}
