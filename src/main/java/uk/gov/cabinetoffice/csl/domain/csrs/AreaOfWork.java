package uk.gov.cabinetoffice.csl.domain.csrs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AreaOfWork implements Serializable {
    private Long id;
    private String name;
    private List<AreaOfWork> children = new ArrayList<>();

    @JsonIgnore
    public List<AreaOfWork> getFlat() {
        List<AreaOfWork> flat = new ArrayList<>();
        flat.add(this);
        if (this.getChildren() != null && !this.getChildren().isEmpty()) {
            flat.addAll(this.getChildren().stream().flatMap(a -> a.getFlat().stream()).toList());
        }
        return flat;
    }

    public AreaOfWork(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
