package uk.gov.cabinetoffice.csl.domain.reportservice;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReportType {
    COURSE_COMPLETIONS("course-completions"),
    REGISTERED_LEARNER("registered-learners");

    private final String url;

    ReportType(String url) {
        this.url = url;
    }

    public static ReportType getWithUrl(String url) {
        return Arrays.stream(ReportType.values()).filter(t -> t.url.equals(url)).findFirst().orElse(null);
    }
}
