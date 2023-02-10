package uk.gov.cabinetoffice.csl.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseRecordOutput;

import java.util.ArrayList;

@Service
public class LearnerRecordService {

    private final RequestEntityFactory requestEntityFactory;

    private final String courseRecordsForLearnerUrl;

    public LearnerRecordService(RequestEntityFactory requestEntityFactory,
                                @Value("${learnerRecord.courseRecordsForLearnerUrl}") String courseRecordsForLearnerUrl) {
        this.requestEntityFactory = requestEntityFactory;
        this.courseRecordsForLearnerUrl = courseRecordsForLearnerUrl;
    }

    //TODO: Implement to invoke learner-record service
    public CourseRecordOutput getCourseRecordForLearner(String learnerId, String courseId) {
        return new CourseRecordOutput(new ArrayList<>());
    }
}
