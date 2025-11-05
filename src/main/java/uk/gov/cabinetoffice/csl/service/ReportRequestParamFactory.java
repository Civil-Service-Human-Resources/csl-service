package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestWithSelectedOrganisationIdsParams;
import uk.gov.cabinetoffice.csl.controller.model.RegisteredLearnerReportRequestParams;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitService;
import uk.gov.cabinetoffice.csl.service.report.params.CourseCompletionReportRequestParams;
import uk.gov.cabinetoffice.csl.service.report.params.CreateRegisteredLearnerReportRequestParams;
import uk.gov.cabinetoffice.csl.service.report.params.IOrganisationalReportRequestParams;
import uk.gov.cabinetoffice.csl.service.report.params.ISelectedOrganisationalReportRequestParams;

import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportRequestParamFactory {

    private final OrganisationalUnitService organisationalUnitService;

    private <T extends IOrganisationalReportRequestParams> T fillParams(ISelectedOrganisationalReportRequestParams input, T output) {
        output.setUserId(input.getUserId());
        output.setUserEmail(input.getUserEmail());
        output.setDownloadBaseUrl(input.getDownloadBaseUrl());
        output.setFullName(input.getFullName());
        output.setTimezone(ZoneId.of(input.getTimezone()));
        if (input.getSelectedOrganisationIds() != null) {
            List<Long> organisationIds = organisationalUnitService.getOrganisationIdsWithChildrenAsFlatList(input.getSelectedOrganisationIds());
            output.setOrganisationIds(organisationIds);
        }
        return output;
    }

    public CourseCompletionReportRequestParams getCourseCompletionReportRequestParams(CreateReportRequestWithSelectedOrganisationIdsParams params) {
        CourseCompletionReportRequestParams createReportServiceReportRequestParams = new CourseCompletionReportRequestParams();
        createReportServiceReportRequestParams.setStartDate(params.getStartDate());
        createReportServiceReportRequestParams.setEndDate(params.getEndDate());
        createReportServiceReportRequestParams.setCourseIds(params.getCourseIds());
        createReportServiceReportRequestParams.setProfessionIds(params.getProfessionIds());
        createReportServiceReportRequestParams.setGradeIds(params.getGradeIds());
        return fillParams(params, createReportServiceReportRequestParams);
    }

    public CreateRegisteredLearnerReportRequestParams getRegisteredLearnerReportRequestParams(RegisteredLearnerReportRequestParams params) {
        return fillParams(params, new CreateRegisteredLearnerReportRequestParams());
    }
}
