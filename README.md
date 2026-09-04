# URLShortener

Учебный сервис сокращения ссылок на Spring Boot.

Сервис принимает длинный URL, генерирует короткий код, сохраняет соответствие в PostgreSQL, делает редирект по короткой ссылке и собирает клики через Kafka. Метрики приложения доступны через Spring Boot Actuator, Prometheus и Grafana.

## Стек

- Java 21
- Spring Boot 4.2.0-M1
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Spring Kafka
- Docker Compose
- Prometheus
- Grafana
- Lombok

## Архитектура

Основной поток создания ссылки:

```text
POST /links
  -> CreateShortLinkRequestDto
  -> LinkService
  -> ShortLinkRepository
  -> PostgreSQL
  -> CreateShortLinkResponseDto
```

Основной поток редиректа и аналитики:

```text
GET /links/{shortCode}
  -> найти ShortLink
  -> отправить LinkClickedEvent в Kafka
  -> вернуть 302 redirect

Kafka consumer
  -> получить LinkClickedEvent
  -> сохранить LinkClick в PostgreSQL
```

Статистика:

```text
GET /links/{shortCode}/stats
  -> найти ShortLink
  -> посчитать клики в link_clicks
  -> вернуть clickCount
```

## Структура проекта

```text
src/main/java/com/example/URLShortener
  link/
    LinkController.java
    LinkService.java
    ShortLinkRepository.java
    dto/
      CreateShortLinkRequestDto.java
      CreateShortLinkResponseDto.java
    entity/
      ShortLink.java

  click/
    ClickService.java
    LinkClickRepository.java
    dto/
      ShortLinkStatsResponseDto.java
    entity/
      LinkClick.java
    event/
      LinkClickedEvent.java
      LinkClickedEventProducer.java
      LinkClickedEventConsumer.java

src/main/resources
  application.yaml
  db/migration/

prometheus/
  prometheus.yml
```

## Конфигурация

Приложение запускается локально, а инфраструктура поднимается через Docker Compose.

Основные настройки из `application.yaml`:

```yaml
app:
  base-url: "http://localhost:8080"
  kafka:
    link-clicked-topic: link-clicked

spring:
  datasource:
    url: jdbc:postgresql://localhost:49950/mydatabase
    username: myuser
    password: secret

  kafka:
    bootstrap-servers: localhost:9092
```

Порты инфраструктуры:

```text
PostgreSQL  -> localhost:49950
Kafka       -> localhost:9092
Prometheus  -> http://localhost:9090
Grafana     -> http://localhost:3000
Application -> http://localhost:8080
```

## Запуск

Поднять инфраструктуру:

```bash
docker compose -f docker-compose.yaml up -d
```

Проверить контейнеры:

```bash
docker compose -f docker-compose.yaml ps
```

Запустить приложение:

```bash
./gradlew bootRun
```

При старте Flyway применит миграции из:

```text
src/main/resources/db/migration
```

## API

### Создать короткую ссылку

```http
POST /links
Content-Type: application/json
```

Тело запроса:

```json
{
  "url": "https://example.com"
}
```

Пример:

```bash
curl -i -X POST http://localhost:8080/links \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com"}'
```

Пример ответа:

```json
{
  "originalUrl": "https://example.com",
  "shortCode": "a8Kz21Qb",
  "shortUrl": "http://localhost:8080/links/a8Kz21Qb"
}
```

Если такой `originalUrl` уже есть в базе, сервис переиспользует существующий `shortCode`.

### Перейти по короткой ссылке

```http
GET /links/{shortCode}
```

Пример:

```bash
curl -i http://localhost:8080/links/a8Kz21Qb
```

Ожидаемый ответ:

```http
HTTP/1.1 302
Location: https://example.com
```

При редиректе сервис отправляет событие клика в Kafka. Consumer получает событие и сохраняет запись в таблицу `link_clicks`.

### Получить статистику ссылки

```http
GET /links/{shortCode}/stats
```

Пример:

```bash
curl -i http://localhost:8080/links/a8Kz21Qb/stats
```

Пример ответа:

```json
{
  "originalUrl": "https://example.com",
  "shortCode": "a8Kz21Qb",
  "clickCount": 3
}
```

## Валидация

DTO создания ссылки проверяет поле `url`:

```java
@NotBlank
@Size(max = 2048)
String url
```

Если отправить пустой URL, приложение вернет `400 Bad Request`:

```bash
curl -i -X POST http://localhost:8080/links \
  -H 'Content-Type: application/json' \
  -d '{"url":""}'
```

## База данных

Основные таблицы:

```text
short_links
  id
  original_url
  short_code
  created_at

link_clicks
  id
  short_link_id
  clicked_at
```

Посмотреть таблицы:

```bash
docker compose -f docker-compose.yaml exec postgres \
  psql -U myuser -d mydatabase -c '\dt'
```

Посмотреть созданные ссылки:

```bash
docker compose -f docker-compose.yaml exec postgres \
  psql -U myuser -d mydatabase -c 'select * from short_links;'
```

Посмотреть последние клики:

```bash
docker compose -f docker-compose.yaml exec postgres \
  psql -U myuser -d mydatabase -c 'select * from link_clicks order by id desc limit 10;'
```

## Kafka

Topic для кликов:

```text
link-clicked
```

Событие:

```java
public record LinkClickedEvent(
        Long shortLinkId,
        String shortCode
) {
}
```

Проверка Kafka-цепочки:

1. Создать ссылку через `POST /links`.
2. Вызвать `GET /links/{shortCode}`.
3. Проверить `GET /links/{shortCode}/stats`.
4. `clickCount` должен увеличиться.

## Метрики

Actuator endpoints:

```text
http://localhost:8080/actuator/health
http://localhost:8080/actuator/prometheus
```

Prometheus:

```text
http://localhost:9090
```

В Prometheus можно проверить target:

```text
Status -> Targets
```

Job `url_shortener` должен быть в состоянии `UP`.

Grafana:

```text
http://localhost:3000
```

Логин по умолчанию:

```text
admin / admin
```

Prometheus data source для Grafana:

```text
http://prometheus:9090
```

Примеры PromQL-запросов:

```promql
up
```

```promql
http_server_requests_seconds_count
```

```promql
rate(http_server_requests_seconds_count[1m])
```

## Полезные команды

Остановить инфраструктуру:

```bash
docker compose -f docker-compose.yaml down
```

Перезапустить инфраструктуру:

```bash
docker compose -f docker-compose.yaml down
docker compose -f docker-compose.yaml up -d
```

Посмотреть логи Kafka:

```bash
docker compose -f docker-compose.yaml logs kafka --tail=100
```

Посмотреть логи Prometheus:

```bash
docker compose -f docker-compose.yaml logs prometheus --tail=100
```

## Что можно улучшить дальше

- Добавить нормальную обработку ошибок через `@ControllerAdvice`.
- Валидировать URL не только на пустоту, но и на допустимую схему `http`/`https`.
- Добавить тесты для controller/service/repository.
- Добавить кастомные бизнес-метрики через Micrometer.
- Подготовить Grafana dashboard как provisioning config.
- Добавить хранение IP, User-Agent и Referer для кликов.
- Добавить rate limiting.
