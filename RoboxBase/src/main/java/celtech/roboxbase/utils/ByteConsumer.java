package celtech.roboxbase.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Tony Aldhous
 */
class ByteConsumer extends Thread
{
    private final InputStream is;
    private final Stenographer steno = StenographerFactory.getStenographer(this.getClass().getName());
    private final ByteArrayOutputStream scriptOutput = new ByteArrayOutputStream();
    
    public ByteConsumer(InputStream is)
    {
        this.is = is;
    }

    public byte[] getBytes()
    {
        try {
            this.join();
        }
        catch (InterruptedException ex) {
        }

        return scriptOutput.toByteArray();
    }

    @Override
    public void run()
    {
        try
        {
            byte[] data = new byte[1024];
            int bytesRead = is.read(data);
            while(bytesRead != -1) {
                //STENO.debug("Read " + bytesRead + " from input stream");
                scriptOutput.write(data, 0, bytesRead);
                bytesRead = is.read(data);
            }
        } catch (IOException ioe)
        {
            steno.error(ioe.getMessage());
        }
    }
}
