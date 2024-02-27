package uk.gov.cabinetoffice.csl.service.messaging.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public interface IMessageMetadata extends Serializable {
    @JsonIgnore
    String getQueue();
}
