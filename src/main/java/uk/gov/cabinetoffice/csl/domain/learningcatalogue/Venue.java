package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Venue implements Serializable {
    private String location;
    private String address;
    private Integer capacity;
    private Integer minCapacity;
    private Integer availability;

    public Venue(String location, String address, Integer capacity, Integer minCapacity) {
        this.location = location;
        this.address = address;
        this.capacity = capacity;
        this.minCapacity = minCapacity;
    }
}
