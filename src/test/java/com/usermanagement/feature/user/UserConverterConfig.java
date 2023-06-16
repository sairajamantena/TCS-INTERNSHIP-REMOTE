package com.usermanagement.feature.user;

import com.usermanagement.feature.user.UserConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Timur Berezhnoi.
 */
@Configuration
public class UserConverterConfig {

    @Bean
    public UserConverter userConverter() {
        return new UserConverter();
    }
}
