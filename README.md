[![Build Status](https://travis-ci.org/xm-online/xm-ms-timeline.svg?branch=master)](https://travis-ci.org/xm-online/xm-ms-timeline) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?&metric=sqale_index&branch=master&project=xm-online:xm-ms-timeline)](https://sonarcloud.io/dashboard/index/xm-online:xm-ms-timeline) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?&metric=ncloc&branch=master&project=xm-online:xm-ms-timeline)](https://sonarcloud.io/dashboard/index/xm-online:xm-ms-timeline) [![Quality Gate](https://sonarcloud.io/api/project_badges/measure?&metric=coverage&branch=master&project=xm-online:xm-ms-timeline)](https://sonarcloud.io/dashboard/index/xm-online:xm-ms-timeline)

# timeline

This application was generated using JHipster 7.8.1, you can find documentation and help at [https://www.jhipster.tech](https://www.jhipster.tech).

This is a "microservice" application intended to be part of a microservice architecture, please refer to the [Doing microservices with JHipster][] page of the documentation for more information.

## Development

To start your application in the dev profile, simply run:

    ./gradlew

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

## Enable RDBMS mode
Open **application-<profile>.yml** config and add following configuration:

1. Specify timeline service type. Possible values: logger, rdbms
```
application:
    timeline-service-impl: rdbms
```

2. Enable automatic repositories configuration
```
spring:
    data:
        jpa:
         repositories:
            enabled: true    
```

3. Add datasource configuration
```
spring:
    jpa:
        database-platform: io.github.jhipster.domain.util.FixedPostgreSQL82Dialect
        database: POSTGRESQL
        show-sql: false
        properties:
            hibernate.id.new_generator_mappings: true
            hibernate.cache.use_second_level_cache: false
            hibernate.cache.use_query_cache: false
            hibernate.generate_statistics: false
            hibernate.cache.use_minimal_puts: true
    datasource:
        type: com.zaxxer.hikari.HikariDataSource
        url: jdbc:postgresql:timeline
        driver-class-name: org.postgresql.Driver
        username: postgres
        password: postgres
```

4. Enable liquibase
```
spring:
    liquibase:
        enabled: true
```

5. Enable health check
```
management:
    health:
        db:
         enabled: true
```

Open **application.yml** and comments following configs
```
spring:
    autoconfigure:
        exclude: org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration, org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration, org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration
```


## Building for production

### Packaging as jar

To build the final jar and optimize the timeline application for production, run:

    ./gradlew -Pprod clean bootJar

To ensure everything worked, run:

    java -jar build/libs/*.jar

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

    ./gradlew -Pprod -Pwar clean bootWar

## Testing

To launch your application's tests, run:

    ./gradlew test integrationTest jacocoTestReport

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the gradle plugin.

Then, run a Sonar analysis:

```
./gradlew -Pprod clean test sonarqube
```

For more information, refer to the [Code quality page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:
```
    ./gradlew bootWar -Pprod jibDockerBuild
```
Then run:
```
    docker-compose -f src/main/docker/app.yml up -d
```
For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 7.8.1 archive]: https://www.jhipster.tech
[doing microservices with jhipster]: https://www.jhipster.tech/microservices-architecture/
[using jhipster in development]: https://www.jhipster.tech/development/
[service discovery and configuration with consul]: https://www.jhipster.tech/microservices-architecture/#consul
[using docker and docker-compose]: https://www.jhipster.tech/docker-compose
[using jhipster in production]: https://www.jhipster.tech/production/
[running tests page]: https://www.jhipster.tech/running-tests/
[code quality page]: https://www.jhipster.tech/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/setting-up-ci/
[node.js]: https://nodejs.org/
[npm]: https://www.npmjs.com/
[openapi-generator]: https://openapi-generator.tech
[swagger-editor]: https://editor.swagger.io
[doing api-first development]: https://www.jhipster.tech/doing-api-first-development/
