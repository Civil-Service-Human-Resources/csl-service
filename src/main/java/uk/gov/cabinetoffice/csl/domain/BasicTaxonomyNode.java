package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicTaxonomyNode {

    private String name;
    private Long id;
    private List<BasicTaxonomyNode> children;

}
