package celtech.roboxbase.postprocessor;

import celtech.roboxbase.configuration.SlicerType;
import celtech.roboxbase.postprocessor.events.*;
import celtech.roboxbase.utils.SystemUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class GCodeFileParser
{

    private final Stenographer steno = StenographerFactory.getStenographer(
            GCodeFileParser.class.getName());
    private final ArrayList<GCodeTranslationEventHandler> listeners = new ArrayList<>();

    /**
     *
     * @param eventHandler
     */
    public void addListener(GCodeTranslationEventHandler eventHandler)
    {
        listeners.add(eventHandler);
    }

    /**
     *
     * @param eventHandler
     */
    public void removeListener(GCodeTranslationEventHandler eventHandler)
    {
        listeners.remove(eventHandler);
    }

    /**
     *
     * @param inputfilename
     * @param percentProgress
     * @param slicerType
     * @throws celtech.roboxbase.postprocessor.PostProcessingError
     */
    public void parse(final String inputfilename, DoubleProperty percentProgress,
            final SlicerType slicerType) throws PostProcessingError
    {
        File inputFile = new File(inputfilename);

        int linesInFile = SystemUtils.countLinesInFile(inputFile);
        int linesSoFar = 0;
        double lastPercentSoFar = 0;

        ArrayList<String> lineRepository = new ArrayList<>();

        ExtrusionTask currentExtrusionTask = null;

        try
        {
            BufferedReader fileReader = new BufferedReader(new FileReader(inputFile));

            String line;
            while ((line = fileReader.readLine()) != null)
            {
                linesSoFar++;
                double percentSoFar = ((double) linesSoFar / (double) linesInFile) * 100;
                if (percentSoFar - lastPercentSoFar >= 1)
                {
                    if (percentProgress != null)
                    {
                        percentProgress.set(percentSoFar);
                    }
                    lastPercentSoFar = percentSoFar;
                }

                GCodeParseEvent eventToOutput = null;

                String comment = null;

                boolean invalidLine = false;

                boolean xPresent = false;
                double xValue = 0.0;

                boolean yPresent = false;
                double yValue = 0.0;

                boolean zPresent = false;
                double zValue = 0.0;

                boolean ePresent = false;
                double eValue = 0.0;

                boolean fPresent = false;
                double fValue = 0.0;

                boolean gPresent = false;
                boolean gCodeEvent = false;
                int gValue = 0;

                boolean mPresent = false;
                int mValue = 0;

                boolean sPresent = false;
                int sValue = 0;

                boolean tPresent = false;
                int tValue = 0;

                String[] commentSplit = line.trim().split(";");

                if (commentSplit.length == 2)
                {
                    //There was a comment
                    comment = commentSplit[1];
                }

                String[] lineParts = commentSplit[0].trim().split(" ");

                for (String partToConsider : lineParts)
                {
                    if (partToConsider != null)
                    {
                        if (partToConsider.length() > 0)
                        {
                            char command = partToConsider.charAt(0);
                            String value = partToConsider.substring(1);

                            switch (command)
                            {
                                case 'G':
                                    gValue = Integer.valueOf(value);
                                    if (gValue > 1)
                                    {
                                        gCodeEvent = true;
                                    } else
                                    {
                                        gPresent = true;
                                    }
                                    break;
                                case 'M':
                                    mPresent = true;
                                    mValue = Integer.valueOf(value);
                                    break;
                                case 'S':
                                    sPresent = true;
                                    sValue = Integer.valueOf(value.replaceFirst("\\..*", ""));
                                    break;
                                case 'T':
                                    tPresent = true;
                                    tValue = Integer.valueOf(value);
                                    break;
                                case 'X':
                                    xPresent = true;
                                    xValue = Double.valueOf(value);
                                    break;
                                case 'Y':
                                    yPresent = true;
                                    yValue = Double.valueOf(value);
                                    break;
                                case 'Z':
                                    zPresent = true;
                                    zValue = Double.valueOf(value);
                                    break;
                                case 'E':
                                    ePresent = true;
                                    eValue = Double.valueOf(value);
                                    break;
                                case 'F':
                                    fPresent = true;
                                    fValue = Double.valueOf(value);
                                    break;
                            }

                            if (gCodeEvent)
                            {
                                break;
                            }
                        } else
                        {
                            invalidLine = true;
                        }
                    } else
                    {
                        invalidLine = true;
                        steno.debug("Discarded null");
                    }
                }

                if (mPresent)
                {
                    MCodeEvent event = new MCodeEvent();

                    event.setMNumber(mValue);

                    if (sPresent)
                    {
                        event.setSNumber(sValue);
                    }

                    if (comment != null)
                    {
                        event.setComment(comment);
                    }

                    eventToOutput = event;
                } else if (tPresent)
                {
                    NozzleChangeEvent event = new NozzleChangeEvent();
                    event.setNozzleNumber(tValue);

                    if (comment != null)
                    {
                        event.setComment(comment);
                    }

                    eventToOutput = event;
                } else if (gPresent && !xPresent && !yPresent && !zPresent && !ePresent)
                {
                    GCodeEvent event = new GCodeEvent();

                    event.setGNumber(gValue);

                    if (comment != null)
                    {
                        event.setComment(comment);
                    }

                    eventToOutput = event;
                } else if (gPresent && zPresent && !xPresent && !yPresent && !ePresent)
                {
                    LayerChangeWithoutTravelEvent event = new LayerChangeWithoutTravelEvent();

                    event.setZ(zValue);

                    if (fPresent)
                    {
                        event.setFeedRate(fValue);
                    }

                    if (comment != null)
                    {
                        event.setComment(comment);
                    }

                    eventToOutput = event;
                } else if (gPresent && ePresent && !xPresent && !yPresent && !zPresent)
                {
                    if (eValue < 0)
                    {
                        //Must be a retract
                        RetractEvent event = new RetractEvent();

                        event.setE(eValue);

                        if (fPresent)
                        {
                            event.setFeedRate(fValue);
                        }

                        if (comment != null)
                        {
                            event.setComment(comment);
                        }

                        eventToOutput = event;
                    } else
                    {
                        UnretractEvent event = new UnretractEvent();
                        event.setE(eValue);

                        if (fPresent)
                        {
                            event.setFeedRate(fValue);
                        }

                        if (comment != null)
                        {
                            event.setComment(comment);
                        }

                        eventToOutput = event;
                    }
                } else if (gPresent && xPresent && yPresent && !ePresent && !zPresent)
                {
                    TravelEvent event = new TravelEvent();
                    event.setX(xValue);
                    event.setY(yValue);

                    if (fPresent)
                    {
                        event.setFeedRate(fValue);
                    }

                    if (comment != null)
                    {
                        event.setComment(comment);
                    }

                    eventToOutput = event;
                } else if (gPresent && xPresent && yPresent && ePresent && !zPresent)
                {
                    if (eValue > 0)
                    {
                        ExtrusionEvent event = new ExtrusionEvent();

                        event.setX(xValue);
                        event.setY(yValue);
                        event.setE(eValue);

                        if (fPresent)
                        {
                            event.setFeedRate(fValue);
                        }

                        if (comment != null)
                        {
                            event.setComment(comment);
                        }

                        switch (slicerType)
                        {
                            case Slic3r:
                                if (comment != null)
                                {
                                    // Look for the extrusion type in the comment
                                    ExtrusionTask foundExtrusionTask = ExtrusionTask.lookupExtrusionTaskFromComment(
                                            slicerType, comment);
                                    event.setExtrusionTask(foundExtrusionTask);
                                    if (foundExtrusionTask != null)
                                    {
                                        currentExtrusionTask = foundExtrusionTask;
                                    }
                                } else if (currentExtrusionTask != null)
                                {
                                    event.setExtrusionTask(currentExtrusionTask);
                                }
                                break;
                            case Cura:
                                if (currentExtrusionTask != null)
                                {
                                    event.setExtrusionTask(currentExtrusionTask);
                                }
                                break;
                        }

                        eventToOutput = event;
                    } else
                    {
                        RetractDuringExtrusionEvent event = new RetractDuringExtrusionEvent();

                        event.setX(xValue);
                        event.setY(yValue);
                        event.setE(eValue);

                        if (fPresent)
                        {
                            event.setFeedRate(fValue);
                        }

                        if (comment != null)
                        {
                            event.setComment(comment);
                        }

                        eventToOutput = event;
                    }
                } else if (gPresent && xPresent && yPresent && zPresent && !ePresent)
                {
                    // This is how cura performs a combined move and layer change
                    // For now split this into a Layer Change then a Travel

                    LayerChangeWithTravelEvent event = new LayerChangeWithTravelEvent();

                    event.setX(xValue);
                    event.setY(yValue);
                    event.setZ(zValue);

                    if (fPresent)
                    {
                        event.setFeedRate(fValue);
                    }

                    if (comment != null)
                    {
                        event.setComment(comment);
                    }

                    eventToOutput = event;
                } else if (comment != null && !gCodeEvent)
                {
                    if (slicerType == SlicerType.Cura)
                    {
                        // Pre-post processing enrichment for Cura - extrusion type is in the comments...
                        ExtrusionTask foundExtrusionTask = ExtrusionTask.lookupExtrusionTaskFromComment(
                                slicerType, comment);

                        if (foundExtrusionTask != null)
                        {
                            currentExtrusionTask = foundExtrusionTask;
                        }
                    }

                    CommentEvent event = new CommentEvent();
                    event.setComment(comment);

                    eventToOutput = event;
                } else if (line.equals(""))
                {
                    BlankLineEvent event = new BlankLineEvent();

                    eventToOutput = event;
                } else
                {
                    for (GCodeTranslationEventHandler listener : listeners)
                    {
                        listener.unableToParse(line);
                    }
                }

                if (eventToOutput != null)
                {
                    eventToOutput.setLinesSoFar(linesSoFar);
                    if (eventToOutput instanceof ExtrusionEvent
                            && ((ExtrusionEvent)eventToOutput).getExtrusionTask() == null)
                    {
                        throw new PostProcessingError("Unable to determine extrusion task type");
                    }
                    for (GCodeTranslationEventHandler listener : listeners)
                    {
                        listener.processEvent(eventToOutput);
                    }
                }
            }

            //End of file - poke the processor
            for (GCodeTranslationEventHandler listener : listeners)
            {
                listener.processEvent(new EndOfFileEvent());
            }
        } catch (FileNotFoundException ex)
        {
            System.out.println("File not found");
        } catch (IOException ex)
        {
            System.out.println("IO Exception");
        }
    }
}
