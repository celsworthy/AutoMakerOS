package celtech.roboxremote.rootDataStructures;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is a localised form of FirmwareError, which is 
 * over the web interface. The error title and error message
 * are translated into the locale language. The options are
 * the FirmwareError options, ANDED to together.
 * 
 * @author taldhous
 */
public class ErrorDetails
{
    //ABORT = 1,
    //CLEAR_CONTINUE = 2
    //RETRY = 4
    //OK = 8
    //OK_ABORT = 16
    //OK_CONTINUE = 32;
    private int errorCode = 0;
    private String errorTitle = null;
    private String errorMessage = null;
    private boolean userToClear = false;
    private int options = 0;
    
    public ErrorDetails()
    {
        // Jackson deserialization
    }

    public ErrorDetails(int errorCode, String errorTitle, String errorMessage, boolean userToClear, int options)
    {
        this.errorCode = errorCode;
        this.errorTitle = errorTitle;
        this.errorMessage = errorMessage;
        this.userToClear = userToClear;
        this.options = options;
    }

    @JsonProperty
    public int getErrorCode()
    {
        return errorCode;
    }

    @JsonProperty
    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    @JsonProperty
    public String getErrorTitle()
    {
        return errorTitle;
    }

    @JsonProperty
    public void setErrorTitle(String errorTitle)
    {
        this.errorTitle = errorTitle;
    }

    @JsonProperty
    public String getErrorMessage()
    {
        return errorMessage;
    }

    @JsonProperty
    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public boolean getUserToClear()
    {
        return userToClear;
    }

    public void setUserToClear(boolean userToClear)
    {
        this.userToClear = userToClear;
    }

    public int getOptions()
    {
        return options;
    }

    public void setOptions(int options)
    {
        this.options = options;
    }
}
