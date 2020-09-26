package celtech.roboxbase.comms;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 *
 * @author Ian
 */
public class DetectedDevice
{

    private final DeviceDetector.DeviceConnectionType connectionType;
    private final String connectionHandle;

    public DetectedDevice(DeviceDetector.DeviceConnectionType connectionType,
            String connectionHandle)
    {
        this.connectionType = connectionType;
        this.connectionHandle = connectionHandle;
    }

    public DeviceDetector.DeviceConnectionType getConnectionType()
    {
        return connectionType;
    }

    public String getConnectionHandle()
    {
        return connectionHandle;
    }

    @Override
    public String toString()
    {
        return connectionType.name() + ":" + connectionHandle;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(21, 31)
                .append(connectionHandle)
                .append(connectionType)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof DetectedDevice))
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }

        DetectedDevice rhs = (DetectedDevice) obj;
        return new EqualsBuilder()
                .append(connectionHandle, rhs.connectionHandle)
                .append(connectionType, rhs.connectionType)
                .isEquals();
    }
}
