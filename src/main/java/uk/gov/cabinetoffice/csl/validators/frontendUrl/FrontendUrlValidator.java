package uk.gov.cabinetoffice.csl.validators.frontendUrl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FrontendUrlValidator implements ConstraintValidator<ValidFrontendUrl, CharSequence> {

    private final List<String> validValues;

    public FrontendUrlValidator(@Value("${reportService.requestCourseCompletionReportValidBaseUrls}") List<String> validValues) {
        this.validValues = validValues;
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        for (String validUrl : validValues) {
            if (value.toString().startsWith(validUrl)) {
                return true;
            }
        }
        return false;
    }
}
