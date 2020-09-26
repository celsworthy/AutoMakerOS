package celtech.roboxbase.services.firmware;

import celtech.roboxbase.comms.rx.FirmwareResponse;

/**
 *
 * @author Ian
 */
public class FirmwareLoadResult
{
    /**
     *
     */
    public static final int SDCARD_ERROR = -1;

    /**
     *
     */
    public static final int SUCCESS = 0;

    /**
     *
     */
    public static final int FILE_ERROR = -2;

    /**
     *
     */
    public static final int OTHER_ERROR = -3;
    
    private int status = OTHER_ERROR;
    private FirmwareResponse response = null;
    private String firmwareID;

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public FirmwareResponse getResponse()
    {
        return response;
    }

    public void setResponse(FirmwareResponse response)
    {
        this.response = response;
    }

    public String getFirmwareID()
    {
        return firmwareID;
    }

    public void setFirmwareID(String firmwareID)
    {
        this.firmwareID = firmwareID;
    }
}
