package nl.tudelft.sem.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Claims;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import nl.tudelft.sem.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class JwtUtilTest {

    private transient User user;
    private transient JwtUtil token;
    private transient String tokenStr;
    private transient UserDetails userDetails;

    private static final String SECRET_KEY = "Everything in this room is eatable. "
            + "Even I'm eatable. But that is called cannibalism, my dead children, "
            + "and is in fact frowned upon in most societies. - Mr.Willy Wonka";

    /** Initializes multiple instances of users at before each test.
     */
    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("admin");
        user.setPassword("adminpassword");
        Set<String> roles2 = new HashSet<>();
        roles2.add("admin");
        user.setRoles(roles2);

        userDetails = new org.springframework.security.core
                .userdetails.User(user.getUsername(), user
                .getPassword(), getGrantedAuthorities(user));

        token = new JwtUtil();
        tokenStr = token.generateToken(userDetails);
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
    public void extractUsernameTest() {
        String username = token.extractUsername(tokenStr);

        assertEquals(username, "admin");
    }

    @Test
    public void extractExpirationTest() {
        assertThat(token.extractExpiration(tokenStr))
                .isAfter(new Date(System.currentTimeMillis()));
    }

    @Test
    void extractClaim() {
        assertThat(token.extractClaim(tokenStr, Claims::getSubject))
                .isEqualTo("admin");
    }

    @Test
    public void getSecretKeyTest() {
        assertEquals(token.getSecretKey(), SECRET_KEY);
    }

    @Test
    public void validateTokenTest() {
        assertTrue(token.validateToken(tokenStr, userDetails));
    }

    @Test
    public void validateTokenFalseTest() {
        UserDetails userFalse = new org.springframework.security.core
                .userdetails.User("false", user
                .getPassword(), getGrantedAuthorities(user));

        assertFalse(token.validateToken(tokenStr, userFalse));
    }

    @Test
    public void generateTokenTest() {
        assertEquals(token.generateToken(userDetails), tokenStr);
    }
}
