package com.usermanagement.feature.user;

import com.usermanagement.feature.user.UserConverter;
import com.usermanagement.feature.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Timur Berezhnoi.
 */
class UserConverterTest {

    private final UserConverter userConverter = new UserConverter();

    @Test
    void shouldMapUserRegistrationDtoToUserEntity() {
        var email = "email@g.com";
        var password = "password";
        var user = userConverter.createFrom(new UserDTO(email, password));

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
    }

}
