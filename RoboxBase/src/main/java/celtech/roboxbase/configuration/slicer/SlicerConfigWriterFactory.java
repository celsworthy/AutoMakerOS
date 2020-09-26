package celtech.roboxbase.configuration.slicer;

import celtech.roboxbase.configuration.SlicerType;

/**
 *
 * @author Ian
 */
public class SlicerConfigWriterFactory
{

    public static SlicerConfigWriter getConfigWriter(SlicerType slicerType)
    {
        SlicerConfigWriter writer = null;
        
        switch (slicerType)
        {
            case Cura4:
                writer = new Cura4ConfigWriter();
                break;
            case Cura:
                writer = new CuraConfigWriter();
                break;
            case Slic3r:
                writer = new Slic3rConfigWriter();
                break;
        }
        return writer;
    }
}
