package nl.tudelft.sem.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.entities.User;
import nl.tudelft.sem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginDetailsService implements UserDetailsService {
    @Autowired
    private transient UserRepository userRepo;

    /** Constructor for mocking.
     *
     * @param userRepository userRepository.
     */
    public LoginDetailsService(UserRepository userRepository) {
        this.userRepo = userRepository;
    }

    /** Retrieves the user from the database and
     * converts it into a UserDetails object.
     *
     * @param username the username of the user.
     * @return a UserDetails object of our user.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepo.findByUsername(username);

        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + username));

        return new org.springframework.security.core
                .userdetails.User(user.get().getUsername(), user
                .get().getPassword(), getGrantedAuthorities(user.get()));
    }

    /** This method transforms the role of a user into Spring-Security
     * readable authorities.
     *
     * @param user the user to get the role from.
     * @return a collection of granted authorities.
     */
    public Collection<GrantedAuthority> getGrantedAuthorities(User user) {
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

}
