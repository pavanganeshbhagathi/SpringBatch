package com.example.batch.batch.process.filter;

import com.example.batch.batch.Entity.Profile;
import com.example.batch.batch.Entity.User;
import com.example.batch.batch.process.ProfileItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.Arrays;
import java.util.List;

public class EmployeeNameFilterItemProcessor implements ItemProcessor<User, User> {

    private static final Logger log = LoggerFactory.getLogger(EmployeeNameFilterItemProcessor.class);
    private static final List<String> NAMES_TO_EXCLUDE = Arrays.asList("pavan", "ganesh");
    @Override
    public User process(User user) throws Exception {

        log.info("filtering user data.....{}", user);
        if (NAMES_TO_EXCLUDE.contains(user.getFirstName()) || NAMES_TO_EXCLUDE.contains(user.getFirstName())) {
            return null;
        }

        return user;
    }
}
