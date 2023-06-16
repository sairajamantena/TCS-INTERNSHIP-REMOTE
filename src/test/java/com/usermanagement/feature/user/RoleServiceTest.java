package com.usermanagement.feature.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.util.Assert.notNull;

/**
 * @author Timur Berezhnoi
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void shouldReturnRole() {
        Role.RoleType roleUser = Role.RoleType.ROLE_APPLICATION_ADMIN;
        when(roleRepository.findByRoleType(roleUser)).thenReturn(Optional.of(new Role(roleUser)));
        notNull(roleService.findByRoleType(roleUser), "Something went wrong...");
    }
}
