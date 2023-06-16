package com.usermanagement.dto.user;

import com.usermanagement.feature.exception.UserAlreadyExistException;
import com.usermanagement.feature.user.UserDTO;
import com.usermanagement.infrastructure.configuration.MessageSourceConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.Assert.assertEquals;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

/**
 * @author Timur Berezhnoi
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MessageSourceConfiguration.class})
class UserDTOTest {

    @Autowired
    private MessageSource messageSource;

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void shouldFailIfEmailIsInvalid() throws UserAlreadyExistException {
        // Given - setUp();
        UserDTO user = new UserDTO("invalid email format", "password");
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(1, constraintViolations.size());
        assertEquals("Email is in wrong format.", messageSource.getMessage(constraintViolations.iterator().next().getMessage(), new Object[] {}, getLocale()));
    }

    @Test
    void shouldFailIfEmailIsMoreThan40Symbols() throws UserAlreadyExistException {
        // Given - setUp();
        UserDTO user = new UserDTO("sdrfsderawewewewewewdaaaaaaaaaaaxsa@g.com", "password");
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(1, constraintViolations.size());
        assertEquals("Email min/max length is 6/40.", messageSource.getMessage(constraintViolations.iterator().next().getMessage(), new Object[] {}, getLocale()));
    }

    @Test
    void shouldFailIfEmailIsLessThan6Symbols() throws UserAlreadyExistException {
        // Given - setUp();
        UserDTO user = new UserDTO("a@g.c", "password");
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(1, constraintViolations.size());
        assertEquals("Email min/max length is 6/40.", messageSource.getMessage(constraintViolations.iterator().next().getMessage(), new Object[] {}, getLocale()));
    }

    @Test
    void shouldFailIfEmailIsNull() throws UserAlreadyExistException {
        // Given - setUp();
        UserDTO user = new UserDTO(null, "password");
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(1, constraintViolations.size());
        assertEquals("Email can't be null.", messageSource.getMessage(constraintViolations.iterator().next().getMessage(), new Object[] {}, getLocale()));
    }

    @Test
    void shouldFailIfPasswordIsNull() throws UserAlreadyExistException {
        // Given - setUp();
        UserDTO user = new UserDTO("tima@g.com", null);
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(1, constraintViolations.size());
        assertEquals("Password can't be null.", messageSource.getMessage(constraintViolations.iterator().next().getMessage(), new Object[] {}, getLocale()));
    }

    @Test
    void shouldFailIfPasswordIsMoreThan100Characters() {
        // Given - setUp();
        UserDTO user = new UserDTO("tima@g.com", "qwertyuiopasdfghqwertyuiop" +
                "                                                         asdfghqwertyuiopasdfghqwert" +
                "                                                         yuiopasdfghqwertyuiopasdfghqwertyuiopasdfghqwertyui");
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(1, constraintViolations.size());
        assertEquals("Password min/max length is 5/20.", messageSource.getMessage(constraintViolations.iterator().next().getMessage(), new Object[] {}, getLocale()));
    }

    @Test
    void shouldFailIfPasswordIsLessThan6Characters() throws UserAlreadyExistException {
        // Given - setUp();
        UserDTO user = new UserDTO("tima@g.com", "qwer");
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(1, constraintViolations.size());
        assertEquals("Password min/max length is 5/20.", messageSource.getMessage(constraintViolations.iterator().next().getMessage(), new Object[] {}, getLocale()));
    }

    @Test
    void someCombainedValidation() {
        // Given - setUp();
        UserDTO user = new UserDTO("t@g.c", null);
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(2, constraintViolations.size());
    }

    @Test
    void userRegistrationDTOValid() {
        UserDTO user = new UserDTO("tima@g.com", "password");
        Set<ConstraintViolation<UserDTO>> constraintViolations = validator.validate(user);

        // Then
        assertEquals(0, constraintViolations.size());
    }
}
