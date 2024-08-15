package uk.gov.cabinetoffice.csl.domain.reportservice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CourseCompletionReportRequest {

    private Long reportRequestId;

    private String requesterId;

    private String requesterEmail;

    private ZonedDateTime requestedTimestamp;

    private ZonedDateTime completedTimestamp;

    private String status;

    private ZonedDateTime fromDate;

    private ZonedDateTime toDate;

    private List<String> courseIds;

    private List<Integer> organisationIds;

    private List<Integer> professionIds;

    private List<Integer> gradeIds;


}
