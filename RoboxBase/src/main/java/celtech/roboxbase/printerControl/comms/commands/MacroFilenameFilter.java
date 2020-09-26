package celtech.roboxbase.printerControl.comms.commands;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.datafileaccessors.HeadContainer;
import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author Ian
 */
public class MacroFilenameFilter implements FilenameFilter
{

    private final String baseMacroName;
    private final String headTypeCode;
    private final GCodeMacros.NozzleUseIndicator nozzleUse;
    private final GCodeMacros.SafetyIndicator safeties;
    private final String separator = "#";

    public MacroFilenameFilter(String baseMacroName,
            String headTypeCode,
            GCodeMacros.NozzleUseIndicator nozzleUse,
            GCodeMacros.SafetyIndicator safeties)
    {
        this.baseMacroName = baseMacroName;
        this.headTypeCode = headTypeCode;
        this.nozzleUse = nozzleUse;
        this.safeties = safeties;
    }

    @Override
    public boolean accept(File dir, String name)
    {
        String[] filenameSplit = name.split("\\.");

        if (filenameSplit.length == 2
                && ("." + filenameSplit[1]).equalsIgnoreCase(BaseConfiguration.macroFileExtension))
        {
            String[] nameParts = filenameSplit[0].split(separator);

            int partsRequired = 0;

            if (baseMacroName != null
                    && nameParts.length > 0)
            {
                if (nameParts[0].equalsIgnoreCase(baseMacroName))
                {
                    partsRequired++;

                    if (headTypeCode != null
                            && !headTypeCode.equalsIgnoreCase(HeadContainer.defaultHeadID))
                    {
                        partsRequired++;
                        if (!doesAttributeExist(nameParts, headTypeCode))
                        {
                            return false;
                        }
                    }

                    if (nozzleUse != GCodeMacros.NozzleUseIndicator.DONT_CARE)
                    {
                        partsRequired++;
                        if (!doesAttributeExist(nameParts, nozzleUse.getFilenameCode()))
                        {
                            return false;
                        }
                    }

                    if (safeties == GCodeMacros.SafetyIndicator.SAFETIES_OFF)
                    {
                        partsRequired++;
                        if (!doesAttributeExist(nameParts, GCodeMacros.SafetyIndicator.SAFETIES_OFF.getFilenameCode()))
                        {
                            return false;
                        }
                    }

                    if (nameParts.length != partsRequired)
                    {
                        return false;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private boolean doesAttributeExist(String[] parts, String attribute)
    {
        for (String part : parts)
        {
            if (part.equalsIgnoreCase(attribute))
            {
                return true;
            }
        }
        return false;
    }

}
