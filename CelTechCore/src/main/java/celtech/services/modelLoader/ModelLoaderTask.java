package celtech.services.modelLoader;

import celtech.Lookup;
import celtech.coreUI.visualisation.metaparts.ModelLoadResult;
import celtech.coreUI.visualisation.metaparts.ModelLoadResultType;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.utils.FileUtilities;
import celtech.utils.threed.importers.obj.ObjImporter;
import celtech.utils.threed.importers.stl.STLImporter;
import celtech.utils.threed.importers.svg.SVGImporter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class ModelLoaderTask extends Task<ModelLoadResults>
{
    
    private Stenographer steno = StenographerFactory.
            getStenographer(ModelLoaderTask.class.getName());
    
    private final List<File> modelFilesToLoad;
    private final DoubleProperty percentProgress = new SimpleDoubleProperty();
    
    public ModelLoaderTask(List<File> modelFilesToLoad)
    {
        this.modelFilesToLoad = modelFilesToLoad;
        
        percentProgress.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                updateProgress(t1.doubleValue(), 100.0);
            }
        });
    }
    
    @Override
    protected ModelLoadResults call() throws Exception
    {
        List<ModelLoadResult> modelLoadResultList = new ArrayList<>();
        
        updateTitle(Lookup.i18n("dialogs.loadModelTitle"));
        
        for (File modelFileToLoad : modelFilesToLoad)
        {
            steno.info("Model file load started:" + modelFileToLoad.getName());
            
            String modelFilePath = modelFileToLoad.getAbsolutePath();
            updateMessage(Lookup.i18n("dialogs.gcodeLoadMessagePrefix")
                    + modelFileToLoad.getName());
            updateProgress(0, 100);
            
            final List<String> fileNamesToLoad = new ArrayList<>();
            
            if (modelFilePath.toUpperCase().endsWith("ZIP"))
            {
//                modelLoadResults.setShouldCentre(false);
                ZipFile zipFile = new ZipFile(modelFilePath);
                
                try
                {
                    final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    while (entries.hasMoreElements())
                    {
                        final ZipEntry entry = entries.nextElement();
                        final String tempTargetname = BaseConfiguration.getUserTempDirectory() + entry.getName();
                        FileUtilities.writeStreamToFile(zipFile.getInputStream(entry), tempTargetname);
                        fileNamesToLoad.add(tempTargetname);
                    }
                } catch (IOException ex)
                {
                    steno.error("Error unwrapping zip - " + ex.getMessage());
                } finally
                {
                    zipFile.close();
                }
            } else
            {
                fileNamesToLoad.add(modelFilePath);
            }
            
            for (String filenameToLoad : fileNamesToLoad)
            {
                ModelLoadResult loadResult = loadTheFile(filenameToLoad);
                if (loadResult != null)
                {
                    modelLoadResultList.add(loadResult);
                } else
                {
                    steno.warning("Failed to load model: " + filenameToLoad);
                }
            }
        }
        
        ModelLoadResultType type = null;
        if (!modelLoadResultList.isEmpty())
        {
            type = modelLoadResultList.get(0).getType();
        }
        return new ModelLoadResults(type, modelLoadResultList);
    }
    
    private ModelLoadResult loadTheFile(String modelFileToLoad)
    {
        ModelLoadResult modelLoadResult = null;
        try {
            if (modelFileToLoad.toUpperCase().endsWith("OBJ"))
            {
                ObjImporter reader = new ObjImporter();
                modelLoadResult = reader.loadFile(this, modelFileToLoad, percentProgress, false);
            } else if (modelFileToLoad.toUpperCase().endsWith("STL"))
            {
                STLImporter reader = new STLImporter();
                modelLoadResult = reader.loadFile(this, new File(modelFileToLoad),
                        percentProgress);
            } else if (modelFileToLoad.toUpperCase().endsWith("SVG"))
            {
                SVGImporter reader = new SVGImporter();
                modelLoadResult = reader.loadFile(this, new File(modelFileToLoad),
                        percentProgress);
            }
        }
        catch (Exception ex) {
            System.out.println("Model load failed : " + ex.getMessage());
        }        
        return modelLoadResult;
    }

    /**
     *
     * @param message
     */
    public void updateMessageText(String message)
    {
        updateMessage(message);
    }
}
