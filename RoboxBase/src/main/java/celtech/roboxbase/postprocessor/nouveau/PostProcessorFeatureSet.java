package celtech.roboxbase.postprocessor.nouveau;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ian
 */
public class PostProcessorFeatureSet
{
    private final List<PostProcessorFeature> featureSet = new ArrayList<>();
    
    public void enableFeature(PostProcessorFeature feature)
    {
        featureSet.add(feature);
    }
    
    public void disableFeature(PostProcessorFeature feature)
    {
        featureSet.remove(feature);
    }
    
    public boolean isEnabled(PostProcessorFeature feature)
    {
        return featureSet.contains(feature);
    }
}
