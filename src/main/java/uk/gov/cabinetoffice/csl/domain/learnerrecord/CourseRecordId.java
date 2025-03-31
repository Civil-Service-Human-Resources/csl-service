package uk.gov.cabinetoffice.csl.domain.learnerrecord;

public record CourseRecordId(String learnerId, String courseId) {

    public String getAsString() {
        return String.format("%s,%s", learnerId, courseId);
    }

}
