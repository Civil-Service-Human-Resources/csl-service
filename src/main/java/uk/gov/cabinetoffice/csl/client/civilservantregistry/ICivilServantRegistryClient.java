package uk.gov.cabinetoffice.csl.client.civilservantregistry;

import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.domain.csrs.OrganisationalUnit;

import java.util.List;

public interface ICivilServantRegistryClient {

    CivilServant getCivilServantProfileWithUid(String uid);

    List<OrganisationalUnit> getAllOrganisationalUnits();
}
