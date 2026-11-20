package uk.gov.cabinetoffice.csl.controller.admin;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import uk.gov.cabinetoffice.csl.controller.admin.model.AdminCourseOverview;
import uk.gov.cabinetoffice.csl.controller.admin.model.EventOverview;
import uk.gov.cabinetoffice.csl.service.admin.CourseManagementService;
import uk.gov.cabinetoffice.csl.service.admin.EventManagementService;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("admin/management")
public class AdminManagementController {

    private final EventManagementService eventManagementService;
    private final CourseManagementService courseManagementService;

    @ResponseBody
    @GetMapping(path = "/courses/{courseId}/modules/{moduleId}/events/{eventId}/overview", produces = "application/json")
    public EventOverview getEventOverview(@PathVariable("courseId") String courseId,
                                          @PathVariable("moduleId") String moduleId,
                                          @PathVariable("eventId") String eventId) {
        return eventManagementService.getOverview(courseId, moduleId, eventId);
    }

    @ResponseBody
    @GetMapping("/courses/{courseId}/overview")
    public AdminCourseOverview getAdminCourseOverview(@PathVariable("courseId") String courseId) {
        return courseManagementService.getOverview(courseId);
    }

}
