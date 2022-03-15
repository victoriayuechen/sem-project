package nl.tudelft.sem.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class JwtUtil {
    private transient String secretKey = "Everything in this room is eatable. "
        + "Even I'm eatable. But that is called cannibalism, my dead children, "
        + "and is in fact frowned upon in most societies. - Mr.Willy Wonka";

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String getSecretKey() {
        return secretKey;
    }

}
