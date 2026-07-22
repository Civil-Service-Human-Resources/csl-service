package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BulkLearningTagStateDto {

    private Collection<Long> ids;
    private String state;

}
