package com.usermanagement.feature.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.feature.constants.EndpointPath;
import com.usermanagement.feature.exception.UserAlreadyExistException;
import com.usermanagement.infrastructure.ControllerAdvice;
import com.usermanagement.infrastructure.configuration.MessageSourceConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Timur Berezhnoi.
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ContextConfiguration(classes = {MessageSourceConfiguration.class})
class UserControllerTest {

    private MockMvc mockMvc;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private UserService userService;

    @Mock
    private UserConverter userConverter;

    @Autowired
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, userConverter))
                .setUseSuffixPatternMatch(false)
                .setControllerAdvice(new ControllerAdvice(messageSource)).build();
    }

    @Test
    @DisplayName("Create user should fails if user with the email is already signed up.")
    void createUserShouldFails() throws Exception {
        // Given
        var userDTO = new UserDTO("prime@gail.com", "password");
        var requestBody = objectMapper.writeValueAsString(userDTO);

        when(userConverter.createFrom(any(UserDTO.class))).thenReturn(new User());
        doThrow(new UserAlreadyExistException("Email is already in use!")).when(userService).createUser(any(User.class));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", is("Email is already in use!")));

        verify(userService, only()).createUser(any(User.class));
    }

    @Test
    void createUserShouldFails_whenEmailIsNull() throws Exception {
        // Given
        var requestBody = objectMapper.writeValueAsString(new UserDTO(null, "password"));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].message", is("Email can't be null.")));

        verifyZeroInteractions(userService);
    }

    @Test
    void createUserShouldFails_whenEmailPatternIsInvalid() throws Exception {
        // Given
        var requestBody = objectMapper.writeValueAsString(new UserDTO("invalidEmail", "password"));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].message", is("Email is in wrong format.")));

        verifyZeroInteractions(userService);
    }

    @Test
    void createUserShouldFails_whenEmailLengthIsLessThan4Chars() throws Exception {
        // Given
        var requestBody = objectMapper.writeValueAsString(new UserDTO("e@g.c", "password"));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].message", is("Email min/max length is 6/40.")));

        verifyZeroInteractions(userService);
    }

    @Test
    void createUserShouldFails_whenEmailLengthIsMoreThan40Chars() throws Exception {
        // Given
        var requestBody = objectMapper.writeValueAsString(new UserDTO("ssxcsdsdsdwjyweessssadasdasdasdasdasd@g.c", "password"));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].message", is("Email min/max length is 6/40.")));

        verify(userService, never()).createUser(any(User.class));
    }

    @Test
    void createUserShouldFails_whenPasswordIsNull() throws Exception {
        // Given
        var requestBody = objectMapper.writeValueAsString(new UserDTO("essdasd@g.c", null));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].message", is("Password can't be null.")));

        verifyZeroInteractions(userService);
    }

    @Test
    void createUserShouldFails_whenPasswordIsLessThan5Chars() throws Exception {
        // Given
        var requestBody = objectMapper.writeValueAsString(new UserDTO("essdasd@g.c", "sass"));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].message", is("Password min/max length is 5/20.")));

        verifyZeroInteractions(userService);
    }

    @Test
    void createUserShouldFails_whenPasswordIsMoreThan100Chars() throws Exception {
        // Given
        var requestBody = objectMapper.writeValueAsString(new UserDTO("essdasd@g.c", "sdrfsderawewewewewewdaaaaaaaaaaaxsaxzxasdasdw" +
                "khjeyutfdghsdrfsderawewewewewewdaaaaaaaaaaaxsaxzxasdasdwkhjeyutfdgh"));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$.[0].message", is("Password min/max length is 5/20.")));

        verifyZeroInteractions(userService);
    }

    @Test
    void createUserShouldFails_whenPasswordAndEmailAreNull() throws Exception {
        // Given
        var requestBody = objectMapper.writeValueAsString(new UserDTO(null, null));

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[*].message", containsInAnyOrder("Email can't be null.", "Password can't be null.")));

        verifyZeroInteractions(userService);
    }

    @Test
    void createUserShouldSuccess() throws Exception {
        // Given
        var email = "email@g.com";
        var password = "password";
        var userDTO = new UserDTO(email, password);
        var requestBody = objectMapper.writeValueAsString(userDTO);

        when(userConverter.createFrom(any(UserDTO.class))).thenReturn(new User());

        // When - Then
        mockMvc.perform(post(EndpointPath.USER).content(requestBody).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(userService, only()).createUser(any(User.class));
        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        // Given
        var email = "email@gmail.com";

        // When - Then
        mockMvc.perform(delete(EndpointPath.USER + "/{email}", email).principal(() -> "loggedin@g.com"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(email);
    }
}
