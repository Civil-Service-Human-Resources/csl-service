package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class GetGradesResponse {
    @JsonProperty("_embedded")
    @JsonDeserialize(using = GradeListDeserializer.class)
    private List<Grade> grades;
}
