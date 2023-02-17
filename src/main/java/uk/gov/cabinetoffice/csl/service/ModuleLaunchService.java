package uk.gov.cabinetoffice.csl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.CourseRecord;
import uk.gov.cabinetoffice.csl.domain.CourseRecordInput;
import uk.gov.cabinetoffice.csl.domain.CourseRecords;

import static uk.gov.cabinetoffice.csl.util.CslServiceUtil.returnError;

@Slf4j
@Service
public class ModuleLaunchService {

    private final LearnerRecordService learnerRecordService;

    private final RusticiService rusticiService;

    public ModuleLaunchService(LearnerRecordService learnerRecordService, RusticiService rusticiService) {
        this.learnerRecordService = learnerRecordService;
        this.rusticiService = rusticiService;
    }

    public ResponseEntity<?> createLaunchLink(CourseRecordInput courseRecordInput) {
        String learnerId = courseRecordInput.getUserId();
        String courseId = courseRecordInput.getCourseId();

        //1. Fetch the course record from the learner-record-service
        ResponseEntity<?> courseRecordForLearnerResponse = learnerRecordService.getCourseRecordForLearner(learnerId, courseId);

        if(courseRecordForLearnerResponse.getStatusCode().is2xxSuccessful()) {
            CourseRecords courseRecords = (CourseRecords)courseRecordForLearnerResponse.getBody();
            if(courseRecords != null) {
                CourseRecord courseRecord = courseRecords.getCourseRecords()
                        .stream()
                        .filter(c -> c.getCourseId().equalsIgnoreCase(courseId))
                        .findFirst()
                        .orElse(null);
                if(courseRecord == null) {
                    //2. If the course record is not present then create the course record along with module record
                }
                //3. Retrieve the relevant module record from the course record
                //4. If the relevant module record is not present then create the module record
                //5. If the uid is not present in the module record then update the module record to assign the uid
                //6. Get the launchLink using module uid as registration id
                //7. If no launch link present then creat the registration and launch link using withLaunchLink

            } else {
                return returnError(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "Unable to retrieve course record for the learnerId: " + learnerId + " and courseId: "
                                + courseId,"/course-record", null);
            }
        }

        return courseRecordForLearnerResponse;
    }
}
