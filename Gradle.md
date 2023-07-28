- (*) - зависимость ранее встречалась
- (n) - not resolved
- (c) - dependency constraint 
- gradle is written on groovy (also supports kotlin syntax)

#### Verification
- check executes before test
- check tests total project (included plugins, tests) - it is lifecycle task - по умолчанию равен таске ТЕСТ, но при подключении других плагинов удобен для совместной верификации
- test only runs unit tests
