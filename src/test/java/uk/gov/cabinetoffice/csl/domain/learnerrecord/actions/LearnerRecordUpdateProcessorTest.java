package uk.gov.cabinetoffice.csl.domain.learnerrecord.actions;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cabinetoffice.csl.service.LearnerRecordService;

@ExtendWith(MockitoExtension.class)
public class LearnerRecordUpdateProcessorTest {

    @Mock
    private LearnerRecordService learnerRecordService;

    @InjectMocks
    private LearnerRecordUpdateProcessor learnerRecordUpdateProcessor;


}
