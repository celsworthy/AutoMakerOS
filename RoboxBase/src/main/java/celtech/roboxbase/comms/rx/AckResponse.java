package celtech.roboxbase.comms.rx;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author ianhudson
 */
public class AckResponse extends RoboxRxPacket
{

    private final String charsetToUse = "US-ASCII";

    private List<FirmwareError> firmwareErrors = new ArrayList<>();

    /**
     *
     * @return
     */
    @JsonIgnore
    public boolean isError()
    {
        return !firmwareErrors.isEmpty();
    }

    /*
     * Errors...
     */
    /**
     *
     */
    public AckResponse()
    {
        super(RxPacketTypeEnum.ACK_WITH_ERRORS, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData, float requiredFirmwareVersion)
    {
        setMessagePayloadBytes(byteData);
        int byteOffset = 1;

        for (; byteOffset < packetLength(requiredFirmwareVersion); byteOffset++)
        {
            if ((byteData[byteOffset] & 1) > 0)
            {
                FirmwareError error = FirmwareError.fromBytePosition(byteOffset - 1);
                firmwareErrors.add(error);
            }
        }

        return !isError();
    }

    public List<FirmwareError> getFirmwareErrors()
    {
        return firmwareErrors;
    }

    public void setFirmwareErrors(List<FirmwareError> firmwareErrors)
    {
        this.firmwareErrors = firmwareErrors;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString()
    {
        StringBuilder outputString = new StringBuilder();

        outputString.append("Report from printer - ");
        if (firmwareErrors.size() == 0)
        {
            outputString.append("No errors detected by printer");
        } else
        {
            for (FirmwareError error : firmwareErrors)
            {
                outputString.append(error.getErrorTitleKey());
                outputString.append("\n");
            }
            outputString.append(">>>>>>>>>>");
        }

        return outputString.toString();
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        if (requiredFirmwareVersion >= 741)
        {
            return 65;
        } else
        {
            return 33;
        }
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(13, 33).
                append(firmwareErrors).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AckResponse))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        AckResponse rhs = (AckResponse) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                append(firmwareErrors, rhs.firmwareErrors).
                isEquals();
    }
}
