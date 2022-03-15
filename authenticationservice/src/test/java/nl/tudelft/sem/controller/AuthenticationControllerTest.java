package nl.tudelft.sem.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.entities.User;
import nl.tudelft.sem.repositories.UserRepository;
import nl.tudelft.sem.service.LoginDetailsService;
import nl.tudelft.sem.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthenticationController.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class AuthenticationControllerTest {

    @MockBean
    private transient UserRepository userRepository;
    @MockBean
    private transient LoginDetailsService userService;
    @MockBean
    private transient JwtUtil token;
    @MockBean
    private transient BCryptPasswordEncoder encoder;
    @MockBean
    private transient AuthenticationManager manager;
    @Autowired
    private transient MockMvc mockMvc;

    private transient User user1;
    private transient User user2;

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJz"
            + "dWIiOiJhbm5pYmFsZSIsImV4cCI6MTY0MDMw"
            + "NjkyMywiaWF0IjoxNjQwMjcwOTIzfQ.Ab3qsQdzo"
            + "U8viZwWtnFf9NqIG9GDsSssTxrjyXj_8Dg";

    /** Initializes multiple instances of users at before each test.
     */
    @BeforeEach
    public void setup() {
        user1 = new User();
        user1.setUsername("rmihalachiuta");
        user1.setPassword("pass");
        Set<String> roles = new HashSet<>();
        roles.add("student");
        roles.add("admin");
        user1.setRoles(roles);

        user2 = new User();
        user2.setUsername("willywonka");
        user2.setPassword("password");
        Set<String> roles2 = new HashSet<>();
        roles2.add("admin");
        user2.setRoles(roles2);
    }

    /** Generic JSON parser.
     *
     * @param obj Object of any class
     * @return JSON String used for requests/response
     */
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "-";
    }

    /** This method transforms the role of a user into Spring-Security
     * readable authorities.
     *
     * @param user the user to get the role from.
     * @return a collection of granted authorities.
     */
    private Collection<GrantedAuthority> getGrantedAuthorities(User user) {
        Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();
        List<String> givenRoles = new LinkedList<>(user.getRoles());

        for (String role : givenRoles) {
            if (role.equalsIgnoreCase("student")) {
                grantedAuthority.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
            } else if (role.equalsIgnoreCase("lecturer")) {
                grantedAuthority.add(new SimpleGrantedAuthority("ROLE_LECTURER"));
            } else if (role.equalsIgnoreCase("admin")) {
                grantedAuthority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            } else if (role.equalsIgnoreCase("ta")) {
                grantedAuthority.add(new SimpleGrantedAuthority("ROLE_TA"));
            }
        }

        return grantedAuthority;
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void saveUserTest() throws Exception {
        when(userRepository.findByUsername("rmihalachiuta"))
                .thenReturn(Optional.of(user1));

        User result = new User();

        result.setUsername(user1.getUsername());
        result.setPassword(encoder.encode(user1.getPassword()));
        result.setRoles(user1.getRoles());

        mockMvc.perform(post("/authentication/save")
                        .content(AuthenticationControllerTest.asJsonString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(AuthenticationControllerTest.asJsonString(result)));
    }

    @Test
    public void loginSuccessfulTest() throws Exception {

        UserDetails user = new org.springframework.security.core
                .userdetails.User(user2.getUsername(), user2
                .getPassword(), getGrantedAuthorities(user2));

        when(userService.loadUserByUsername(user2.getUsername()))
                .thenReturn(user);

        when(token.generateToken(user))
                .thenReturn(TOKEN);

        when(token.validateToken(TOKEN, user))
                .thenReturn(true);

        mockMvc.perform(post("/authentication/login")
                        .content(AuthenticationControllerTest.asJsonString(user2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(manager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(user2.getUsername(), user2.getPassword()));
    }

    @Test
    public void loginFailureTest() throws Exception {

        UserDetails user = new org.springframework.security.core
                .userdetails.User(user2.getUsername(), user2
                .getPassword(), getGrantedAuthorities(user2));

        when(userService.loadUserByUsername(user2.getUsername()))
                .thenReturn(user);

        when(token.generateToken(user))
                .thenReturn(TOKEN);

        when(token.validateToken(TOKEN, user))
                .thenReturn(false);

        mockMvc.perform(post("/authentication/login")
                        .content(AuthenticationControllerTest.asJsonString(user2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(manager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(user2.getUsername(), user2.getPassword()));
    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addRoleSuccessTest() throws Exception {
        User victoria = new User();
        victoria.setRoles(new HashSet<>());
        when(userRepository.findByUsername("victoria"))
            .thenReturn(Optional.of(victoria));
        victoria.addRole("ta");

        mockMvc.perform(put("/authentication/add-role-ta/victoria")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(asJsonString(victoria)));

    }

    @WithMockUser(roles = "ADMIN")
    @Test
    public void addRoleNoUserTest() throws Exception {
        when(userRepository.findByUsername("victoria"))
            .thenReturn(Optional.empty());

        mockMvc.perform(put("/authentication/add-role-ta/victoria")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

    }
}
