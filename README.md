# Java Enum Constraint at Spring JPA and Spring WebFlux
---

## Framework
---
- Java 1.8 +
- Spring Boot 2.2.x
- Spring WebFlux
- Spring Data JPA
- MySQL 8.0.19 with Docker

## How to use 
---
### Docker for MySQL
```bash
~] cd docker
~] docker-compose up -d
```

### Using Enum at JPA Entity

#### Define Interface for Enum
```java
public interface BaseEnumCode<T> {
    T getValue();
}
```
#### Declare Enum Type 
```java
@Getter
@AllArgsConstructor
public enum TFCode implements BaseEnumCode<String> {

    TRUE("T"),
    FALSE("F"),
    NULL("")
    ;

    private final String value;
}
```

#### Define Abstract Converter for `BaseEnumCode<T>`
```java
public abstract class AbstractBaseEnumConverter<X extends Enum<X> & BaseEnumCode<Y>, Y> implements AttributeConverter<X, Y> {

    protected abstract X[] getValueList();

    @Override
    public Y convertToDatabaseColumn(X attribute) {
        return attribute.getValue();
    }

    @Override
    public X convertToEntityAttribute(Y dbData) {
        return Arrays.stream(getValueList())
                     .filter(e -> e.getValue().equals(dbData))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported type for %s.", dbData)));
    }
}
```

#### Declare JPA Converter
```java
@Converter(autoApply = true)
public class TFCodeConverter extends AbstractBaseEnumConverter<TFCode, String> {

    @Override
    protected TFCode[] getValueList() {
        return TFCode.values();
    }
}
```

#### Using EnumCode at JPA Entity
```java
@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = {"userId"})
public class User implements Serializable {

    @Id
    private String userId;

    private String password;

    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode isVerified;
}
```

* `User` Table DDL
```sql
CREATE TABLE IF NOT EXISTS `user` (
    `user_id`         varchar(30)  NOT NULL COMMENT '사용자아이디',
    `password`        varchar(255) NOT NULL COMMENT '비밀번호(ENC)',
    `is_verified`     char(1)      NOT NULL COMMENT 'T or F',
    `update_date`     timestamp    NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자정보';
```

### Using Enum at WebFlux Validation

#### Define Constraint Annotation
```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ReportAsSingleViolation
@Constraint(validatedBy=EnumValueValidator.class)
public @interface EnumValue {

    Class<? extends Enum<?>> enumClazz();

    String[] values();

    String message() default "{moon.odyssey.constraint.EnumValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

#### Declare Constraint Validator
```java
public class EnumValueValidator implements ConstraintValidator<EnumValue, Enum> {

    List<Enum> valueList = null;

    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {

        return valueList.contains(value);
    }

    @Override
    public void initialize(EnumValue constraintAnnotation) {

        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClazz();

        List<String> enumValueList = Arrays.asList(constraintAnnotation.values());

        Enum<?>[] enumValues = enumClass.getEnumConstants();
        valueList = Arrays.stream(enumValues)
                          .filter(e -> enumValueList.contains(e.name()))
                          .collect(Collectors.toList());

    }
}
```

#### Using Validation at `@Valid` Parameter
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserInfo {

    @NotEmpty
    @Length(min = 4, max = 30)
    private String userId;

    @NotEmpty
    @Length(min = 8, max = 50)
    private String password;

    @EnumValue(enumClazz = TFCode.class, values = {"TRUE", "FALSE"}, message = "Invalid Code")
    private TFCode isVerified;
}
```

#### Validation at `@RestController`
```java
@RestController
@RequestMapping(path = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserRepository repository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addUser(@RequestBody @Valid UserInfo userInfo) {

        return
            Mono.defer(() -> repository.findById(userInfo.getUserId()).map(Mono::just).orElseGet(Mono::empty))
                .subscribeOn(Schedulers.elastic())
                .flatMap(__ -> Mono.error(new ResponseStatusException(HttpStatus.CONFLICT, "The userId already exists : " + userInfo.getUserId())))
                .switchIfEmpty(Mono.defer(() -> Mono.just(repository.save(new User(userInfo.getUserId(), userInfo.getPassword(), userInfo.getIsVerified())))).subscribeOn(Schedulers.elastic()))
                .cast(User.class)
                .then()
            ;
    }
}
```





