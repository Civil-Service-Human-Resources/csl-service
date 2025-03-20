package uk.gov.cabinetoffice.csl.service.chart;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChartFactoryService {

    private final Map<CourseCompletionChartType, CourseCompletionChartFactoryBase> factories;

    public ChartFactoryService(List<CourseCompletionChartFactoryBase> factories) {
        this.factories = new HashMap<>();
        factories.forEach(f -> this.factories.put(f.getType(), f));
    }

    public CourseCompletionChartFactoryBase getFactory(CourseCompletionChartType type) {
        CourseCompletionChartFactoryBase factory = this.factories.get(type);
        if (factory == null) {
            throw new GenericServerException(String.format("No factory class implemented for type %s", type));
        }
        return factory;
    }
}
