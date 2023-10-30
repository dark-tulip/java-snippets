# Advanced message queuing protocol
- general purpose message queuing protocol
- TCP connections (TLS + username + passwd)
- publish -> exchanger -> route -> queue -> consumers
- routes messages to queues (топик обменника перенаправляет сообщения в очереди)
- очереди отправляют консюмерам
- both sync and async
- 2 GB max , 123 MB recommend
  
# Message Queuing Telemetry Transport
- specially designed IoT
- publisher / subscriber architecture
- использует MQTT topics
- TCP connections (TLS + username + passwd)
- работа при постоянной потере связи
- легковесный
- asynchronous msg mode by event-based
- can queue msg
- 256 MB max
