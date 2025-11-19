package uk.gov.cabinetoffice.csl.domain.learnerrecord.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.cabinetoffice.csl.controller.model.BookEventDto;
import uk.gov.cabinetoffice.csl.domain.User;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModule;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.CourseWithModuleWithEvent;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Module;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.event.Event;
import uk.gov.cabinetoffice.csl.service.user.UserDetailsService;
import uk.gov.cabinetoffice.csl.util.TestDataService;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("no-redis")
public class BookingDtoFactoryTest {

    private BookingDtoFactory bookingDtoFactory;
    private UserDetailsService userDetailsService = mock(UserDetailsService.class);

    private final TestDataService testDataService = new TestDataService();

    @BeforeEach
    public void setup() {
        bookingDtoFactory = new BookingDtoFactory("catalogue_url",
                Clock.fixed(Instant.parse("2023-01-01T10:00:00.000Z"), ZoneId.of("Europe/London")),
                userDetailsService);
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
        User user = testDataService.generateUser();
        when(userDetailsService.getUserWithUid("learnerUID")).thenReturn(user);
        BookEventDto bookEventDto = new BookEventDto();
        BookingDto dto = bookingDtoFactory.createBooking("learnerUID", courseWithModuleWithEvent, bookEventDto);

        assertEquals(dto.getEvent(), URI.create("catalogue_url/courses/courseId/modules/moduleId/events/eventId"));
        assertEquals("learnerUID", dto.getLearner());
        assertEquals("userEmail@email.com", dto.getLearnerEmail());
        assertEquals("", dto.getAccessibilityOptions());
        assertEquals(BookingStatus.CONFIRMED, dto.getStatus());
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
        User user = testDataService.generateUser();
        when(userDetailsService.getUserWithUid("learnerUID")).thenReturn(user);
        BookEventDto bookEventDto = new BookEventDto();
        bookEventDto.setAccessibilityOptions(List.of("accessibilityOption1", "accessibilityOption2"));
        BookingDto dto = bookingDtoFactory.createBooking("learnerUID", courseWithModuleWithEvent, bookEventDto);

        assertEquals(dto.getEvent(), URI.create("catalogue_url/courses/courseId/modules/moduleId/events/eventId"));
        assertEquals("learnerUID", dto.getLearner());
        assertEquals("userEmail@email.com", dto.getLearnerEmail());
        assertEquals("accessibilityOption1,accessibilityOption2", dto.getAccessibilityOptions());
        assertEquals(BookingStatus.REQUESTED, dto.getStatus());
    }

}
