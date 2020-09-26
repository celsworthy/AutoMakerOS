package celtech.roboxbase.services.slicer;

import celtech.roboxbase.configuration.SlicerType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
class SlicerOutputGobbler extends Thread
{

    private final InputStream is;
    private final String type;
    private final Stenographer steno = StenographerFactory.getStenographer(this.getClass().getName());
    private final ProgressReceiver progressReceiver;
    private final SlicerType slicerType;

    SlicerOutputGobbler(ProgressReceiver progressReceiver, InputStream is, String type,
        SlicerType slicerType)
    {
        this.progressReceiver = progressReceiver;
        this.is = is;
        this.type = type;
        this.slicerType = slicerType;
        this.setName("SlicerOutputGobbler");
    }

    @Override
    public void run()
    {
        if (slicerType == SlicerType.Slic3r)
        {
            setLoadProgress("Slicing meshes", 5);
        }

        try
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int printCounter = 0;
            while ((line = br.readLine()) != null)
            {
                printCounter++;
                if (slicerType == SlicerType.Slic3r)
                {
                    steno.debug(">" + line);
                    if (line.contains("Processing triangulated mesh"))
                    {
                        setLoadProgress("Slicing perimeters", 10);
                    } else if (line.contains("Generating perimeters"))
                    {
                        setLoadProgress("Slicing solid surfaces", 20);
                    } else if (line.contains("Detecting solid surfaces"))
                    {
                        setLoadProgress("Slicing infill", 30);
                    } else if (line.contains("Preparing infill surfaces"))
                    {
                        setLoadProgress("Slicing horizontal shells", 40);
                    } else if (line.contains("Generating horizontal shells"))
                    {
                        setLoadProgress("Slicing infill", 50);
                    } else if (line.contains("Combining infill"))
                    {
                        setLoadProgress("Slicing layers", 60);
                    } else if (line.contains("Infilling layers"))
                    {
                        setLoadProgress("Slicing skirt", 70);
                    } else if (line.contains("Generating skirt"))
                    {
                        setLoadProgress("Slice exporting", 80);
                    } else if (line.startsWith("Exporting"))
                    {
                    } else if (line.startsWith("Done"))
                    {
                        setLoadProgress("Slicing complete", 100);
                    }
                } else if (slicerType == SlicerType.Cura || slicerType == SlicerType.Cura4)
                {
                    if (line.startsWith("Progress"))
                    {
                        String[] lineParts = line.split(":");
                        if (lineParts.length == 4)
                        {
                            String task = lineParts[1];
                            int progressInt = 0;

                            float workDone = Float.valueOf(lineParts[2]);
                            float totalWork = slicerType == SlicerType.Cura4 ? parseTotalWork(lineParts[3]) : Float.valueOf(lineParts[3]);

                            if (workDone == 1f || workDone == totalWork || printCounter >= 40)
                            {
                                printCounter = 0;
                                steno.debug(">" + line);
                            }
                            
                            if (task.equalsIgnoreCase("inset"))
                            {
                                progressInt = (int) ((workDone / totalWork) * 25);
                            } else if (task.contains("skin"))
                            {
                                progressInt = (int) ((workDone / totalWork) * 25) + 25;
                            } else if (task.equalsIgnoreCase("export"))
                            {
                                progressInt = (int) ((workDone / totalWork) * 49) + 50;
                            } else if (task.equalsIgnoreCase("process"))
                            {
                                progressInt = (int) ((workDone / totalWork) * 1) + 99;
                            }
                            setLoadProgress(task, progressInt);
                        }
                    } else
                    {
                        printCounter = 0;
                        steno.debug(">" + line);
                    }
                }
            }
        } catch (IOException ioe)
        {
            steno.error(ioe.getMessage());
        }
    }

    private void setLoadProgress(final String loadMessage, final int percentProgress)
    {
        if (progressReceiver != null)
        {
            progressReceiver.progressUpdateFromSlicer(loadMessage, percentProgress);
        }
    }
    
    private float parseTotalWork(String totalWork)
    {
        String[] lineParts = totalWork.split(" ");
        return Float.valueOf(lineParts[0]);
    }
    
    private float parsePercentagePart(String percentagePart) 
    {
        String[] lineParts = percentagePart.split(" ");
        String value = lineParts[1].replace("%", "");
        return Float.valueOf(value);
    }
}
