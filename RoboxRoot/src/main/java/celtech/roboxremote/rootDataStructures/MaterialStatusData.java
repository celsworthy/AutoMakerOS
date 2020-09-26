package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.configuration.Filament;
import celtech.roboxbase.configuration.datafileaccessors.FilamentContainer;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.ColourStringConverter;
import celtech.roboxremote.PrinterRegistry;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author taldhous
 */
public class MaterialStatusData
{
    private String printerID;

    //Material
     private FilamentDetails[] attachedFilaments = null;

    public MaterialStatusData()
    {
        // Jackson deserialization
    }

    public void updateFromPrinterData(String printerID)
    {
        this.printerID = printerID;
        Printer printer = PrinterRegistry.getInstance().getRemotePrinters().get(printerID);
 
        int numberOfExtruders = 1;
        if (printer.extrudersProperty().size() == 2
                && printer.extrudersProperty().get(1) != null
                && printer.extrudersProperty().get(1).isFittedProperty().get())
        {
            numberOfExtruders = 2;
        }
                
        Head head = printer.headProperty().get();

        attachedFilaments = new FilamentDetails[numberOfExtruders];
        for (int extruderNumber = 0; extruderNumber < numberOfExtruders; extruderNumber++)
        {
            String filamentName = "";
            String materialName = "";
            String webColour = "";
            int filamentTemperature = -1;
            float remainingFilament = -1.0F;
            boolean customFlag = false;
            boolean extruderFitter = (printer.extrudersProperty().get(extruderNumber) != null &&
                                      printer.extrudersProperty().get(extruderNumber).isFittedProperty().get());
            boolean canEject = (extruderFitter &&
                                printer.extrudersProperty().get(extruderNumber).canEjectProperty().get());
            boolean materialLoaded = (extruderFitter &&
                                      printer.extrudersProperty().get(extruderNumber).filamentLoadedProperty().get());
            boolean canExtrude = (head == null) || (materialLoaded && extruderNumber < head.getNozzleHeaters().size());
            boolean canRetract = (head == null) || materialLoaded;
            
            if (printer.effectiveFilamentsProperty().get(extruderNumber) != FilamentContainer.UNKNOWN_FILAMENT)
            {
                Filament filament = printer.effectiveFilamentsProperty().get(extruderNumber);
                filamentName = filament.getFriendlyFilamentName();
                materialName = filament.getMaterial().toString();
                filamentTemperature = filament.getNozzleTemperature();
                remainingFilament = filament.getRemainingFilament();
                webColour = "#" + ColourStringConverter.colourToString(filament.getDisplayColourProperty().get());
                customFlag = filament.isMutable();
                
                if (printer.reelsProperty().containsKey(extruderNumber) && printer.reelsProperty().get(extruderNumber) != null)
                {
                    remainingFilament = printer.reelsProperty().get(extruderNumber).remainingFilamentProperty().get();
                    if (remainingFilament < 0.0F)
                        remainingFilament = 0.0F;
                }
            }

            attachedFilaments[extruderNumber] = new FilamentDetails(filamentName, materialName, webColour,
                                                                    filamentTemperature, 0.001F * remainingFilament, // Remaining filament is in mm but needs to be reported in m.
                                                                    customFlag, materialLoaded, canEject, canExtrude, canRetract);
        }
    }

    @JsonProperty
    public String getPrinterID()
    {
        return printerID;
    }

    @JsonProperty
    public void setPrinterID(String printerID)
    {
        this.printerID = printerID;
    }

    @JsonProperty
    public void setAttachedFilaments(FilamentDetails[] attachedFilaments)
    {
        this.attachedFilaments = attachedFilaments;
    }

    @JsonProperty
    public FilamentDetails[] getAttachedFilaments()
    {
        return attachedFilaments;
    }
}
