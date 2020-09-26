package celtech.coreUI.keycommands;

import celtech.Lookup;
import celtech.roboxbase.comms.DummyPrinterCommandInterface;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class HiddenKey
{

    private Stenographer steno = StenographerFactory.getStenographer(HiddenKey.class.getName());
    private boolean captureKeys = false;
    private ArrayList<String> commandSequences = new ArrayList<>();
    private ArrayList<String> parameterCaptureSequences = new ArrayList<>();
    private final ArrayList<KeyCommandListener> keyCommandListeners = new ArrayList<>();
    private UnhandledKeyListener unhandledKeyListener = null;
    private String hiddenCommandKeyBuffer = "";
    private String parameterCaptureBuffer = "";
    private boolean parameterCaptureInProgress = false;

    private final EventHandler<KeyEvent> hiddenErrorCommandEventHandler = (KeyEvent event) ->
    {
        boolean wasConsumed = false;

        switch (event.getCode())
        {
            case DIGIT1:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    wasConsumed |= triggerListeners("dummy:",
                            DummyPrinterCommandInterface.defaultRoboxAttachCommand);
                }
                break;
            case DIGIT2:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    wasConsumed |= triggerListeners("dummy:",
                            DummyPrinterCommandInterface.defaultRoboxAttachCommand2);
                }
                break;
            case DIGIT3:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    wasConsumed |= triggerListeners("dummy:", "ATTACH EXTRUDER 1");
                }
                break;
            case DIGIT4:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    wasConsumed |= triggerListeners("dummy:", "ATTACH REEL RBX-PLA-OR022 1");
                }
                break;
            case B:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    // trigger B_STUCK
                    wasConsumed |= triggerListeners("dummy:", "ERROR B_STUCK");
                }
                break;
            case E:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    // trigger E_FILAMENT_SLIP
                    wasConsumed |= triggerListeners("dummy:", "ERROR E_FILAMENT_SLIP");
                }
                break;

            case M:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    if (Lookup.getSelectedPrinterProperty().get().
                            extrudersProperty().get(0).filamentLoadedProperty().get())
                    {
                        wasConsumed |= triggerListeners("dummy:", "UNLOAD 0");
                    } else
                    {
                        wasConsumed |= triggerListeners("dummy:", "LOAD 0");
                    }

                }
                break;
            case N:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    if (Lookup.getSelectedPrinterProperty().get().
                            extrudersProperty().get(1).filamentLoadedProperty().get())
                    {
                        wasConsumed |= triggerListeners("dummy:", "UNLOAD 1");
                    } else
                    {
                        wasConsumed |= triggerListeners("dummy:", "LOAD 1");
                    }

                }
                break;
            case D:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    // trigger D_FILAMENT_SLIP
                    wasConsumed |= triggerListeners("dummy:", "ERROR D_FILAMENT_SLIP");
                }
            case S:
                if (event.isShortcutDown() && event.isAltDown())
                {
                    // trigger detach printer
                    wasConsumed |= triggerListeners("dummy:", "DETACH PRINTER");
                }

                break;

        }

        if (!wasConsumed
                && unhandledKeyListener != null)
        {
            unhandledKeyListener.unhandledKeyEvent(event);
        } else
        {
            event.consume();
        }
    };

    private final EventHandler<KeyEvent> hiddenCommandEventHandler = (KeyEvent event) ->
    {
        steno.debug("Got character [" + event.getCharacter() + "]");

        if (parameterCaptureInProgress)
        {
            if (event.getCharacter().equals("\r"))
            {
                steno.debug("Got captured parameter " + parameterCaptureBuffer);
                triggerListeners(hiddenCommandKeyBuffer, parameterCaptureBuffer);
                hiddenCommandKeyBuffer = "";
                parameterCaptureBuffer = "";
                parameterCaptureInProgress = false;
            } else
            {
                parameterCaptureBuffer += event.getCharacter();
            }
        } else
        {
            boolean matchedNone = true;

            for (String commandSequence : commandSequences)
            {
                if (commandSequence.equals(hiddenCommandKeyBuffer + event.getCharacter()))
                {
                    hiddenCommandKeyBuffer = "";
                    triggerListeners(commandSequence);
                    matchedNone = false;
                    break;
                } else if (commandSequence.startsWith(hiddenCommandKeyBuffer + event.
                        getCharacter()))
                {
                    hiddenCommandKeyBuffer += event.getCharacter();
                    matchedNone = false;
                    break;
                }
            }

            if (matchedNone)
            {
                for (String parameterCaptureSequence : parameterCaptureSequences)
                {
                    if (!parameterCaptureInProgress
                            && parameterCaptureSequence.equals(hiddenCommandKeyBuffer + event.
                                    getCharacter()))
                    {
                        hiddenCommandKeyBuffer += event.getCharacter();
                        parameterCaptureInProgress = true;
                        matchedNone = false;
                        break;
                    } else if (parameterCaptureSequence.startsWith(hiddenCommandKeyBuffer + event.
                            getCharacter()))
                    {
                        hiddenCommandKeyBuffer += event.getCharacter();
                        matchedNone = false;
                        break;
                    }
                }
            }

            if (matchedNone)
            {
                hiddenCommandKeyBuffer = "";
            }
        }
    };

    public void stopCapturingHiddenKeys(Scene scene)
    {
        if (captureKeys)
        {
            scene.removeEventHandler(KeyEvent.KEY_TYPED, hiddenCommandEventHandler);
            hiddenCommandKeyBuffer = "";
            captureKeys = false;
        }
    }

    public void captureHiddenKeys(Scene scene)
    {
        if (!captureKeys)
        {
            scene.addEventHandler(KeyEvent.KEY_TYPED, hiddenCommandEventHandler);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, hiddenErrorCommandEventHandler);
            captureKeys = true;
        }
    }

    public void addCommandSequence(String commandSequence)
    {
        commandSequences.add(commandSequence);
    }

    public void addKeyCommandListener(KeyCommandListener listener)
    {
        keyCommandListeners.add(listener);
    }

    private boolean triggerListeners(String commandSequence)
    {
        boolean wasConsumed = false;

        for (KeyCommandListener listener : keyCommandListeners)
        {
            wasConsumed |= listener.trigger(commandSequence, null);
        }

        return wasConsumed;
    }

    private boolean triggerListeners(String commandSequence, String capturedParameter)
    {
        boolean wasConsumed = false;

        for (KeyCommandListener listener : keyCommandListeners)
        {
            wasConsumed |= listener.trigger(commandSequence, capturedParameter);
        }

        return wasConsumed;
    }

    public void addCommandWithParameterSequence(String commandPrefix)
    {
        parameterCaptureSequences.add(commandPrefix);
    }
    
    public void addUnhandledKeyListener(UnhandledKeyListener listener)
    {
        unhandledKeyListener = listener;
    }
}
