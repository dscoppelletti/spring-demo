package it.scoppelletti.springdemo.r2dbc;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class TutorialValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Tutorial.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "NotBlank");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description",
                "NotBlank");
    }
}
