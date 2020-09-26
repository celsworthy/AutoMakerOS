package celtech.roboxbase.postprocessor.nouveau;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.RoboxProfile;
import celtech.roboxbase.configuration.fileRepresentation.PrinterSettingsOverrides;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.configuration.slicer.NozzleParameters;
import celtech.roboxbase.postprocessor.GCodeOutputWriter;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.LayerNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.MCodeNode;
import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import celtech.roboxbase.postprocessor.nouveau.timeCalc.TimeAndVolumeCalcResult;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.printerControl.comms.commands.MacroLoadException;
import celtech.roboxbase.utils.TimeUtils;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public class OutputUtilities
{
    static final DecimalFormat df = new DecimalFormat("#.####");

    protected void prependPrePrintHeader(GCodeOutputWriter writer, Optional<PrinterType> typeCode,
            String headType, RoboxProfile settingsProfile, boolean useNozzle0, boolean useNozzle1, boolean requireSafetyFeatures)    {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM y HH:mm:ss", Locale.UK);
        try
        {
            writer.writeOutput(";###############################################################################\n");
            writer.writeOutput("; File post-processed by the CEL Tech Roboxiser on "
                    + formatter.format(new Date()) + "\n");
            writer.writeOutput("; " + BaseConfiguration.getTitleAndVersion() + "\n");
                        // Get the map to prevent error messages if the setting is not present.
            
            writer.writeOutput(";\n; Settings\n");
            writeProfileSetting(writer, settingsProfile, "infillLayerThickness");
            writeProfileSetting(writer, settingsProfile, "fillExtrusionWidth_mm");
            
            List<NozzleParameters> nozzleParameters = settingsProfile.getNozzleParameters();
            if (nozzleParameters.size() > 0 && useNozzle0) {
                writeFloatSetting(writer, "nozzle0_ejectionvolume", nozzleParameters.get(0).getEjectionVolume());
            }
            if (nozzleParameters.size() > 1 && useNozzle1) {
                writeFloatSetting(writer, "nozzle1_ejectionvolume", nozzleParameters.get(1).getEjectionVolume());
            }
            
            writer.writeOutput(";\n; Pre print gcode\n");

            for (String macroLine : GCodeMacros.getMacroContents("before_print", typeCode, headType, useNozzle0, useNozzle1, requireSafetyFeatures))
            {
                writer.writeOutput(macroLine);
                writer.newLine();
            }

            writer.writeOutput("; End of Pre print gcode\n");
        } catch (IOException | MacroLoadException ex)
        {
            throw new RuntimeException("Failed to add pre-print header in post processor - " + ex.getMessage(), ex);
        }
    }

    private void writeProfileSetting(GCodeOutputWriter writer, RoboxProfile settingsProfile, String valueId) throws IOException {
        String valueString = settingsProfile.getSpecificSettingAsStringWithDefault(valueId, "").trim();
        if (!valueString.isEmpty()) {
            writer.writeOutput(";# " + valueId + " = " + valueString + "\n");
        }
    }

    private void writeFloatSetting(GCodeOutputWriter writer, String valueId, float value) throws IOException {
        if (value > 0.0f) {
            writer.writeOutput(";# " + valueId + " = " + df.format(value) + "\n");
        }
    }

    protected void appendPostPrintFooter(GCodeOutputWriter writer,
            TimeAndVolumeCalcResult timeAndVolumeCalcResult, Optional<PrinterType> typeCode,
            String headType, boolean useNozzle0, boolean useNozzle1, boolean requireSafetyFeatures)
    {
        try
        {
            writer.writeOutput(";\n; Post print gcode\n");
            for (String macroLine : GCodeMacros.getMacroContents("after_print", typeCode, headType, useNozzle0, useNozzle1, requireSafetyFeatures))
            {
                writer.writeOutput(macroLine);
                writer.newLine();
            }
            writer.writeOutput("; End of Post print gcode\n");
            writer.writeOutput(";\n");
            writer.writeOutput("; Time and volume summary\n");
            writer.writeOutput("; =======================\n");
            writer.writeOutput(";\n");
            writer.writeOutput("; Extruder E\n");
            writer.writeOutput("; ----------\n");
            writer.writeOutput("; Volume of material - " + timeAndVolumeCalcResult.getExtruderEStats().getVolume() + "\n");
            writer.writeOutput("; Feedrate dependent time - " + TimeUtils.convertToHoursMinutesSeconds((int) timeAndVolumeCalcResult.getExtruderEStats().getDuration().getTotal_duration()) + "\n");
            writer.writeOutput("; ==========\n");
            writer.writeOutput("; Extruder D\n");
            writer.writeOutput("; ----------\n");
            writer.writeOutput("; Volume of material - " + timeAndVolumeCalcResult.getExtruderDStats().getVolume() + "\n");
            writer.writeOutput("; Feedrate dependent time - " + TimeUtils.convertToHoursMinutesSeconds((int) timeAndVolumeCalcResult.getExtruderDStats().getDuration().getTotal_duration()) + "\n");
            writer.writeOutput("; ==========\n");
            writer.writeOutput("; ----------------------------------\n");
            writer.writeOutput("; Feedrate independent time - " + TimeUtils.convertToHoursMinutesSeconds((int) timeAndVolumeCalcResult.getFeedrateIndependentDuration().getTotal_duration()) + "\n");
            writer.writeOutput("; ==================================================================\n");
            writer.writeOutput("; Total print time estimate - "
                    + TimeUtils.convertToHoursMinutesSeconds((int) (timeAndVolumeCalcResult.getExtruderEStats().getDuration().getTotal_duration()
                            + timeAndVolumeCalcResult.getExtruderDStats().getDuration().getTotal_duration()
                            + timeAndVolumeCalcResult.getFeedrateIndependentDuration().getTotal_duration()))
                    + "\n");
            writer.writeOutput("; ===================================================================\n");
            writer.writeOutput(";\n");
        } catch (IOException | MacroLoadException ex)
        {
            throw new RuntimeException("Failed to add post-print footer in post processor - " + ex.getMessage(), ex);
        }
    }

    protected void outputSingleMaterialNozzleTemperatureCommands(GCodeOutputWriter writer,
            boolean useNozzle0Heater, boolean useNozzle1Heater,
            boolean useEExtruder, boolean useDExtruder)
    {
        try
        {
            MCodeNode nozzleTemp = new MCodeNode(104);
            if (useNozzle0Heater
                    || (!useNozzle0Heater && !useNozzle1Heater))
            {
                nozzleTemp.setSOnly(true);
            }
            if (useNozzle1Heater)
            {
                nozzleTemp.setTOnly(true);
            }

            nozzleTemp.setCommentText(" Go to nozzle temperature from loaded reel - don't wait");
            writer.writeOutput(nozzleTemp.renderForOutput());
            writer.newLine();
        } catch (IOException ex)
        {
            throw new RuntimeException("Failed to add post layer 1 temperature commands in post processor - " + ex.getMessage(), ex);
        }
    }

    protected void writeLayerToFile(LayerNode layerNode, GCodeOutputWriter writer)
    {
        if (layerNode != null)
        {
            try
            {
                writer.writeOutput(layerNode.renderForOutput());
                writer.newLine();
            } catch (IOException ex)
            {
                throw new RuntimeException("Error outputting post processed data at node " + layerNode.renderForOutput(), ex);
            }

            Iterator<GCodeEventNode> layerIterator = layerNode.treeSpanningIterator(null);

            while (layerIterator.hasNext())
            {
                GCodeEventNode node = layerIterator.next();

                if (node instanceof Renderable)
                {
                    Renderable renderableNode = (Renderable) node;
                    try
                    {
                        writer.writeOutput(renderableNode.renderForOutput());
                        writer.newLine();
                    } catch (IOException ex)
                    {
                        throw new RuntimeException("Error outputting post processed data at node " + renderableNode.renderForOutput(), ex);
                    }
                }
            }
        }
    }

    protected void outputNodes(GCodeEventNode node, int level)
    {
        //Output me
        StringBuilder outputBuilder = new StringBuilder();

        for (int levelCount = 0; levelCount < level; levelCount++)
        {
            outputBuilder.append('\t');
        }
        if (node instanceof Renderable)
        {
            outputBuilder.append(((Renderable) node).renderForOutput());
        } else
        {
            outputBuilder.append(node.toString());
        }
        System.out.println(outputBuilder.toString());

        //Output my children
        List<GCodeEventNode> children = node.getChildren();
        for (GCodeEventNode child : children)
        {
            level++;
            outputNodes(child, level);
            level--;
        }
    }
}
