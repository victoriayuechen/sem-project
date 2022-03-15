package nl.tudelft.sem.entities;

import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
    @Column(name = "user_role")
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private Set<String> roles;

    public User() {
    }

    /**
     * Constructor for a User object.
     *
     * @param username the username of the user.
     * @param password the password of the user.
     * @param roles    the role of the user.
     */
    public User(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public void addRole(String role) {
        this.roles.add(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(username, user.username)
            && Objects.equals(password, user.password)
            && Objects.equals(roles, user.roles);
    }

    @Override
    public int hashCode() {

        return Objects.hash(username, password, roles);
    }
}

