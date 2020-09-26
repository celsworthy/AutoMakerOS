package celtech.roboxremote;

import celtech.roboxbase.printerControl.PrintJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.parboiled.common.FileUtils;

/**
 *
 * @author Ian
 */
public class PrintJobPersister
{

    private static final Stenographer steno = StenographerFactory.getStenographer(PrintJobPersister.class.getName());

    private static PrintJobPersister instance = null;
    private String printJobIDBeingPersisted = null;
    private BufferedWriter printJobWriter = null;

    private PrintJobPersister()
    {
    }

    public static PrintJobPersister getInstance()
    {
        if (instance == null)
        {
            instance = new PrintJobPersister();
        }

        return instance;
    }

    public void startFile(String printJobID)
    {
        try
        {
            if (printJobWriter != null)
            {
                //Let's close this file and flag the unusual circumstance...
                printJobWriter.close();
                steno.error("Found a partially persisted remote file for job " + printJobIDBeingPersisted);
            }

//                        steno.info("Receiving print job " + remoteTx.getMessagePayload());
            printJobIDBeingPersisted = printJobID;
            PrintJob pj = new PrintJob(printJobIDBeingPersisted);

            FileUtils.forceMkdir(new File(pj.getRoboxisedFileLocation()).getParentFile());
            FileWriter fw = new FileWriter(pj.getRoboxisedFileLocation());
            printJobWriter = new BufferedWriter(fw);
        } catch (IOException ex)
        {
            steno.exception("Error when attempting to persist remote file " + printJobIDBeingPersisted, ex);
        }
    }

    public void writeSegment(String segment)
    {
        //                    steno.info("Got chunk " + payload);
        if (printJobWriter != null)
        {
            try
            {
                printJobWriter.write(segment);
            } catch (IOException ex)
            {
                steno.exception("Error when attempting to persist remote file " + printJobIDBeingPersisted, ex);
            }
        } else
        {
            steno.error("Unable to process remote file segment - no local file open.");
        }
    }

    public void closeFile(String segment)
    {
        steno.info("End of print job " + printJobIDBeingPersisted);

        if (printJobWriter != null)
        {
            try
            {
                printJobWriter.write(segment);
                printJobWriter.flush();
                printJobWriter.close();
                printJobWriter = null;
                printJobIDBeingPersisted = null;
            } catch (IOException ex)
            {
                steno.exception("Error when attempting to persist remote file " + printJobIDBeingPersisted, ex);
            }
        } else
        {
            steno.error("Unable to process end of remote file - no local file open.");
        }
    }
    
    public String getPrintJobID()
    {
        return printJobIDBeingPersisted;
    }
}
