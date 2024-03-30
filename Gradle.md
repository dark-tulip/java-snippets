## как подружить Ant with Gradle
- gradle.build.kts
```kts

ant.importBuild("build-ant.xml")
```

- build-ant.xml
```xml
<project>
    <target name="hello-ant">
        <echo>Hello, from ANT!</echo>
    </target>
</project>
```

- output:
```
(base) tansh@MBP-tansh test % ./gradlew hello-ant
> Task :hello-ant
[ant:echo] Hello, from ANT!
```

- (*) - зависимость ранее встречалась
- (n) - not resolved
- (c) - dependency constraint 
- gradle is written on groovy (also supports kotlin syntax)

#### Verification
- check executes before test
- check tests total project (included plugins, tests) - it is lifecycle task - по умолчанию равен таске ТЕСТ, но при подключении других плагинов удобен для совместной верификации
- test only runs unit tests

### Strictly dependencies
- существуют либы, которые используют принудительные версии пакетов, это мешает gradle правильно составлять дерево зависимостей
- строгие зависимости

```groovy
implementation("com.squareup.okhttp3:okhttp") {
  version {
    strictly '4.10.0'
  }
}

configurations.configureEach { pkg ->
  pkg.exclude(group: "com.squareup.okhttp3", module: "okhttp")
}
```
### Dependency tree
```
./gradlew :plugin:dependencyInsight --dependency okhttp3
```
