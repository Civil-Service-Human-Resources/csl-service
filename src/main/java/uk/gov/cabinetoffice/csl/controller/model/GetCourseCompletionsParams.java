package uk.gov.cabinetoffice.csl.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationBinDelimiter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
public class GetCourseCompletionsParams {
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Size(min = 1, max = 10)
    @NotNull
    private List<String> courseIds;

    @Size(min = 1)
    @NotNull
    private List<String> organisationIds;

    private List<String> professionIds;

    private List<String> gradeIds;

    private AggregationBinDelimiter binDelimiter = AggregationBinDelimiter.DAY;

    public GetCourseCompletionsParams() {
    }

    @JsonIgnore
    public String getProfessionIdsAsString() {
        return Optional.ofNullable(professionIds).map(p -> String.join(",", p)).orElse("");
    }

    @JsonIgnore
    public String getGradeIdsAsString() {
        return Optional.ofNullable(gradeIds).map(g -> String.join(",", g)).orElse("");
    }

    @JsonIgnore
    public String getCourseIdsAsString() {
        return String.join(",", courseIds);
    }

    @JsonIgnore
    public String getOrganisationIdsAsString() {
        return String.join(",", organisationIds);
    }

}
