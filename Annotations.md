- метки используемые для передачи определенной информации
## Target (цель) - описывает область применения аннотации
- `TYPE` (class, enum, interface)
- `FIELD` 
- `METHOD`
- `CONSTRUCTOR`
- `PARAMETER` - параметры метода или класса

## Retention - жизненный цикл аннотации
- `SOURCE` - есть в источнике кода, нет в байт коде (отбрасывается компилятором)
- `CLASS`- есть в байт коде, но нет в рантайме (отбрасывается JVM) - default
- `RUNTIME` - работает в рантайме
```Java

// create annotation
// set methods
// set default
// get class - get annotations
// for reflection USE RUNTIME, CLASS WILL NOT WORK


@OperatingSystem(year = 2222, terminalType = TerminalType.CMD)
class Windows {
}

@OperatingSystem(displayName = "Linux Ubuntu 22.04", terminalType = TerminalType.BASH, year = 1991)
class Linux {
}

public class JavaAnnotations {

    public static void main(String[] args) throws ClassNotFoundException {

        Class windows = Class.forName("org.example.Windows");
        Class ubuntu = Class.forName("org.example.Linux");

        OperatingSystem windowsAnnotation = (OperatingSystem) windows.getAnnotation(OperatingSystem.class);
        System.out.printf("displayName: %s, terminalType: %s, year: %s%n", windowsAnnotation.displayName(), windowsAnnotation.terminalType(), windowsAnnotation.year());

        OperatingSystem ubuntuAnnotation = (OperatingSystem) ubuntu.getAnnotation(OperatingSystem.class);
        System.out.printf("displayName: %s, terminalType: %s, year: %s%n", ubuntuAnnotation.displayName(), ubuntuAnnotation.terminalType(), ubuntuAnnotation.year());

        /*
        displayName: Windows, terminalType: CMD, year: 2222
        displayName: Linux Ubuntu 22.04, terminalType: BASH, year: 1991
        */

        System.out.println("toString:                       " + ubuntuAnnotation);  // @org.example.OperatingSystem(year=1991, displayName="Linux Ubuntu 22.04", terminalType=BASH)
        System.out.println("annotationType:                 " + ubuntuAnnotation.annotationType());
        System.out.println("annotationType.getSimpleName:   " + ubuntuAnnotation.annotationType().getSimpleName());
        System.out.println("getClassLoader.getClassLoader:  " + ubuntuAnnotation.annotationType().getClassLoader());
        System.out.println("getAnnotations:                 " + Arrays.toString(ubuntuAnnotation.annotationType().getAnnotations()));

        /**
         * toString:                       @org.example.OperatingSystem(year=1991, displayName="Linux Ubuntu 22.04", terminalType=BASH)
         * annotationType:                 interface org.example.OperatingSystem
         * annotationType.getSimpleName:   OperatingSystem
         * getClassLoader.getClassLoader:  jdk.internal.loader.ClassLoaders$AppClassLoader@5cb0d902
         * getAnnotations:                 [@java.lang.annotation.Target({TYPE}), @java.lang.annotation.Retention(RUNTIME)]
         */
    }
}

enum TerminalType {
    TERMINAL,
    BASH,
    CMD
}


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface OperatingSystem {
    String displayName() default "Windows";

    TerminalType terminalType();

    int year() default 2001;

}
```
- если установить для аннотаций RETENTION CLASS - сломаются во время рантайма - `Cannot invoke "org.example.OperatingSystem.displayName()" because "windowsAnnotation" is null`
- @interface
- внутренние поля прописаны как методы
- default - должно принимать constant значение по умолчанию


### Аннотации для валидации

```json
{
    "KAZ": {
        "validationRules": {
            "PHONE": {
                "pattern": "^77[0-9]{9}$",
                "description": "Phone number validation for KAZ"
            },
            "BUS_PLATE_NUMBER": {
                "pattern": "^([A-Z0-9]{1,3})([A-Z0-9]{2,3})([A-Z0-9]{2,3})$",
                "description": "Bus number validation for KAZ"
            },
            "IIN": {
                "pattern": "^[0-9]{12}$",
                "description": "Iin number validation for KAZ"
            }
        }
    },
    "KGZ": {
        "validationRules": {
            "PHONE": {
                "pattern": "^996[0-9]{9}$",
                "description": "Phone number validation for KGZ"
            },
            "BUS_PLATE_NUMBER": {
                "pattern": "^[A-Z0-9]{4,6}$",
                "description": "Bus number validation for KGZ"
            },
            "IIN": {
                "pattern": "^[0-9]{7}$",
                "description": "Bus number validation for KGZ"
            }
        }
    }
}

```

```java
/**
 * Чтобы вставить страну Гуглим "код страны киргизии"
 */
public enum Country {
    KAZ("KAZ", "Казахстан"),
    KGZ("KGZ", "Киргизстан");

    @Getter
    final String code;
    final String description;

    Country(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Country getByCode(String code) throws IllegalArgumentException {
        if (code == null || code.isEmpty())
            throw new IllegalArgumentException(CustomErrorTypes.DictionaryNotFound,
                    "Enum: " + Country.class.getSimpleName() +
                            "code: is null (must not be null)");

        return Country.valueOf(code);
    }
}

public enum ValidationType {
    PHONE,
    IIN,
    BUS_PLATE_NUMBER;

    public static ValidationType getByCode(String code) throws IllegalArgumentException {
        if (code == null || code.isEmpty())
            throw new IllegalArgumentException(CustomErrorTypes.DictionaryNotFound,
                    "Enum: " + ValidationType.class.getSimpleName() +
                            "code: is null (must not be null)");

      return ValidationType.valueOf(code.toUpperCase());
    }
}

/**
 * Аннотация для валидации ИИН-а человека. Для киргизии ИИН это id number.
 * Лучше назвать @ValidateIdNumber чем @ValidateIin, потому что ИИН это термин только для нас,
 * в других странах используется Id number.
 */
@Constraint(validatedBy = IdNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateIdNumber {
    /**
     * Дополнительный текст сообщения об ошибке. Не показывается пользователю
     */
    String message() default "Неверный ИИН пользователя";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Список стран по которым проходит валидация
     */
    Country[] countries() default Country.KAZ;
}


/**
 * Реализация логики валидации,
 * которая используется для аннотации @ValidateIdNumber
 */
public class IdNumberValidator implements ConstraintValidator<ValidateIdNumber, String> {

    private Country[] countries;
    private String    developerMessage;

    @Override
    public void initialize(ValidateIdNumber constraintAnnotation) {
        this.countries = constraintAnnotation.countries();
        this.developerMessage = constraintAnnotation.message();
    }

    @Override
    @SneakyThrows
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }

        // Если совпало с regex-ом страны - то поле успешно прошло валидацию
        for (Country country : countries) {
            String regex = ValidationConfig.getValidationRegex(country, ValidationType.IIN);
            Pattern pattern = Pattern.compile(regex);
            if (pattern.matcher(value).matches()) {
                return true;
            }
        }
        throw new BusinessLogicException(CustomErrorTypes.IinNumberValidationFailed, "", "iin: " + value + " additionalMessage: " + developerMessage);
    }
}



public class ValidationConfig {
    private static Map<Country, ValidationRulesMap> countryValidationRules = new HashMap<>();

    private static final Gson gson = new Gson();

    static {
        loadValidationRulesConfigFile();
    }

    /**
     * Все правила валидации по каждой стране находятся в файле validationRules.json
     */
    @SneakyThrows
    private static void loadValidationRulesConfigFile() {
        try (InputStream inputStream = ValidationConfig.class.getResourceAsStream("/validationRules.json")) {
            if (inputStream == null) {
                throw new IOException("Configuration file not found");
            }

            countryValidationRules = gson.fromJson(
                    new InputStreamReader(inputStream),
                    new TypeToken<Map<Country, ValidationRulesMap>>() {
                    }.getType()
            );

        } catch (IOException e) {
            throw new InternalException("Cannot read validation config file", e);
        }
    }

    public static boolean validateBool(Locality locality, ValidationType validationType, String value) throws IllegalArgumentException {
        ValidationRulesMap rulesMap = countryValidationRules.getOrDefault(
                Country.getByCode(locality.getCountry()),
                countryValidationRules.get(Country.KAZ)  // KAZ is default
        );

        Map<ValidationType, ValidationRule> rules = rulesMap.getValidationRules();
        ValidationRule                      rule  = rules.get(validationType);
        if (rule == null) {
            throw new IllegalArgumentException(CustomErrorTypes.DictionaryNotFound, "Validation rule not found by locality " + locality + " and validationType " + validationType);
        }
        return value.matches(rule.getPattern());
    }

    public static void validate(Locality locality, ValidationType validationType, String value) throws IllegalArgumentException {
        if (validateBool(locality, validationType, value)) {
            switch (validationType) {
                case PHONE:
                    throw new IllegalArgumentException(CustomErrorTypes.PhoneNumberValidationFailed, "phone: " + value);
                case IIN:
                    throw new IllegalArgumentException(CustomErrorTypes.IinNumberValidationFailed, "iin or id number: " + value);
                case BUS_PLATE_NUMBER:
                    throw new IllegalArgumentException(CustomErrorTypes.BusPlateNumberValidationFailed, "plate number: " + value);
                default:
                    throw new IllegalArgumentException(CustomErrorTypes.DictionaryNotFound, "please define error message for validation type");
            }
        }
    }

    public static String getValidationRegex(Locality locality, ValidationType validationType) {
        ValidationRulesMap rulesMap = countryValidationRules.getOrDefault(
                Country.valueOf(locality.getCountry()),
                countryValidationRules.get(Country.KAZ)  // KAZ is default
        );

        Map<ValidationType, ValidationRule> rules = rulesMap.getValidationRules();
        return rules.get(validationType).getPattern();
    }

    public static String getValidationRegex(Country country, ValidationType validationType) {
        ValidationRulesMap rulesMap = countryValidationRules.getOrDefault(
                country,
                countryValidationRules.get(Country.KAZ)  // KAZ is default
        );

        Map<ValidationType, ValidationRule> rules = rulesMap.getValidationRules();
        return rules.get(validationType).getPattern();
    }

    @Data
    @AllArgsConstructor
    private static class ValidationRule {
        private String pattern;
        private String description;
    }

    /**
     * Класс нужен потому что мапу в мапе с gson-ом десериализовать нельзя
     */
    @Getter
    @AllArgsConstructor
    private static class ValidationRulesMap {
        private Map<ValidationType, ValidationRule> validationRules;
    }

    /**
     * todo вынести в юнит тесты
     */
    public static void main(String[] args) throws IllegalArgumentException {
        assertTrue(validateBool(Locality.ASTANA, ValidationType.PHONE, "77776665544"), "");

        // IIN for KAZ
        assertTrue(validateBool(Locality.ASTANA, ValidationType.IIN, "020728666555"), "");
        assertTrue(!validateBool(Locality.KENTAU, ValidationType.IIN, "7776665"), "");
        assertTrue(validateBool(Locality.SHYMKENT, ValidationType.IIN, "123456789098"), "");
    }
}

```
