- где хранить прото файлы? `/src/main/proto` указывается это здесь

```xml
<plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>${protobuf.maven.plugin.version}</version>
                <configuration>
                    <protocArtifact>com.google.protobuf:protoc:${protoc.version}:exe:${os.detected.classifier}</protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>io.grpc:protoc-gen-grpc-java:${grpc.version}:exe:${os.detected.classifier}</pluginArtifact>
                    <protoSourceRoot>
                        ${basedir}/src/main/proto/
                    </protoSourceRoot>
                </configuration>
</plugin>
```
- framework from google
- from service-to service communication
- proto file is a platfrom neutral file
- если IDEA не распознает класс прото файла но успешно компилируется

<img width="512" alt="image" src="https://github.com/dark-tulip/java-snippets/assets/89765480/6eea16ab-18c5-4b71-8c0e-0e36f3ac9648">

нужно пометить директорию Mark as Generated Project Sources 

<img width="564" alt="image" src="https://github.com/dark-tulip/java-snippets/assets/89765480/6a49008c-6285-48ab-ad72-009e2437e4cf">


### `package v1; and option java_package="kz.tansh.proto.v1";`

- Если у вас два одинаковых класса, версионируйте прото файлы по пакетам

<img width="500" alt="image" src="https://github.com/dark-tulip/java-snippets/assets/89765480/e8da7679-7e0c-4995-b3b8-c973ac9506de">


### `option java_multiple_files = true;`

- позволяет directly обратиться к билдеру объекта без ссылания к PersonOuterClass
  
<img width="600" alt="image" src="https://github.com/dark-tulip/java-snippets/assets/89765480/2c57a290-1eea-4e2d-af14-2d0da4d90b9e">


### Cвойства grpc Object Builder-a

```java
package kz.tansh;

import kz.tansh.proto.v1.Person;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
  public static void main(String[] args) {
    var person1 = Person.newBuilder()
                       .setAge(12)
                       .setName("Test")
                       .build();

    var person2 = Person.newBuilder()
                       .setAge(12)
                       .setName("Test")
                       .build();

    // 1. EQUALITY PROPERTY
    log.info("person1.equals(person2): " + person1.equals(person2));  // true
    log.info("person1 == person2: " + (person1 == person2));          // false

    // 2. IMMUTABLE PROPERTY
    // PROTO СLASSES ARE IMMUTABLE, YOU CANNOT USE SETTER DIRECTLY
    log.info("person1 before " + person1.getAge());  // age: 12

    // you only can create new object, using toBuilder() Method
    person1.toBuilder().setAge(44).build();
    log.info("person1 after " + person1.getAge());   // age: 12

    var person3 = person1.toBuilder().setAge(44).build();
    log.info("person3 after " + person3.getAge());   // age: 44

    // 3. NULLS ARE NOT ALLOWED
    // person1.toBuilder().setName(null).build();       // NPE
    person1 = person1.toBuilder().clearName().build();  // use clear() to set empty property
    log.info("person1 after " + person1.getName());

    person1 = Person.newBuilder().setAge(33).build();   // здесь не установили имя
    log.info("person1 {} ", person1.getName());         // allowed, empty value
  }
}
```

for efficient encoding of negative values use sint32 (int), or sint64 (long), однако обычные int32, int64 тоже допустимы для отрицательных значений

## JSON
- JSON - это текстовый формат данных (больший размер сообщения: медленней пересылка)
- Избыточный - повторение ключей в массиве каждый раз
- нет строгой типизации
- JSON slow for serialization for machine that protobuf

## Protobuf
- Protobuf - бинарный формат данных (ускорение до 7-10 раз)
- строная типизация
- нечитаемый для человека
- необходимо кодировать и декодировать эти данные

### Версии протофайлов
прото1 - нечто внутреннее от гугла что не было выпущено внаружу опубликовано
прото2 - your should always set if the field is optional or required
прото3 

## HTTP 2.0
- меньший размер - выше скорость
- потоки данных
- мультиплексирование, приоритизация потоков

## Генерация кода
- компилятор protoc
- `.proto` file describes типы данных, формат сообщений и RPC операции

## Когда нужен gRPC?
- если монолит: куда нужен доступ из браузера REST API
- between microservices
- different langs
- data streaming
- огромное кол-во запросов или узкий канал

# GRPC server

```proto
syntax = "proto3";
package kz.inn.grpc;

message HelloRequest {
  string name = 1;  // key=1
  repeated string hobbies = 2; // key tag should be unique
}

message HelloResponse {
  string greeting = 1;
}

service GreetingService {
  rpc greeting(HelloRequest) returns (HelloResponse);
}
```

```java
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        Server server = ServerBuilder.forPort(8080).addService(new GreetingServiceImpl()).build();

        server.start();

        System.out.println("Server started");

        server.awaitTermination();

    }
}

public class GreetingServiceImpl extends GreetingServiceGrpc.GreetingServiceImplBase {
    public void greeting(kz.inn.grpc.GreetingServiceOuterClass.HelloRequest request,
                         StreamObserver<kz.inn.grpc.GreetingServiceOuterClass.HelloResponse> responseStreamObserver) {
        System.out.println(request);

        GreetingServiceOuterClass.HelloResponse response = kz.inn.grpc.GreetingServiceOuterClass
                .HelloResponse.newBuilder()
                .setGreeting("Hello from server, " + request.getName())
                .build();

        responseStreamObserver.onNext(response);

        responseStreamObserver.onCompleted();
    }
}
```
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>kz.inn</groupId>
    <artifactId>GRPC_SERVER</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-netty-shaded</artifactId>
            <version>1.24.0</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-protobuf</artifactId>
            <version>1.24.0</version>
        </dependency>
        <dependency>
            <groupId>io.grpc</groupId>
            <artifactId>grpc-stub</artifactId>
            <version>1.24.0</version>
        </dependency>
        <dependency>
            <groupId>javax.annotation</groupId>
            <artifactId>javax.annotation-api</artifactId>
            <version>1.3.1</version>
        </dependency>
    </dependencies>

    <build>
        <extensions>
            <extension>
                <groupId>kr.motd.maven</groupId>
                <artifactId>os-maven-plugin</artifactId>
                <version>1.6.2</version>
            </extension>
        </extensions>
        <plugins>
            <plugin>
                <groupId>org.xolstice.maven.plugins</groupId>
                <artifactId>protobuf-maven-plugin</artifactId>
                <version>0.6.1</version>
                <configuration>
                    <protocArtifact>com.google.protobuf:protoc:3.9.0:exe:${os.detected.classifier}</protocArtifact>
                    <pluginId>grpc-java</pluginId>
                    <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.24.0:exe:${os.detected.classifier}</pluginArtifact>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>compile-custom</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

# GRPC client + same `proto file` and `pom.xml` as in server
```java

public class Client {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080")
                .usePlaintext().build();

        // stub это тот объект на котором можно делать удаленные вызовы
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc.newBlockingStub(channel);
        GreetingServiceOuterClass.HelloRequest request = GreetingServiceOuterClass.HelloRequest
                .newBuilder().setName("Neil").build();

        GreetingServiceOuterClass.HelloResponse response = stub.greeting(request);

        System.out.println(response);

        channel.shutdownNow();
    }
}
```
