package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Audience {
    public enum Type {
        OPEN,
        CLOSED_COURSE,
        PRIVATE_COURSE,
        REQUIRED_LEARNING
    }

    private List<String> areasOfWork;
    private List<String> departments;
    private List<String> grades;
    private String frequency;
    private Type type;

//    @JsonDeserialize(using = LocalDateDeserializer.class)
//    private LocalDate requiredBy;
//
//    public int getRelevance(CivilServant civilServant) {
//        int relevance = 0;
//        if (areasOfWork != null && areasOfWork.contains(civilServant.getProfession().getName())) {
//            relevance += 1;
//        }
//        if (departments != null && departments.contains(civilServant.getOrganisationalUnit().getCode())) {
//            relevance += 1;
//        }
//        if (grades != null && grades.contains(civilServant.getGrade().getCode())) {
//            relevance += 1;
//        }
//        return relevance;
//    }
//
//    public LocalDate getNextRequiredBy(LocalDate completionDate) {
//        LocalDate today = LocalDate.now();
//        if (requiredBy == null) {
//            return null;
//        }
//        if (frequency == null) {
//            if (requiredBy.isAfter(today) || requiredBy.isEqual(today)){
//               return requiredBy;
//            }
//            return null;
//        }
//        LocalDate nextRequiredBy = requiredBy;
//        while (nextRequiredBy.isBefore(today)) {
//            nextRequiredBy = increment(nextRequiredBy, frequency);
//        }
//        LocalDate lastRequiredBy = decrement(nextRequiredBy, frequency);
//
//        if (completionDate != null && completionDate.isAfter(lastRequiredBy)) {
//            return increment(nextRequiredBy, frequency);
//        }
//        return nextRequiredBy;
//    }
//
//    private LocalDate decrement(LocalDate dateTime, String frequency) {
//        Period period = Period.parse(frequency);
//        return dateTime
//            .minusYears(period.getYears())
//            .minusMonths(period.getMonths());
//    }
//
//    private LocalDate increment(LocalDate dateTime, String frequency) {
//        Period period = Period.parse(frequency);
//        return dateTime
//            .plusYears(period.getYears())
//            .plusMonths(period.getMonths());
//    }
}
