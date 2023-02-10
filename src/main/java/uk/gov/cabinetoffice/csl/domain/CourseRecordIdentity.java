package uk.gov.cabinetoffice.csl.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseRecordIdentity {

    private String courseId;

    private String userId;

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
