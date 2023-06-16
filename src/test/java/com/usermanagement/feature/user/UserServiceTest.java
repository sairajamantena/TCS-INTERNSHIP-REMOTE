package com.usermanagement.feature.user;

import com.usermanagement.feature.exception.UserAlreadyExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.usermanagement.feature.user.Role.RoleType.ROLE_APPLICATION_ADMIN;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Timur Berezhnoi
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldFailSignUpIfUserAlreadyExist() {
        // Given - setUp();
        var user = new User("prime@gmail.com", "password", null);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(new User("prime@gmail.com", "password", new Role(ROLE_APPLICATION_ADMIN))));

        // When
        assertThrows(UserAlreadyExistException.class, () -> userService.createUser(user), "Email is already in use!");
        // Then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldCreateUser() {
        // Given - setUp();
        var user = new User("newprime@gmail.com", "password", new Role(ROLE_APPLICATION_ADMIN));
        when(userRepository.save(any(User.class))).thenReturn(new User("email@g.com", "password", new Role(ROLE_APPLICATION_ADMIN)));
        when(roleService.findByRoleType(ROLE_APPLICATION_ADMIN)).thenReturn(new Role(ROLE_APPLICATION_ADMIN));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("passaskdfjbh23j");

        // When
        userService.createUser(user);

        // Then
        verify(userRepository, times(1)).save(any(User.class));
    }
}
