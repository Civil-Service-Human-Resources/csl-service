package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.taxonomy.ITaxonomyItemDTO;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LearningTagDTO implements ITaxonomyItemDTO {

    @NotNull
    @Size(min = 1, max = 50)
    private String name;
    @NotNull
    @Size(min = 1, max = 50)
    private String code;
    @Size(min = 1, max = 255)
    private String description;
    private Long parentId;
    @Size(min = 1, max = 50)
    private String urlSlug;
    @NotNull
    private boolean isCategory;
    private boolean isArchived = false;

}
