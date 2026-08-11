package uk.gov.cabinetoffice.csl.domain.learningcatalogue.learningTag;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.cabinetoffice.csl.domain.taxonomy.BasicTaxonomyNode;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LearningTagTreeNode extends BasicTaxonomyNode {

    private boolean archived;

    public LearningTagTreeNode(String name, Long id, List<BasicTaxonomyNode> children, boolean archived) {
        super(name, id, children);
        this.archived = archived;
    }
}
