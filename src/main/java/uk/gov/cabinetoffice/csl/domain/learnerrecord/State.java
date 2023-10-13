package uk.gov.cabinetoffice.csl.domain.learnerrecord;

import java.io.Serializable;

public enum State implements Serializable {
    APPROVED,
    ARCHIVED,
    COMPLETED,
    IN_PROGRESS,
    REGISTERED,
    SKIPPED,
    UNREGISTERED,
    NULL
}
