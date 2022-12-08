package uk.gov.cabinetoffice.csl.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties implements Serializable {

    private String serviceUrl;

    private String clientId;

    private String clientSecret;

    private String tokenUrl;

    private String checkTokenUrl;

    private int maxTotalConnections;

    private int defaultMaxConnectionsPerRoute;

    private int maxPerServiceUrl;

    private String jwtKey;
}
