package nl.tudelft.sem;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;


public class ExpandJWTFilter extends ZuulFilter {
    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 999;
    }

    @Override
    public boolean shouldFilter() {
        // TODO Discuss whether we want to keep this.

        RequestContext ctx = RequestContext.getCurrentContext();
        var path = ctx.getRequest().getRequestURI().replace("/api/", "");
        var type = ctx.getRequest().getMethod();

        return (!path.equals("users") && !path.equals("users/login")) || !type.equals("POST");
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String token = getToken(ctx);

        if (verifyToken(token)) {
            DecodedJWT decodedJWT = JWT.decode(token);
            ctx.addZuulRequestHeader("x-user-role", decodedJWT.getClaim("userRole").asString());
            ctx.addZuulRequestHeader("x-user-name", decodedJWT.getClaim("userName").asString());
        } else {
            ctx.addZuulRequestHeader("x-user-role", null);
            ctx.addZuulRequestHeader("x-user-name", null);
        }
        return null;
    }

    /**
     * Get the token from the request context.
     *
     * @param ctx request context
     * @return token
     */
    private String getToken(RequestContext ctx) {
        String token = ctx.getRequest().getHeader("Authorization");
        return token.replace("Bearer ", "");
    }

    /**
     * Verify the JWT token.
     *
     * @param token token
     * @return true if token is valid
     */
    private boolean verifyToken(String token) {
        if (token != null) {
            System.out.println("Token: " + token);
            Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getJwtSecret());
            JWTVerifier verifier = JWT.require(algorithm).build();
            try {
                verifier.verify(token);
                return true;
            } catch (JWTVerificationException ignored) {
            }
        }
        return false;
    }
}
