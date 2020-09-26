package celtech.roboxremote.security;

import java.security.Principal;
import java.util.Set;

/**
 *
 * @author Ian
 */
public class User implements Principal
{

    private final String name;

    public User(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return (int) (Math.random() * 100);
    }
}
