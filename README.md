# csl-service

CSL Business logic API service which:

* Accepts requests from CSL UI
* Exposes secured endpoints (Endpoints are secured using oAuth2.0 token created using csl identity-service)
* Actuator is included to monitor the application health and other runtime parameters

### Build using:

* Java 17
* Spring Boot 3.0
* Maven 3.8.6
* Docker 4.15.0

### Running Locally:

The application requires Java 17 installed to build and run.

Run the application either using IDE e.g. Intellij or maven or docker as follows:

* Intellij: Use the default Run or Debug option
 
* Maven:
  * `` ./mvnw clean install ``
  * `` ./mvnw spring-boot:run ``
 
* Docker:
    * `` docker build -t csl-service-tag . ``
    * `` docker run -it --rm -p 9003:9003 csl-service-tag ``

### REST Endpoints:

* A postman collection is available at [docs/csl-service.postman_collection.json] for the following:
  
  * Generating oAuth2.0 token using identity-service
  * Actuator Endpoints
  * Test Endpoint
