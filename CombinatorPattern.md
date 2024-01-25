```java
package org.example;

import java.util.function.Function;

import static org.example.UserValidatorCombinator.ValidationResult;

public interface UserValidatorCombinator extends Function<UserDto, ValidationResult> {

  enum ValidationResult {
    SUCCESSFUL,
    WRONG_EMAIL,
    WRONG_USERNAME,
    WRONG_PHONE,
    WRONG_AGE,
  }

  static UserValidatorCombinator isEmailValid() {
    return userDto -> userDto.getEmail().contains("@")
        ? ValidationResult.SUCCESSFUL
        : ValidationResult.WRONG_EMAIL;
  }

  static UserValidatorCombinator isUsernameValid() {
    return userDto -> !userDto.getName().isEmpty() && !userDto.getName().isBlank()
        ? ValidationResult.SUCCESSFUL
        : ValidationResult.WRONG_USERNAME;
  }

  static UserValidatorCombinator isAgeValid() {
    return userDto -> userDto.getAge() > 18
        ? ValidationResult.SUCCESSFUL
        : ValidationResult.WRONG_AGE;
  }

  static UserValidatorCombinator isPhoneNumberValid() {
    return userDto -> userDto.getPhone().contains("+7")
        ? ValidationResult.SUCCESSFUL
        : ValidationResult.WRONG_PHONE;
  }

  default UserValidatorCombinator and(UserValidatorCombinator param) {
    return userDto -> {
      ValidationResult userValidationResult = this.apply(userDto);
      return userValidationResult.equals(ValidationResult.SUCCESSFUL)
          ? param.apply(userDto)
          : userValidationResult;
    };
  }
}
```


```java
package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
  private String email;
  private String phone;
  private String name;
  private int    age;
}
```

```java
package org.example;

import static org.example.UserValidatorCombinator.*;

public class Main {

  public static void main(String[] args) {

    UserDto userDto = new UserDto(
        "test@email.com",
        "+77776665544",
        "testName",
        22
    );

    ValidationResult result = isEmailValid()
        .and(isPhoneNumberValid())
        .and(isUsernameValid())
        .and(isAgeValid())
        .apply(userDto);

    System.out.println(result);

  }
}
```
