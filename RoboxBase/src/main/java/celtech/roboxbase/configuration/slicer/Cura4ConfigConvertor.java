package celtech.roboxbase.configuration.slicer;

import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import celtech.roboxbase.configuration.datafileaccessors.PrinterContainer;
import celtech.roboxbase.configuration.fileRepresentation.HeadFile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterDefinitionFile;
import celtech.roboxbase.printerControl.model.Head;
import celtech.roboxbase.printerControl.model.Nozzle;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.cura.CuraDefaultSettingsEditor;
import celtech.roboxbase.utils.models.PrintableMeshes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author George Salter
 */
public class Cura4ConfigConvertor {
    
    private static final Stenographer STENO = StenographerFactory.getStenographer(Cura4ConfigConvertor.class.getName());
    
    private final Printer printer;
    private final PrintableMeshes printableMeshes;
    
    private CuraDefaultSettingsEditor curaDefaultSettingsEditor;
    
    public Cura4ConfigConvertor(Printer printer, PrintableMeshes printableMeshes) {
        this.printer = printer;
        this.printableMeshes = printableMeshes;
    }
    
    public void injectConfigIntoCura4SettingsFile(String configFile, String storageDirectory) {
        curaDefaultSettingsEditor = new CuraDefaultSettingsEditor(printableMeshes.getNumberOfNozzles() <= 1);
        curaDefaultSettingsEditor.beginEditing();
        
        addDefaultsForPrinter();
        addExtrudersAndDefaults();
        addMappedSettings(configFile);
        
        curaDefaultSettingsEditor.endEditing(storageDirectory);
    }
    
    private void addDefaultsForPrinter()
    {
        int width;
        int depth;
        int height;
        
        if(printer == null) 
        {
            PrinterDefinitionFile printerDef = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);
            width = printerDef.getPrintVolumeWidth();
            depth = printerDef.getPrintVolumeDepth();
            height = printerDef.getPrintVolumeHeight();
        } else
        {
            width =  printer.printerConfigurationProperty().get().getPrintVolumeWidth();
            depth = printer.printerConfigurationProperty().get().getPrintVolumeDepth();
            height = printer.printerConfigurationProperty().get().getPrintVolumeHeight();
        }
        curaDefaultSettingsEditor.editDefaultFloatValue("machine_width", width);
        curaDefaultSettingsEditor.editDefaultFloatValue("machine_depth", depth);
        curaDefaultSettingsEditor.editDefaultFloatValue("machine_height", height);
        
        // Currently need to move origin back to corner of bed, not center.
        curaDefaultSettingsEditor.editDefaultFloatValue("mesh_position_x", (float) -(width /2));
        curaDefaultSettingsEditor.editDefaultFloatValue("mesh_position_y", (float) -(depth /2));
        
        int numberOfNozzles = printableMeshes.getNumberOfNozzles();
        curaDefaultSettingsEditor.editDefaultIntValue("machine_extruder_count", numberOfNozzles);
        curaDefaultSettingsEditor.editDefaultIntValue("extruders_enabled_count", numberOfNozzles);
    }
    
    private void addExtrudersAndDefaults() {
        Head headOnPrinter;
        if (printer == null ||
            printer.headProperty() == null ||
            printer.headProperty().get() == null) {
            HeadFile defaultHeadData = HeadContainer.getHeadByID(HeadContainer.defaultHeadID);
            headOnPrinter = new Head(defaultHeadData);
        } else {
            headOnPrinter = printer.headProperty().get();
        }
        
        List<Nozzle> nozzles = headOnPrinter.getNozzles();
        for(int i = 0; i < nozzles.size(); i++) {
            String nozzleReference = "noz" + String.valueOf(i + 1);
            curaDefaultSettingsEditor.beginNewExtruderFile(nozzleReference);
            Nozzle nozzle = nozzles.get(i);
            curaDefaultSettingsEditor.editExtruderValue("machine_nozzle_id", nozzleReference, nozzleReference);
            curaDefaultSettingsEditor.editExtruderValue("machine_nozzle_size", nozzleReference, 
                    String.valueOf(nozzle.diameterProperty().get()));
        }
    }
    
    private void addMappedSettings(String configFile) {
        try {
            File configOptions = new File(configFile);
            BufferedReader fileReader = new BufferedReader(new FileReader(configOptions));
            
            String readLine;
            
            while((readLine = fileReader.readLine()) != null) {
                if(!readLine.startsWith("#")) {
                    String[] settingAndValue = readLine.split("=");
                    String settingName = settingAndValue[0];
                    String value = settingAndValue[1];
                    if (value.contains(":")) {
                        String[] valuesForNozzles = value.split(":");
                        for(int i = 0; i < valuesForNozzles.length; i++) {
                            String nozzleReference = "noz" + String.valueOf(i + 1);
                            curaDefaultSettingsEditor.editExtruderValue(settingName, nozzleReference, valuesForNozzles[i]);
                        }
                    } else {
                        curaDefaultSettingsEditor.editDefaultValue(settingName, value);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            STENO.error("Config file: " + configFile + " could not be found.");
            STENO.error(ex.getMessage());
        } catch (IOException ex) {
            STENO.error("Error while reading config file: " + configFile);
            STENO.error(ex.getMessage());
        }
    }
}
