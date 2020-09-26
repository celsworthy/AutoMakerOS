package celtech.utils;

import java.io.File;
import java.util.List;

/**
 *
 * @author Ian
 */
public class MyMiniFactoryLoadResult
{
    private boolean success;
    private List<File> filesToLoad;

    public MyMiniFactoryLoadResult()
    {
    }

    public MyMiniFactoryLoadResult(boolean success, List<File> filesToLoad)
    {
        this.success = success;
        this.filesToLoad = filesToLoad;
    }    

    public boolean isSuccess()
    {
        return success;
    }

    public List<File> getFilesToLoad()
    {
        return filesToLoad;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public void setFilesToLoad(List<File> filesToLoad)
    {
        this.filesToLoad = filesToLoad;
    }
}
