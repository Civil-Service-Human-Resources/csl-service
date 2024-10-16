package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Audience implements Serializable {
    public enum Type {
        OPEN,
        CLOSED_COURSE,
        PRIVATE_COURSE,
        REQUIRED_LEARNING,
        NULL
    }

    private String name;
    private List<String> areasOfWork;
    private List<String> departments;
    private List<String> grades;
    private String frequency;
    private Type type;
    private LocalDate requiredBy;
    private LearningPeriod learningPeriod;

    public Type getType() {
        return Optional.ofNullable(this.type).orElse(Type.NULL);
    }

    @JsonIgnore
    public boolean isRequiredForDepartments() {
        return this.isRequired() && !getDepartments().isEmpty();
    }

    @JsonIgnore
    public boolean isRequired() {
        return (getType().equals(Audience.Type.REQUIRED_LEARNING) && getRequiredBy() != null);
    }

    @JsonIgnore
    public String getFrequencyAsString() {
        return getFrequencyAsPeriod().map(f -> String.format("%s years, %s months", f.getYears(), f.getMonths()))
                .orElse("None");
    }

    @JsonIgnore
    public Optional<Period> getFrequencyAsPeriod() {
        if (!StringUtils.isBlank(frequency)) {
            return Optional.of(Period.parse(frequency));
        }
        return Optional.empty();
    }

}
