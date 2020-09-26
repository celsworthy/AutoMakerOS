package celtech.configuration;

import celtech.roboxbase.configuration.SlicerType;

/**
 *
 * @author Ian
 */
public enum CustomSlicerType
{
    Default(null), 
    Cura(SlicerType.Cura),
    Cura4(SlicerType.Cura4);
    
    private final SlicerType slicerType;

    private CustomSlicerType(SlicerType slicerType)
    {
        this.slicerType = slicerType;
    }
     
    public static CustomSlicerType customTypefromSettings(SlicerType slicerType)
    {
        CustomSlicerType customSlicerType = CustomSlicerType.valueOf(slicerType.name());
        
        return customSlicerType;
    }
    
    public SlicerType getSlicerType()
    {
        return slicerType;
    }
}
