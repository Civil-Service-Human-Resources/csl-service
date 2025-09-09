package uk.gov.cabinetoffice.csl.domain.reportservice.reportRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CourseCompletionReportRequest extends OrganisationalReportRequest {

    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private List<String> courseIds;
    private List<Integer> professionIds;
    private List<Integer> gradeIds;
    private String requesterTimezone;
    private Boolean detailedExport;
    
}
