package uk.gov.cabinetoffice.csl.domain;

public class CourseRecordIdentity {

    private String courseId;

    private String userId;

    public CourseRecordIdentity() {
    }

    public CourseRecordIdentity(String courseId, String userId) {
        this.courseId = courseId;
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CourseRecordIdentity that = (CourseRecordIdentity) o;

        if (!courseId.equals(that.courseId)) return false;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        int result = courseId.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }
}
