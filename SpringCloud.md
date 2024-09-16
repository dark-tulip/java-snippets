## Fallback feign 
- Fallback feign - метод выполняющийся в случае упадка сервиса,
- Хистрикс проксирует класс, который содержит логику circuit breaker
- Прокси класс вызывает аннотированный метод и ожидает ответа, при падении вызывает аварийный метод Fallback feign 
- @HystrixCommand Annotation 

## Zuul proxy
- Zuil is reverse proxy, accepts all queries 
- Rebalances all queries to all services
- По умолчанию зуул отрезает все http headers которые приходят с клиента
- Zuul proxy помогает спрингу проксировать запросы на основе его урл и направлять в соотв микросервис

## Service discovery
- Сервис дисковери идет через днс
- Кубернетис не может разворачиваться между кластерами без `istio`, `Spring cloud` может
- Resilience взаимодействие между ДЦ
