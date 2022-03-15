package nl.tudelft.sem.controller;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.ErrorString;
import nl.tudelft.sem.entities.User;
import nl.tudelft.sem.repositories.UserRepository;
import nl.tudelft.sem.service.LoginDetailsService;
import nl.tudelft.sem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/authentication")
public class AuthenticationController {

    @Autowired
    private transient AuthenticationManager authenticationManager;

    @Autowired
    private transient JwtUtil token;

    @Autowired
    private transient LoginDetailsService userDetailsService;

    @Autowired
    private transient BCryptPasswordEncoder encoder;

    @Autowired
    private transient UserRepository userRepository;

    /** Constructor for mocking.
     *
     * @param userRepository userRepository.
     * @param userService the user service.
     * @param manager the authentication manager.
     * @param token the JWT token.
     * @param encoder the password encoder.
     */
    public AuthenticationController(UserRepository userRepository,
                                    LoginDetailsService userService,
                                    AuthenticationManager manager,
                                    JwtUtil token,
                                    BCryptPasswordEncoder encoder) {
        this.userDetailsService = userService;
        this.authenticationManager = manager;
        this.encoder = encoder;
        this.userRepository = userRepository;
        this.token = token;
    }

    /**
     * Saves a user to the database.
     * Encrypts their password too.
     * This represents the authorization component in the COR pattern.
     * It is achieved through the @PreAuthorize annotation.
     *
     * @param user the user to be saved to the database.
     * @return the user.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body(user);
    }

    /**
     * This method does the validation of the token,
     * as well as the generation of the token which will be used for further authorization.
     *
     * @param user     the user to login.
     * @param response the response.
     * @return the JWT token for the user.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody User user,
                                                       HttpServletResponse response)
        throws Exception {

        // authentication component in COR pattern
        authenticateComponent(user);

        final UserDetails userDetails = userDetailsService
            .loadUserByUsername(user.getUsername());

        final String jwt = token.generateToken(userDetails);

        //finally, the token is sent to the client, if the token is valid.
        if (token.validateToken(jwt, userDetails)) {
            return ResponseEntity.ok(jwt);
        } else {
            return ResponseEntity.badRequest()
                .body(new ErrorString("The token is not valid."));
        }

    }

    /**
     * This method checks the credentials of a user.
     *
     * @param user the user to be authenticated.
     * @throws Exception bad credentials exception.
     */
    private void authenticateComponent(User user) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password.");
        }
    }

    /**
     * This method allows a user to take on a TA role.
     *
     * @param request  The HTTP request.
     * @param username The username for whom there is a role upgrade.
     * @return The response.
     */
    @PreAuthorize("hasAnyAuthority('ROLE_LECTURER', 'ROLE_ADMIN')")
    @PutMapping("/add-role-ta/{username}")
    public ResponseEntity<?> addTaRole(HttpServletRequest request,
                                       @PathVariable String username) {
        Optional<User> targetUser = userRepository.findByUsername(username);

        if (targetUser.isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorString("No user with such username found."));
        }

        User addedTa = targetUser.get();
        addedTa.addRole("ta");
        userRepository.save(addedTa);

        return ResponseEntity
            .ok()
            .body(addedTa);
    }
}
