package uk.gov.cabinetoffice.csl.service.chart;

import org.springframework.stereotype.Service;
import uk.gov.cabinetoffice.csl.domain.error.GenericServerException;
import uk.gov.cabinetoffice.csl.domain.reportservice.aggregation.IAggregation;
import uk.gov.cabinetoffice.csl.service.chart.factory.CourseCompletionChartFactoryBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChartFactoryService {

    private final Map<CourseCompletionChartType, CourseCompletionChartFactoryBase<? extends IAggregation>> factories;

    public ChartFactoryService(List<CourseCompletionChartFactoryBase<? extends IAggregation>> factories) {
        this.factories = new HashMap<>();
        factories.forEach(f -> this.factories.put(f.getType(), f));
    }

    public CourseCompletionChartFactoryBase<? extends IAggregation> getFactory(CourseCompletionChartType type) {
        CourseCompletionChartFactoryBase<? extends IAggregation> factory = this.factories.get(type);
        if (factory == null) {
            throw new GenericServerException(String.format("No factory class implemented for type %s", type));
        }
        return factory;
    }
}
