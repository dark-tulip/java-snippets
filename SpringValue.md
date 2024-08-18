```java
public double MIN_ANOMALY_SPEED;

@Value("#{new Double('${bus.velocity.max.anomaly.km.per.hour}')}")
public double MAX_ANOMALY_SPEED;

@Value("#{new Double('${bus.velocity.default.km.per.hour}')}")
private double busVelocityDefaultKmPerHour;
```
