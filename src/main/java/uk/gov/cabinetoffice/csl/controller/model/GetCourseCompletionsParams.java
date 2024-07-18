package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationBinDelimiter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCourseCompletionsParams {
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;

    @NotNull
    private ZoneId timezone;

    public String getTimezone() {
        return timezone.toString();
    }

    @Size(min = 1, max = 30)
    @NotNull
    private List<String> courseIds;

    @Size(min = 1)
    @NotNull
    private List<String> organisationIds;

    private List<String> professionIds;

    private List<String> gradeIds;

    private AggregationBinDelimiter binDelimiter = AggregationBinDelimiter.DAY;

}
