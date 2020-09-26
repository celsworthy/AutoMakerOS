/*
 * Copyright 2014 CEL UK
 */
package celtech.utils;

import celtech.coreUI.components.ChoiceLinkDialogBox;
import celtech.coreUI.components.ChoiceLinkDialogBox.PrinterDisconnectedException;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.MachineType;
import java.util.ResourceBundle;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * The SystemValidation class houses functions that validate the host system
 * such as 3D support.
 *
 * @author tony
 */
public class SystemValidation
{

    private static final Stenographer steno = StenographerFactory.getStenographer(SystemValidation.class.getName());

    /**
     * Check that the machine type is fully recognised and if not then exit the
     * application.
     */
    public static boolean checkMachineTypeRecognised(ResourceBundle i18nBundle)
    {
        MachineType machineType = BaseConfiguration.getMachineType();
        if (machineType.equals(MachineType.UNKNOWN))
        {
            ChoiceLinkDialogBox unknownMachineBox = new ChoiceLinkDialogBox(false);
            unknownMachineBox.setTitle(i18nBundle.getString("dialogs.fatalErrorDetectingMachineType"));
            unknownMachineBox.setMessage(i18nBundle.getString("dialogs.automakerUnknownMachineType"));
            unknownMachineBox.addChoiceLink(i18nBundle.getString("dialogs.error.okAbortJob"));
            try
            {
                unknownMachineBox.getUserInput();
            } catch (PrinterDisconnectedException ex)
            {
            }
            steno.error("Closing down due to unrecognised machine type.");
            Platform.exit();
            return false;
        } else
        {
            return true;
        }
    }

    /**
     * Check that 3D is supported on this machine and if not then exit the
     * application.
     *
     * @param i18nBundle
     * @return
     */
    public static boolean check3DSupported(ResourceBundle i18nBundle)
    {
        boolean threeDSupportOK = false;

        steno.debug("Starting AutoMaker - check 3D support...");
        boolean checkForScene3D = true;

        String forceGPU = System.getProperty("prism.forceGPU");

        if (forceGPU != null)
        {
            if (forceGPU.equalsIgnoreCase("true"))
            {
                checkForScene3D = false;
                threeDSupportOK = true;
            }
        }

        if (checkForScene3D == true)
        {
            if (!Platform.isSupported(ConditionalFeature.SCENE3D))
            {
                BaseLookup.getTaskExecutor().runOnGUIThread(() ->
                {
                    ChoiceLinkDialogBox threeDProblemBox = new ChoiceLinkDialogBox(false);
                    threeDProblemBox.setTitle(i18nBundle.getString("dialogs.fatalErrorNo3DSupport"));
                    threeDProblemBox.setMessage(i18nBundle.getString("dialogs.automakerErrorNo3DSupport"));
                    threeDProblemBox.addChoiceLink(i18nBundle.getString("dialogs.error.okAbortJob"));
                    try
                    {
                        threeDProblemBox.getUserInput();
                    } catch (PrinterDisconnectedException ex)
                    {
                    }
                    steno.error("Closing down due to lack of required 3D support.");
                    Platform.exit();
                });
            } else
            {
                threeDSupportOK = true;
            }
        }

        return threeDSupportOK;
    }

}
