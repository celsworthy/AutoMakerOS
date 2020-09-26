package celtech.roboxbase.comms.remote.types;

import celtech.roboxbase.comms.rx.FirmwareError;
import java.util.List;

/**
 *
 * @author Tony
 */
public class SerializableErrorList {
   private List<FirmwareError> errorList;
   
   public SerializableErrorList()
   {
   }
   
    public List<FirmwareError> getErrorList()
    {
        return errorList;
    }

    public void setErrorList(List<FirmwareError> errorList)
    {
        this.errorList = errorList;
    }
}
