# Elasticsearch Appender

Elasticsearch Appender is a library for Java / Spring Boot applications that automatically ships application logs to Elasticsearch.

## Compatibility

- ✅ Supports **Elasticsearch 8.x**.

## Installation

### Using Maven

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.amirankhazaradze</groupId>
    <artifactId>elasticsearch-appender</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration Parameters

Below are the configuration parameters for Elasticsearch Appender and their descriptions:

- **management.health.elasticsearch.enabled**: Enables/disables the Elasticsearch health check. Example: `true` or `false`.
- **elasticsearch.appender.elastic.create-index**: Creates the data stream in Elasticsearch if it does not exist. Example: `true` or `false`.
- **elasticsearch.appender.elastic.iml.policy-name**: Name of the log ILM policy. Created if it does not exist. Example: `log-policy`.
- **elasticsearch.appender.elastic.iml.hot-phase**: ILM hot phase duration. Example: `30d`.
- **elasticsearch.appender.elastic.iml.warm-phase**: ILM warm phase duration. Example: `60d`.
- **elasticsearch.appender.elastic.iml.cold-phase**: ILM cold phase duration. Example: `90d`.
- **elasticsearch.appender.elastic.iml.delete-phase**: ILM delete phase duration. Example: `365d`.
- **elasticsearch.appender.elastic.enable**: Enables/disables logging. Example: `true` or `false`.
- **elasticsearch.appender.elastic.host**: Elasticsearch server address. Example: `elastic-host`.
- **elasticsearch.appender.elastic.port**: Elasticsearch server port. Default port is `9200`.
- **elasticsearch.appender.elastic.username**: Elasticsearch username. Example: `elastic`.
- **elasticsearch.appender.elastic.schema**: Scheme used (`http` or `https`). Example: `https`.
- **elasticsearch.appender.elastic.password**: Elasticsearch user password. Example: `password`.
- **elasticsearch.appender.elastic.action-log-index-name**: Index name for action logs. Example: `prod-action-log`.
- **elasticsearch.appender.url-patterns**: URL patterns that should be logged. Example: `/api/*`.
- **elasticsearch.appender.elastic.app-name**: Application name used in the logs. Example: `app-name`.
- **elasticsearch.appender.elastic.log-level**: Log level. Example: `INFO`.
- **elasticsearch.appender.elastic.system-log-index-name**: Index name for system logs. Example: `prod-system-log`. If this parameter is empty (`""` or `null`), system logs will not be sent to Elasticsearch.
- **elasticsearch.appender.elastic.system-log-logger-name**: Logger name for system logs. Example: `es-logger`.
- **elasticsearch.appender.elastic.system-log-error-logger-name**: Logger name for system error logs. Example: `es-error-logger`.

## Installation and Usage

1. **Add the dependency**: Add this dependency to your project.

2. **Configuration**: Set the parameters listed above in your application's configuration file (e.g., `application.properties` or `application.yml`).

3. **Run**: Start your application and logs will automatically be sent to Elasticsearch.

## Example

```properties
management.health.elasticsearch.enabled=true
elasticsearch.appender.elastic.enable=true
elasticsearch.appender.elastic.create-index=true
elasticsearch.appender.elastic.iml.policy-name=log-policy
elasticsearch.appender.elastic.iml.hot-phase=30d
elasticsearch.appender.elastic.iml.warm-phase=60d
elasticsearch.appender.elastic.iml.cold-phase=90d
elasticsearch.appender.elastic.iml.delete-phase=365d
elasticsearch.appender.elastic.host=host
elasticsearch.appender.elastic.port=9200
elasticsearch.appender.elastic.username=elastic
elasticsearch.appender.elastic.schema=https
elasticsearch.appender.elastic.password=password
elasticsearch.appender.elastic.action-log-index-name=prod-action-log
elasticsearch.appender.url-patterns=/api/*
elasticsearch.appender.elastic.app-name=app-name
elasticsearch.appender.elastic.log-level=INFO
elasticsearch.appender.elastic.system-log-index-name=prod-system-log
elasticsearch.appender.elastic.system-log-logger-name=es-logger
elasticsearch.appender.elastic.system-log-error-logger-name=es-error-logger
```
