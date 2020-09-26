package celtech.roboxbase.importers.twod.svg;

import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.importers.twod.svg.metadata.dragknife.StylusMetaPart;
import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.printerControl.comms.commands.MacroLoadException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class StylusMetaToGCodeEngine
{

    private final Stenographer steno = StenographerFactory.getStenographer(StylusMetaToGCodeEngine.class.getName());

    private final String outputFilename;
    private final List<StylusMetaPart> metaparts;

    public StylusMetaToGCodeEngine(String outputURIString, List<StylusMetaPart> metaparts)
    {
        this.outputFilename = outputURIString;
        this.metaparts = metaparts;
    }

    public List<GCodeEventNode> generateGCode()
    {
        List<GCodeEventNode> gcodeNodes = new ArrayList<>();
        
        PrintWriter out = null;
        try
        {
            out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename)));

            //Add a macro header
            try
            {
                List<String> startMacro = GCodeMacros.getMacroContents("stylus_cut_start",
                        Optional.<PrinterType>empty(), null, false, false, false);
                for (String macroLine : startMacro)
                {
                    out.println(macroLine);
                }
            } catch (MacroLoadException ex)
            {
                steno.exception("Unable to load stylus cut start macro.", ex);
            }

            String renderResult = null;

            for (StylusMetaPart part : metaparts)
            {
                renderResult = part.renderToGCode();
                if (renderResult != null)
                {
                    out.println(renderResult);
                    gcodeNodes.addAll(part.renderToGCodeNode());
                    renderResult = null;
                }
            }

            //Add a macro footer
            try
            {
                List<String> startMacro = GCodeMacros.getMacroContents("stylus_cut_finish",
                        Optional.<PrinterType>empty(), null, false, false, false);
                for (String macroLine : startMacro)
                {
                    out.println(macroLine);
                }
            } catch (MacroLoadException ex)
            {
                steno.exception("Unable to load stylus cut start macro.", ex);
            }
        } catch (IOException ex)
        {
            steno.error("Unable to output SVG GCode to " + outputFilename);
        } finally
        {
            if (out != null)
            {
                out.flush();
                out.close();
            }
        }
        
        return gcodeNodes;
    }
}
