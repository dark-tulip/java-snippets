Fallback feign - метод выполняющийся в случае упадка сервиса,

`
@HystrixCommand Annotation 
`

## Zuul proxy
- Zuil is reverse proxy, accepts all queries 
- Rebalances all queries to all services
- По умолчанию зуул отрезает все http headers которые приходят с клиента
- Zuul proxy помогает спрингу проксировать запросы на основе его урл и направлять в соотв микросервис
