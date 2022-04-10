package com.example.batch.batch.process.validating;

import com.example.batch.batch.Entity.User;
import com.example.batch.batch.process.filter.EmployeeNameFilterItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

public class UserValidator implements Validator<User> {
    private static final Logger log = LoggerFactory.getLogger(UserValidator.class);
    @Override
    public void validate(User user) throws ValidationException {
        if (user.getEmail().isEmpty()) {
            log.info("validating data user data.....{}", user);
            throw new ValidationException("firstName must not be null");
        }
    }
}
