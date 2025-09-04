package uk.gov.cabinetoffice.csl.domain.reportservice.reportRequest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReportRequest {

    private Long reportRequestId;

    private String requesterId;

    private String requesterEmail;

    private LocalDateTime requestedTimestamp;

    private LocalDateTime completedTimestamp;

    private String status;

    private String fullName;

    private String urlSlug;

    private String downloadBaseUrl;

    private Integer timesDownloaded;
}
