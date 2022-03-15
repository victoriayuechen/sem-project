package nl.tudelft.sem.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import nl.tudelft.sem.entities.User;
import nl.tudelft.sem.service.LoginDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class JwtRequestFilterTest {

    private transient LoginDetailsService userService =
            Mockito.mock(LoginDetailsService.class);

    private transient JwtUtil token =
            Mockito.mock(JwtUtil.class);

    private transient JwtRequestFilter filter =
            Mockito.mock(JwtRequestFilter.class);

    private transient User user1;
    private transient MockHttpServletRequest request;
    private transient MockHttpServletResponse response;
    private transient MockFilterChain chain;


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

        request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + TOKEN);
        request.setRequestURI("/api/test");
        response = new MockHttpServletResponse();
        chain = new MockFilterChain();
        SecurityContextHolder.getContext().setAuthentication(null);
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

    @Test
    public void doFilterTestMissingtokenTest() throws Exception {
        UserDetails user = new org.springframework.security.core
                .userdetails.User(user1.getUsername(), user1
                .getPassword(), getGrantedAuthorities(user1));

        when(userService.loadUserByUsername(user1.getUsername()))
                .thenReturn(user);

        when(token.generateToken(user))
                .thenReturn(null);

        filter.doFilterInternal(request, response, chain);

        verify(filter, times(1)).doFilterInternal(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void testJwtFilter() throws Exception {

        UserDetails user = new org.springframework.security.core
                .userdetails.User(user1.getUsername(), user1
                .getPassword(), getGrantedAuthorities(user1));

        filter.doFilterInternal(request, response, chain);

        when(userService.loadUserByUsername(user1.getUsername()))
                .thenReturn(user);

        when(token.generateToken(user))
                .thenReturn(TOKEN);

        when(token.validateToken(TOKEN, user))
                .thenReturn(true);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        verify(filter, times(1)).doFilterInternal(request, response, chain);
    }
}
