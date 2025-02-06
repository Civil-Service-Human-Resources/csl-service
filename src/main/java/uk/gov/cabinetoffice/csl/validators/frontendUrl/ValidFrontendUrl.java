package uk.gov.cabinetoffice.csl.validators.frontendUrl;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = FrontendUrlValidator.class)
public @interface ValidFrontendUrl {

    String message() default "Must be a valid frontend URL";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
