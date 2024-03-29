package uk.gov.cabinetoffice.csl.util.stub;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.csrs.CivilServant;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Service
public class CSRSStubService {

    private final CslTestUtil utils;

    public CSRSStubService(CslTestUtil utils) {
        this.utils = utils;
    }

    public void getCivilServant(String uid, CivilServant response) {
        stubFor(
                WireMock.get(urlPathEqualTo("/csrs/civilServants/resource/" + uid + "/profile"))
                        .withHeader("Authorization", equalTo("Bearer fakeToken"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(utils.toJson(response)))
        );
    }

}
