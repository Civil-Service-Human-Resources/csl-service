package uk.gov.cabinetoffice.csl.domain.learningcatalogue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HyperlinkDto {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String title;

    @Size(max = 255)
    private String description;

    @NotBlank
    @Size(max = 255)
    @Pattern(regexp = "^https://.*", message = "href must be an HTTPS URL")
    private String href;
}
