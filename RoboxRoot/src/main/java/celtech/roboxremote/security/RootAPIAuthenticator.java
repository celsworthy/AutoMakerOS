package celtech.roboxremote.security;

import celtech.roboxremote.Root;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Ian
 */
public class RootAPIAuthenticator implements Authenticator<BasicCredentials, User>
{
    private static final Map<String, Set<String>> VALID_USERS = ImmutableMap.of(
            "root", ImmutableSet.of()
    );

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException
    {
        String rootPIN = Root.getInstance().getApplicationPIN();
        if (VALID_USERS.containsKey(credentials.getUsername()) && rootPIN.equals(credentials.getPassword()))
        {
            return Optional.of(new User(credentials.getUsername()));
        }
        return Optional.empty();
    }
}
