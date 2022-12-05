FROM amazoncorretto:17

ENV SPRING_PROFILES_ACTIVE production

EXPOSE 9003

ADD lib/AI-Agent.xml /opt/appinsights/AI-Agent.xml

ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.4.4/applicationinsights-agent-3.4.4.jar /opt/appinsights/applicationinsights-agent-3.4.4.jar

ADD build/libs/csl-service.jar /data/app.jar

CMD java -javaagent:/opt/appinsights/applicationinsights-agent-3.4.4.jar -jar /data/app.jar
