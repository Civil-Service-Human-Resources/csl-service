package uk.gov.cabinetoffice.csl.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.gov.cabinetoffice.csl.domain.learningcatalogue.Course;
import uk.gov.cabinetoffice.csl.util.TestDataService;
import uk.gov.cabinetoffice.csl.util.stub.CSLStubService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class AdminInviteLearnerTest extends IntegrationTestBase {

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CSLStubService cslStubService;

    @Test
    public void testAdminInviteLearner() throws Exception {
        Course course = testDataService.generateCourse(true, true);
        String userEmail = testDataService.getUserEmail();
        String emailDto = String.format("""
                {
                    "learnerEmail": "%s"
                }
                """, userEmail);
        String identityDtoResponse = """
                {
                    "uid": "inviteUserUid",
                    "roles": ["LEARNER"]
                }
                """;
        cslStubService.getIdentityAPIServiceStubService().getIdentityWithEmail(userEmail, identityDtoResponse);
        cslStubService.getLearningCatalogue().getCourse(course);
        cslStubService.getNotificationServiceStubService().sendEmail("INVITE_LEARNER",
                String.format("""
                        {
                            "recipient": "%s",
                            "personalisation": {
                                "learnerName": "%s",
                                "courseTitle": "Test Course",
                                "courseDate": "01 Jan 2023",
                                "courseLocation": "London",
                                "inviteLink": "http://localhost:9000/lpg_ui/book/courseId/moduleId/choose-date"
                            }
                        }
                        """, userEmail, userEmail));
        String expectedInviteDto = String.format("""
                {
                    "learnerEmail": "%s",
                    "learnerUid": "inviteUserUid"
                }
                """, userEmail);
        cslStubService.getLearnerRecord().createInvite(testDataService.getEventId(), expectedInviteDto);
        String url = String.format("/admin/courses/%s/modules/%s/events/%s/invite", course.getId(), testDataService.getModuleId(), testDataService.getEventId());
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailDto))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testAdminInviteLearnerNotFound() throws Exception {
        String userEmail = testDataService.getUserEmail();
        String emailDto = String.format("""
                {
                    "learnerEmail": "%s"
                }
                """, userEmail);
        cslStubService.getIdentityAPIServiceStubService().getIdentityWithEmailNotFound(userEmail);
        String url = String.format("/admin/courses/%s/modules/%s/events/%s/invite", testDataService.getCourseId(), testDataService.getModuleId(), testDataService.getEventId());
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailDto))
                .andExpect(status().isNotFound());
    }

}
