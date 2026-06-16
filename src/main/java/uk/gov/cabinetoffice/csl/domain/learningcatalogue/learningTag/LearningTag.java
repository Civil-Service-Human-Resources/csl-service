package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.ITaxonomyItem;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LearningTag implements Serializable, ITaxonomyItem {

    private Long id;
    private String name;
    private String description;
    private String code;
    private String urlSlug;
    private boolean isCategoryTag;
    private boolean isArchived;
    private Long parentId;
    private String parentName;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
    private LocalDateTime archivedTimestamp;

    // Custom data
    private String formattedName;
    @JsonIgnore
    private Set<Long> childIds = new HashSet<>();
    @JsonIgnore
    private String fullUrl;

    @Override
    public void resetCustomData() {
        formattedName = null;
        parentName = null;
        childIds = new HashSet<>();
    }
}
