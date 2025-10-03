package uk.gov.cabinetoffice.csl.integration;

import org.junit.jupiter.api.BeforeEach;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.cabinetoffice.csl.configuration.TestConfig;
import uk.gov.cabinetoffice.csl.util.CSLServiceWireMockServer;
import uk.gov.cabinetoffice.csl.util.CslTestUtil;

import java.time.Instant;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"wiremock", "no-redis"})
@Import(TestConfig.class)
public class IntegrationTestBase extends CSLServiceWireMockServer {

    protected MockMvc mockMvc;

    @Autowired
    protected CslTestUtil utils;

    @Autowired
    protected WebApplicationContext context;

    @MockBean
    private RedissonClient redissonClient;

    @BeforeEach
    public void setup() {
        RLock mockLock = mock(RLock.class);
        when(redissonClient.getLock(anyString())).thenReturn(mockLock);
        doNothing().when(mockLock).lock();
        doNothing().when(mockLock).unlock();

        Jwt jwt = new Jwt("token", Instant.now(), Instant.MAX, Map.of("alg", "none"),
                Map.of(
                        JwtClaimNames.SUB, "userId",
                        "user_name", "userId"
                ));
        SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtPostProcessor = jwt()
                .jwt(jwt)
                .authorities(new SimpleGrantedAuthority("LEARNER"));
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(get("/").with(jwtPostProcessor))
                .defaultRequest(post("/").with(jwtPostProcessor))
                .defaultRequest(put("/").with(jwtPostProcessor))
                .alwaysDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .build();
    }
}
