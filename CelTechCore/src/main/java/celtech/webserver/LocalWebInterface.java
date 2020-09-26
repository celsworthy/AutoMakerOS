package celtech.webserver;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class LocalWebInterface
{

    private final Stenographer steno = StenographerFactory.getStenographer(LocalWebInterface.class.
        getName());
    private HttpServer server;

    public LocalWebInterface()
    {
        try
        {
            server = HttpServer.create(new InetSocketAddress(81), 0);
            HttpContext context = server.createContext("/", new AutoMakerController());
            context.setAuthenticator(new BasicAuthenticator("get")
            {
                @Override
                public boolean checkCredentials(String user, String pwd)
                {
                    return user.equals("admin") && pwd.equals("password");
                }
            });
        } catch (IOException ex)
        {
            steno.error("Unable to start local web server");
            ex.printStackTrace();
        }
    }

    public void start()
    {
        server.start();
    }

    public void stop()
    {
        server.stop(0);
    }
}
