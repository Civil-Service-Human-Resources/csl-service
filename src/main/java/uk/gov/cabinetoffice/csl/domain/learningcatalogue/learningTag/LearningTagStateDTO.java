package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.annotations.ValidEnum;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningTagStateDTO {

    @ValidEnum(enumClass = LearningTagStateUpdate.class)
    private LearningTagStateUpdate state;

}
