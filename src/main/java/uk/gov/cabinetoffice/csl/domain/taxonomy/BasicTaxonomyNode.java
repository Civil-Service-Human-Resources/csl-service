package uk.gov.cabinetoffice.csl.domain.taxonomy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicTaxonomyNode {

    private String name;
    private Long id;
    private List<? extends BasicTaxonomyNode> children = new ArrayList<>();

    public BasicTaxonomyNode(String name, Long id) {
        this.name = name;
        this.id = id;
        this.children = new ArrayList<>();
    }
}
