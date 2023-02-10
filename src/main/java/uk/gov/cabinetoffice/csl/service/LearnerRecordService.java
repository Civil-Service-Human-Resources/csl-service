package uk.gov.cabinetoffice.csl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseRecordOutput;

import java.util.ArrayList;

@Service
public class LearnerRecordService {

    private final RequestEntityFactory requestEntityFactory;

    private final String courseRecordForLearnersUrl;

    public LearnerRecordService(RequestEntityFactory requestEntityFactory,
                                @Value("${learnerRecord.courseRecordForLearnersUrl}") String courseRecordForLearnersUrl) {
        this.requestEntityFactory = requestEntityFactory;
        this.courseRecordForLearnersUrl = courseRecordForLearnersUrl;
    }

    //TODO: Implement to invoke learner-record service
    public CourseRecordOutput getCourseRecordForLearner(String learnerId, String courseId) {
        return new CourseRecordOutput(new ArrayList<>());
    }
}
