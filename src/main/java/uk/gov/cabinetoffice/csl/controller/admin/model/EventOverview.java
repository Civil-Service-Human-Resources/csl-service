package uk.gov.cabinetoffice.csl.controller.admin.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Venue;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventOverview {

    private String id;
    private Venue venue;
    private Collection<String> dates;
    private String status;
    private String cancellationReason;

    private String moduleId;
    private String moduleTitle;

    private String courseId;
    private String courseTitle;
    private String courseStatus;

    private Collection<String> invitedEmails;

    private Collection<BookingOverview> bookings;
}
