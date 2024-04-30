## Поднять локальный раббит с админкой

```
docker run -d \
  --name my-rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=reader \
  -e RABBITMQ_DEFAULT_PASS=reader \
  rabbitmq:3.9-management
```
