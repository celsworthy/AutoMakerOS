package celtech.roboxremote.security;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.auth.Authenticator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.JwtContext;

/**
 *
 * @author Ian
 */
public class JWTAuthenticator implements Authenticator<JwtContext, User>
{
    private static final Map<String, Set<String>> VALID_USERS = ImmutableMap.of(
            "root", ImmutableSet.of()
    );
    
    @Override
    public Optional<User> authenticate(JwtContext context)
    {
            // Provide your own implementation to lookup users based on the principal attribute in the
        // JWT Token. E.g.: lookup users from a database etc.
        // This method will be called once the token's signature has been verified

            // In case you want to verify different parts of the token you can do that here.
        // E.g.: Verifying that the provided token has not expired.
        // All JsonWebTokenExceptions will result in a 401 Unauthorized response.
        try
        {
            final String subject = context.getJwtClaims().getSubject();
            
            if ("good-guy".equals(subject))
            {
                return Optional.of(new User("root"));
            }
            return Optional.empty();
        } catch (MalformedClaimException e)
        {
            return Optional.empty();
        }
    }
}
