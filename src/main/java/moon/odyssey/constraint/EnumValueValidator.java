package moon.odyssey.constraint;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
