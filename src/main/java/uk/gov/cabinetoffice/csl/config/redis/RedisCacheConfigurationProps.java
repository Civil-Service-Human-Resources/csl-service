package uk.gov.cabinetoffice.csl.config.redis;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RedisCacheConfigurationProps {
    private String name;
    private Integer ttl;
}
