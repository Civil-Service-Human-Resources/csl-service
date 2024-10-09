package uk.gov.cabinetoffice.csl.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import uk.gov.cabinetoffice.csl.domain.reportservice.AggregationBinDelimiter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCourseCompletionsParams {
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    protected LocalDateTime startDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    protected LocalDateTime endDate;

    @NotNull
    protected ZoneId timezone;

    public String getTimezone() {
        return timezone.toString();
    }

    @Size(min = 1, max = 30)
    @NotNull
    protected List<String> courseIds;

    @Size(min = 1)
    @NotNull
    protected List<String> organisationIds;

    protected List<String> professionIds;

    protected List<String> gradeIds;

    @JsonIgnore
    public ZonedDateTime getStartDateZoned() {
        return this.startDate.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(this.timezone);
    }

    @JsonIgnore
    public ZonedDateTime getEndDateZoned() {
        return this.endDate.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(this.timezone);
    }

    @JsonIgnore
    public AggregationBinDelimiter getBinDelimiterVal() {
        long dayDiff = DAYS.between(startDate, endDate);
        if (dayDiff <= 1) {
            return AggregationBinDelimiter.HOUR;
        } else if (dayDiff <= 31) {
            return AggregationBinDelimiter.DAY;
        } else {
            return AggregationBinDelimiter.MONTH;
        }
    }

    @JsonProperty("binDelimiter")
    public AggregationBinDelimiter getBinDelimiter() {
        return this.getBinDelimiterVal();
    }

}
