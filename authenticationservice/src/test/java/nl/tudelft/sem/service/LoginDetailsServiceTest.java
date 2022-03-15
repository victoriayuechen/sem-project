package nl.tudelft.sem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import nl.tudelft.sem.entities.User;
import nl.tudelft.sem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class LoginDetailsServiceTest {
    private transient UserRepository userRepository =
            Mockito.mock(UserRepository.class);

    private transient LoginDetailsService userService =
            new LoginDetailsService(userRepository);

    private transient User user1;
    private transient User user2;
    private transient User user3;
    private transient User user4;

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

        user3 = new User();
        user3.setUsername("yoda");
        user3.setPassword("password1");
        Set<String> roles3 = new HashSet<>();
        roles3.add("LecTureR");
        user3.setRoles(roles3);

        user4 = new User();
        user4.setUsername("ta");
        user4.setPassword("passwordta");
        Set<String> roles4 = new HashSet<>();
        roles4.add("ta");
        user4.setRoles(roles4);
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
    public void loadUserSuccessfullyTest() throws Exception {

        when(userRepository.findByUsername("rmihalachiuta"))
                .thenReturn(Optional.of(user1));

        UserDetails user = new org.springframework.security.core
                .userdetails.User(user1.getUsername(), user1
                .getPassword(), getGrantedAuthorities(user1));

        assertEquals(user, userService.loadUserByUsername(user1.getUsername()));
    }

    @Test
    public void loadUserExceptionTest() throws Exception {

        when(userRepository.findByUsername("rmihalachiuta"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("rmihalachiuta")
        );
    }

    @Test
    public void convertRolesTest() {
        Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();

        grantedAuthority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        assertEquals(grantedAuthority, userService.getGrantedAuthorities(user2));
    }


    @Test
    public void convertRolesTest2() {
        Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();

        grantedAuthority.add(new SimpleGrantedAuthority("ROLE_STUDENT"));
        grantedAuthority.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        assertEquals(grantedAuthority, userService.getGrantedAuthorities(user1));
    }

    @Test
    public void convertRolesIgnoreCaseTest() {
        Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();

        grantedAuthority.add(new SimpleGrantedAuthority("ROLE_LECTURER"));

        assertEquals(grantedAuthority, userService.getGrantedAuthorities(user3));
    }

    @Test
    public void convertTaRoleTest() {
        Collection<GrantedAuthority> grantedAuthority = new ArrayList<>();

        grantedAuthority.add(new SimpleGrantedAuthority("ROLE_TA"));

        assertEquals(grantedAuthority, userService.getGrantedAuthorities(user4));
    }
}
