package uk.gov.cabinetoffice.csl.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationBinDelimiter;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCourseCompletionsParams {
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

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
