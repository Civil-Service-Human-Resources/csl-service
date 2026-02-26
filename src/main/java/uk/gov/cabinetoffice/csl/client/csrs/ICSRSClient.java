package uk.gov.cabinetoffice.csl.client.csrs;

import uk.gov.cabinetoffice.csl.client.identity.UidRequest;
import uk.gov.cabinetoffice.csl.controller.csrs.model.CreateDomainDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.DeleteDomainDto;
import uk.gov.cabinetoffice.csl.controller.csrs.model.OrganisationalUnitDto;
import uk.gov.cabinetoffice.csl.domain.csrs.*;
import uk.gov.cabinetoffice.csl.domain.csrs.record.CivilServantSkillsMetadataPagedResponse;
import uk.gov.cabinetoffice.csl.util.IFetchClient;

import java.time.LocalDateTime;
import java.util.List;

public interface ICSRSClient extends IFetchClient<OrganisationalUnitMap> {

    CivilServant getCivilServantProfileWithUid(String uid);

    List<AreaOfWork> getAreasOfWork();

    List<Grade> getGrades();

    void patchCivilServant(PatchCivilServantDto patch);

    void patchCivilServantOrganisation(OrganisationalUnitIdDto organisationalUnitIdDTO);

    void deleteOrganisationalUnit(Long organisationalUnitId);

    UpdateDomainResponse addDomainToOrganisation(Long organisationalUnitId, CreateDomainDto domain);

    UpdateDomainResponse deleteDomain(Long organisationUnitId, Long domainId, DeleteDomainDto dto);

    OrganisationalUnit patchOrganisationalUnit(Long organisationalUnitId, OrganisationalUnitDto organisationalUnitDto);

    AgencyToken createAgencyToken(Long organisationUnitId, AgencyToken agencyTokenDto);

    AgencyToken updateAgencyToken(Long organisationUnitId, AgencyToken agencyToken);

    void deleteAgencyToken(Long organisationalUnitId);

    OrganisationalUnit createOrganisationalUnit(OrganisationalUnitDto organisationalUnitDto);

    CivilServantSkillsMetadataPagedResponse getSkillsCivilServants(Integer size, LocalDateTime syncTimestampLte);

    void syncSkillsMetadata(UidRequest request);
}
