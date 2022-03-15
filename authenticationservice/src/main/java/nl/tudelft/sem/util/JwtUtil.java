package nl.tudelft.sem.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtUtil {
    private transient String secretKey = "Everything in this room is eatable. "
        + "Even I'm eatable. But that is called cannibalism, my dead children, "
        + "and is in fact frowned upon in most societies. - Mr.Willy Wonka";


    /** Retrieves the username from token.
     *
     * @param token the jwt token.
     * @return the username from the token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Retrieves the expiration date from token.
     *
     * @param token token.
     * @return the expiration date.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /** Return the claim.
     *
     * @param token token.
     * @param claimsResolver the function.
     * @param <T> the generic type.
     * @return the claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /** Retrieves all roles.
     *
     * @param token token.
     * @return all claims.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    /** Verifies if the token is expired or not.
     *
     * @param token token.
     * @return true if the token is expired.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /** Generates a jwt token.
     *
     * @param userDetails the user details object.
     * @return the token.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("roles", userDetails.getAuthorities());

        return createToken(claims, userDetails);
    }

    /** Creates the token.
     *
     * @param claims the roles.
     * @param userDetails the user details object.
     * @return the token.
     */
    private String createToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /** Validates the token.
     *
     * @param token the token.
     * @param userDetails the user details object.
     * @return true if the token is valid.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /** Gets the secret key.
     *
     * @return the secret key.
     */
    public String getSecretKey() {
        return this.secretKey;
    }

}
