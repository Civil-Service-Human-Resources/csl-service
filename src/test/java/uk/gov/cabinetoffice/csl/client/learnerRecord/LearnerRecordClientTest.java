package uk.gov.cabinetoffice.csl.client.learnerRecord;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.client.IHttpClient;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecord;
import uk.gov.cabinetoffice.csl.domain.learnerrecord.ModuleRecords;
import uk.gov.cabinetoffice.csl.util.UtilService;

import java.time.Clock;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LearnerRecordClientTest {

    private final IHttpClient iHttpClient = mock(IHttpClient.class);
    private final LearnerRecordClientConfigParams configParams = mock(LearnerRecordClientConfigParams.class);
    private final UtilService utilService = new UtilService(mock(Clock.class));

    private final LearnerRecordClient learnerRecordClient = new LearnerRecordClient(iHttpClient, configParams,
            null, utilService);

    @Test
    public void testGetModuleRecordsBulk() {
        Set<String> moduleIds = IntStream.range(0, 10).mapToObj(i -> "moduleId" + i).collect(Collectors.toSet());
        when(configParams.getModuleRecordBatchSize()).thenReturn(5);
        List<ModuleRecord> moduleRecords = List.of(
                new ModuleRecord(),
                new ModuleRecord(),
                new ModuleRecord()
        );
        when(iHttpClient.executeRequest(any(), any())).thenReturn(new ModuleRecords(moduleRecords));
        GetModuleRecordParams params = GetModuleRecordParams.builder().userIds(Set.of("user1")).moduleIds(moduleIds).build();
        List<ModuleRecord> moduleRecordsResponse = learnerRecordClient.getModuleRecords(params);
        assertEquals(6, moduleRecordsResponse.size());
        verify(iHttpClient, atLeast(2)).executeRequest(any(), any());
    }

}
