package celtech.web;

import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;

/**
 *
 * @author Ian
 */
public class AllCookiePolicy implements CookiePolicy
{
    @Override
    public boolean shouldAccept(URI uri, HttpCookie cookie)
    {
        boolean accept = false;

        if (uri.getHost().equalsIgnoreCase("cel-robox.myminifactory.com"))
        {
            accept = true;
        }
        
        return accept;
    }
}
