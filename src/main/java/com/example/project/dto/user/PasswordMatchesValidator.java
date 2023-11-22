package com.example.project.dto.user;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class PasswordMatchesValidator implements ConstraintValidator<FieldMatch,
        UserRegistrationRequestDto> {

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserRegistrationRequestDto request, ConstraintValidatorContext context) {
        //UserRegistrationRequestDto requestDto = (UserRegistrationRequestDto) value;
        UserRegistrationRequestDto requestDto = request;
        return requestDto.getPassword().equals(requestDto.getRepeatPassword());
    }
}
