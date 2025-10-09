package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
public class UpdateDomainResponse {
    private Long primaryOrganisationId;
    private Domain domain;
    private List<Long> updatedChildOrganisationIds;
    private List<Long> skippedChildOrganisationIds;

    @JsonIgnore
    public Collection<Long> getAllUpdatedIds() {
        return Stream.concat(updatedChildOrganisationIds.stream(), Stream.of(primaryOrganisationId)).toList();
    }
}
