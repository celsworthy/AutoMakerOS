package celtech.roboxbase.comms;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

/**
 *
 * @author Ian
 */
public class RootWebAuthenticator extends Authenticator
{

    @Override
    protected URL getRequestingURL()
    {
        System.out.println("ello");
        return super.getRequestingURL(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected RequestorType getRequestorType()
    {
        System.out.println("ello");
        return super.getRequestorType(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication()
    {
        System.out.println("ello");
        return new PasswordAuthentication("root", "1111".toCharArray());
    }

}
