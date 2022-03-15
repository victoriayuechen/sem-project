package nl.tudelft.sem.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class TokenFilter extends OncePerRequestFilter {
    private transient JwtUtil jwtUtil = new JwtUtil();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            String token = authorizationHeader.substring(7);
            final JwtParser parser = Jwts.parser().setSigningKey(jwtUtil.getSecretKey());
            Jws<Claims> jws = parser.parseClaimsJws(token);

            Claims claims = jws.getBody();
            String username = (String) claims.get("username");
            List<String> actualRoles = new LinkedList<>();
            List<LinkedHashMap<String, String>> roles =
                (ArrayList<LinkedHashMap<String, String>>) claims.get("roles");

            for (var entry : roles) {
                actualRoles.add(entry.get("authority"));
            }

            final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            actualRoles.forEach(r -> {
                authorities.add(new SimpleGrantedAuthority(r));
            });

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);

        } else {
            filterChain.doFilter(request, response);
        }
    }

}
