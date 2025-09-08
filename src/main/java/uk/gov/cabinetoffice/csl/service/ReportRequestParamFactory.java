package uk.gov.cabinetoffice.csl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.controller.model.CreateReportRequestWithSelectedOrganisationIdsParams;
import uk.gov.cabinetoffice.csl.controller.model.RegisteredLearnerReportRequestParams;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;
import uk.gov.cabinetoffice.csl.service.csrs.OrganisationalUnitListService;
import uk.gov.cabinetoffice.csl.service.report.params.CourseCompletionReportRequestParams;
import uk.gov.cabinetoffice.csl.service.report.params.CreateRegisteredLearnerReportRequestParams;
import uk.gov.cabinetoffice.csl.service.report.params.IOrganisationalReportRequestParams;
import uk.gov.cabinetoffice.csl.service.report.params.ISelectedOrganisationalReportRequestParams;

import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReportRequestParamFactory {

    private final OrganisationalUnitListService organisationalUnitService;

    private <T extends IOrganisationalReportRequestParams> T fillParams(ISelectedOrganisationalReportRequestParams input, T output) {
        output.setUserId(input.getUserId());
        output.setUserEmail(input.getUserEmail());
        output.setDownloadBaseUrl(input.getDownloadBaseUrl());
        output.setFullName(input.getFullName());
        if (input.getSelectedOrganisationIds() != null) {
            List<OrganisationalUnit> organisations = organisationalUnitService.getOrganisationsWithChildrenAsFlatList(input.getSelectedOrganisationIds());
            output.setOrganisationIds(organisations.stream().map(OrganisationalUnit::getId).toList());
        }
        return output;
    }

    public CourseCompletionReportRequestParams getCourseCompletionReportRequestParams(CreateReportRequestWithSelectedOrganisationIdsParams params) {
        CourseCompletionReportRequestParams createReportServiceReportRequestParams = new CourseCompletionReportRequestParams();
        createReportServiceReportRequestParams.setStartDate(params.getStartDate());
        createReportServiceReportRequestParams.setEndDate(params.getEndDate());
        createReportServiceReportRequestParams.setTimezone(ZoneId.of(params.getTimezone()));
        createReportServiceReportRequestParams.setCourseIds(params.getCourseIds());
        createReportServiceReportRequestParams.setProfessionIds(params.getProfessionIds());
        createReportServiceReportRequestParams.setGradeIds(params.getGradeIds());
        return fillParams(params, createReportServiceReportRequestParams);
    }

    public CreateRegisteredLearnerReportRequestParams getRegisteredLearnerReportRequestParams(RegisteredLearnerReportRequestParams params) {
        return fillParams(params, new CreateRegisteredLearnerReportRequestParams());
    }
}
