package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;
import java.util.Optional;

/**
 *
 * @author Ian
 */
public class GCodeDirectiveNode extends GCodeEventNode implements Renderable
{

    private int gValue = -1;
    private boolean sValueSet = false;
    private int sValue = 0;
    private boolean pValueSet = false;
    private int pValue = 0;

    public Integer getGValue()
    {
        return gValue;
    }

    public void setGValue(int gValue)
    {
        this.gValue = gValue;
    }

    public void clearPValue()
    {
        pValueSet = false;
        this.pValue = 0;
    }

    public void setPValue(int pValue)
    {
        pValueSet = true;
        this.pValue = pValue;
    }

    public Optional<Integer> getPValue()
    {
        if (pValueSet)
        {
            return Optional.of(pValue);
        } else
        {
            return Optional.empty();
        }
    }

    public void clearSValue()
    {
        sValueSet = false;
        this.sValue = 0;
    }

    public void setSValue(int sValue)
    {
        sValueSet = true;
        this.sValue = sValue;
    }

    public Optional<Integer> getSValue()
    {
        if (sValueSet)
        {
            return Optional.of(sValue);
        } else
        {
            return Optional.empty();
        }
    }

    @Override
    public String renderForOutput()
    {
        StringBuilder stringToOutput = new StringBuilder();

        stringToOutput.append("G");
        stringToOutput.append(gValue);
        
        if (sValueSet)
        {
            stringToOutput.append(" S");
            stringToOutput.append(sValue);
        }

        if (pValueSet)
        {
            stringToOutput.append(" P");
            stringToOutput.append(pValue);
        }
        
        stringToOutput.append(getCommentText());
        return stringToOutput.toString().trim();
    }
}
