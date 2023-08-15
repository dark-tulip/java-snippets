- zookeeper used to manage kafka brokers
- helps to perform leader elections
- отправляет уведомления кафке об изменениях в конфигурации
- зукипер работает с нечетным числом серверов (1, 3, 5, 7)
- kafka 2.* cannot work without zookeeper (goes together)
- kafka 3.* may have kafka Raft instead of zookeeper (KIP-500)
- до кафки 4.0 желательно всегда использовать зукипер
- ЗУКИПЕР НИКОГДА НЕ хранит consumer offsets (ложная инфа в интернете)
- zeekeeper leader server (write), others, followers (for read)

Зачем и когда нужен зукипер?
- когда работают внутренне с брокерами кафки
- не нужен клиентам кафки (по соображениям безопасности)
- весь API и CLI для работы с клиентами кафки уже мигрировал с зукипера в брокеры кафки
- порты зукпера должны быть открыты только кафка брокерам, НЕ кафка клиентам
