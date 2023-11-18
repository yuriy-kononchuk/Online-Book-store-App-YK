package com.example.project.dto.user;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<FieldMatch, Object> {

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        UserRegistrationRequestDto requestDto = (UserRegistrationRequestDto) value;
        return requestDto.getPassword().equals(requestDto.getRepeatPassword());
    }
}
