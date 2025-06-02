package uk.gov.cabinetoffice.csl.domain.learnerrecord.ID;

public interface ILearnerRecordResourceID {

    String getResourceId();

    String getLearnerId();

    String getAsString();

    default boolean equals(ILearnerRecordResourceID id) {
        return id.getAsString().equals(this.getAsString());
    }

}
