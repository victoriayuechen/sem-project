package nl.tudelft.sem.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class UserTest {

    private transient User user1;
    private transient User user2;
    private transient User user3;
    private transient User user4;
    private transient User user5;
    private transient User user6;

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
        user2.setUsername("rmihalachiuta");
        user2.setPassword("pass");
        Set<String> roles2 = new HashSet<>();
        roles2.add("student");
        roles2.add("admin");
        user2.setRoles(roles2);

        user3 = new User();
        user3.setUsername("yoda");
        user3.setPassword("password1");
        Set<String> roles3 = new HashSet<>();
        roles3.add("LecTureR");
        user3.setRoles(roles3);

        user4 = new User();
        user4.setUsername("rmihalachiuta");
        user4.setPassword("pass");
        Set<String> roles4 = new HashSet<>();
        roles4.add("ta");
        user4.setRoles(roles4);

        user5 = new User();
        user5.setUsername("rmihalachiuta");
        user5.setPassword("password");
        Set<String> roles5 = new HashSet<>();
        roles5.add("student");
        roles5.add("admin");
        user5.setRoles(roles5);

        user6 = new User();
    }

    @Test
    public void constructorTest() {
        assertEquals(user6, new User());
    }

    @Test
    public void constructor2Test() {
        Set<String> roles4 = new HashSet<>();
        roles4.add("ta");
        assertEquals(user4, new User("rmihalachiuta", "pass", roles4));
    }


    @Test
    public void hashCodeTest() {
        int hash = Objects.hash(user1.getUsername(), user1.getPassword(),
                                    user1.getRoles());

        assertEquals(user1.hashCode(), hash);
    }

    @Test
    public void equalsTest() {
        assertEquals(user1, user2);
    }

    @Test
    public void equalsSameTest() {
        assertEquals(user1, user1);
    }

    @Test
    public void equalsNullTest() {
        assertNotEquals(user2, null);
    }

    @Test
    public void notEqualsUsernameTest() {
        assertNotEquals(user1, user3);
    }

    @Test
    public void notEqualsRolesTest() {
        assertNotEquals(user1, user3);
    }

    @Test
    public void notEqualsPassTest() {
        assertNotEquals(user1, user5);
    }
}
