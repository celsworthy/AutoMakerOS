package celtech.WebEngineFix;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 *
 * @author Ian
 */
public class AMURLStreamHandlerFactory implements URLStreamHandlerFactory
{
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol)
    {
        if (protocol.equals("http"))
        {
            return new AMURLHandler();
        }
        return null;
    }

}