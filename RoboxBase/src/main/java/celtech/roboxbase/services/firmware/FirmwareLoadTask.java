package celtech.roboxbase.services.firmware;

import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.exceptions.SDCardErrorException;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.SystemUtils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class FirmwareLoadTask extends Task<FirmwareLoadResult>
{

    private String firmwareFileToLoad = null;
    private final Stenographer steno = StenographerFactory.getStenographer(this.getClass().getName());
    private Printer printerToUpdate = null;

    /**
     * Modified so that this does not trigger the actual update - this should now be fired once this task reports success
     *
     * @param firmwareFileToLoad
     * @param printerToUpdate
     */
    public FirmwareLoadTask(String firmwareFileToLoad, Printer printerToUpdate)
    {
        this.firmwareFileToLoad = firmwareFileToLoad;
        this.printerToUpdate = printerToUpdate;
    }

    @Override
    protected FirmwareLoadResult call() throws Exception
    {
        ResourceBundle languageBundle = BaseLookup.getLanguageBundle();

        FirmwareLoadResult returnValue = new FirmwareLoadResult();

        try
        {
            File file = new File(firmwareFileToLoad);
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();

            int remainingBytes = fileData.length;
            int bufferPosition = 0;
            String firmwareID = SystemUtils.generate16DigitID();
            boolean sendOK = printerToUpdate.initialiseDataFileSend(firmwareID, false);

            if (sendOK)
            {
                updateTitle(languageBundle.getString("dialogs.firmwareUpdateProgressTitle"));
                updateMessage(languageBundle.getString("dialogs.firmwareUpdateProgressLoading"));

                while (bufferPosition < fileData.length && !isCancelled())
                {
                    updateProgress(bufferPosition, fileData.length);
                    byte byteToOutput = fileData[bufferPosition];
                    String byteAsString = String.format("%02X", byteToOutput);

                    printerToUpdate.sendDataFileChunk(byteAsString, remainingBytes == 1, false);

                    remainingBytes--;
                    bufferPosition++;
                }

                if (!isCancelled())
                {
                    printerToUpdate.transmitUpdateFirmware(firmwareID);
                    returnValue.setStatus(FirmwareLoadResult.SUCCESS);
                }
            }
        } catch (SDCardErrorException ex)
        {
            steno.error("SD card exception whilst updating firmware");
            returnValue.setStatus(FirmwareLoadResult.SDCARD_ERROR);
        } catch (RoboxCommsException ex)
        {
            steno.exception("Other comms exception whilst updating firmware ", ex);
            returnValue.setStatus(FirmwareLoadResult.OTHER_ERROR);
        } catch (IOException ex)
        {
            steno.error("Couldn't load firmware file " + ex.toString());
            returnValue.setStatus(FirmwareLoadResult.FILE_ERROR);
        }

        return returnValue;
    }
}
