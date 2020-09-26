package celtech.roboxbase.configuration;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 *
 * @author Ian
 */
public enum SlicerType
{

    Slic3r(0), 
    @JsonEnumDefaultValue Cura(1), 
    Cura4(2);

    private final int enumPosition;

    private SlicerType(int enumPosition)
    {
        this.enumPosition = enumPosition;
    }

    /**
     *
     * @return
     */
    public int getEnumPosition()
    {
        return enumPosition;
    }

    /**
     *
     * @param enumPosition
     * @return
     */
    public static SlicerType fromEnumPosition(int enumPosition)
    {
        SlicerType returnVal = null;

        for (SlicerType value : values())
        {
            if (value.getEnumPosition() == enumPosition)
            {
                returnVal = value;
                break;
            }
        }

        return returnVal;
    }

    @Override
    public String toString()
    {
        return name();
    }
}
