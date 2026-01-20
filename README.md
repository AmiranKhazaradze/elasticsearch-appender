# Elasticsearch Appender

Elasticsearch Appender არის ბიბლიოთეკა, რომელიც გამოიყენება Java / Spring Boot აპლიკაციებში
ლოგების ავტომატურად Elasticsearch-ში გასაგზავნად.

## თავსებადობა

- ✅ მხარდაჭერილია **Elasticsearch 8.x** ვერსია.

## ინსტალაცია

### Maven-ის გამოყენებით

დაამატეთ დამოკიდებულება თქვენს `pom.xml` ფაილში:

```xml
<dependency>
    <groupId>io.github.amirankhazaradze</groupId>
    <artifactId>elasticsearch-appender</artifactId>
    <version>8.1.0</version>
</dependency>
```

## კონფიგურაციის პარამეტრები

ქვემოთ მოცემულია Elasticsearch Appender-ის კონფიგურაციის პარამეტრები და მათი აღწერა:

- **management.health.elasticsearch.enabled**: გამორთული Elasticsearch ჯანმრთელობის შემოწმება. მაგალითად: `true or false`.
- **elasticsearch.appender.elastic.create-index**: დატასტრიმის შექმნა Elasticsearch ში. შექმნის თუ არ არსებობს. მაგალითად: `true or false`.
- **elasticsearch.appender.elastic.iml.policy-name**: ლოგების პოლისის სახელი. შექმნის თუ არ არსებობს. მაგალითად: `log-policy`.
- **elasticsearch.appender.elastic.iml.hot-phase**: პოლისის ფაზა ცხელი. მაგალითად: `30d`.
- **elasticsearch.appender.elastic.iml.warm-phase**: პოლისის ფაზა თბილი. მაგალითად: `60d`.
- **elasticsearch.appender.elastic.iml.cold-phase**: პოლისის ფაზა ცივი. მაგალითად: `90d`.
- **elasticsearch.appender.elastic.iml.delete-phase**: პოლისის ფაზა წაშლა. მაგალითად: `365d`.
- **elasticsearch.appender.elastic.enable**: ლოგების ჩართვა გამორთვა. მაგალითად: `true or false`.
- **elasticsearch.appender.elastic.host**: Elasticsearch სერვერის მისამართი. მაგალითად: `elastic-host`.
- **elasticsearch.appender.elastic.port**: Elasticsearch სერვერის პორტი. ნაგულისხმევი პორტია `9200`.
- **elasticsearch.appender.elastic.username**: Elasticsearch-ის მომხმარებლის სახელი. მაგალითად: `elastic`.
- **elasticsearch.appender.elastic.schema**: გამოყენებული სქემა (`http` ან `https`). მაგალითად: `https`.
- **elasticsearch.appender.elastic.password**: Elasticsearch-ის მომხმარებლის პაროლი. მაგალითად: `password`.
- **elasticsearch.appender.elastic.action-log-index-name**: ინდექსის სახელი ქმედებების ლოგებისთვის. მაგალითად: `prod-action-log`.
- **elasticsearch.appender.url-patterns**: URL ნიმუშები, რომლებიც უნდა იყოს ლოგირებული. მაგალითად: `/api/*`.
- **elasticsearch.appender.elastic.app-name**: აპლიკაციის სახელი, რომელიც გამოიყენება ლოგებში. მაგალითად: `app-name`.
- **elasticsearch.appender.elastic.log-level**: ლოგის დონე. მაგალითად: `INFO`.
- **elasticsearch.appender.elastic.system-log-index-name**: ინდექსის სახელი სისტემური ლოგებისთვის. მაგალითად: `prod-system-log`. თუ ეს პარამეტრი ცარიელია (`""` ან `null`), სისტემური ლოგები არ გაიგზავნება Elasticsearch-ში.
- **elasticsearch.appender.elastic.system-log-logger-name**: ლოგერის სახელი სისტემური ლოგებისთვის. მაგალითად: `es-logger`.
- **elasticsearch.appender.elastic.system-log-error-logger-name**: ლოგერის სახელი სისტემური შეცდომების ლოგებისთვის. მაგალითად: `es-error-logger`.

## ინსტალაცია და გამოყენება

1. **დამოკიდებულების დამატება**: დაამატეთ ეს დამოკიდებულება თქვენს პროექტში.

2. **კონფიგურაცია**: დააყენეთ ზემოთ ჩამოთვლილი პარამეტრები თქვენი აპლიკაციის კონფიგურაციის ფაილში (მაგ., `application.properties` ან `application.yml`).

3. **გაშვება**: გაუშვით თქვენი აპლიკაცია და ლოგები ავტომატურად გაიგზავნება Elasticsearch-ში.

## მაგალითი

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
elasticsearch.appender.elastic.app-name=security-policies-back
elasticsearch.appender.elastic.log-level=INFO
elasticsearch.appender.elastic.system-log-index-name=prod-system-log
elasticsearch.appender.elastic.system-log-logger-name=es-logger
elasticsearch.appender.elastic.system-log-error-logger-name=es-error-logger