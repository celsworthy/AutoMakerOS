package celtech.roboxbase.postprocessor.nouveau.nodes;

import celtech.roboxbase.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class MCodeNode extends GCodeEventNode implements Renderable
{

    private int mNumber;
    private boolean sPresent = false;
    private boolean sNumberPresent = false;
    private int sNumber;
    private boolean tPresent = false;
    private boolean tNumberPresent = false;
    private int tNumber;
    private boolean ePresent = false;
    private boolean dPresent = false;
        private boolean cPresent = false;

    public MCodeNode()
    {
    }

    public MCodeNode(int mNumber)
    {
        this.mNumber = mNumber;
    }

    /**
     *
     * @return
     */
    public int getMNumber()
    {
        return mNumber;
    }

    /**
     *
     * @param value
     */
    public void setMNumber(int value)
    {
        this.mNumber = value;
    }

    /**
     *
     * @return
     */
    public int getSNumber()
    {
        return sNumber;
    }

    /**
     *
     * @param value
     */
    public void setSNumber(int value)
    {
        sPresent = true;
        sNumberPresent = true;
        this.sNumber = value;
    }

    /**
     *
     * @param sOnly
     */
    public void setSOnly(boolean sOnly)
    {
        sPresent = sOnly;
    }
    
    public boolean isSOnly()
    {
        return sPresent && !sNumberPresent;
    }

    public boolean isSAndNumber()
    {
        return sPresent && sNumberPresent;
    }

    /**
     *
     * @return
     */
    public int getTNumber()
    {
        return tNumber;
    }

    /**
     *
     * @param value
     */
    public void setTNumber(int value)
    {
        tPresent = true;
        tNumberPresent = true;
        this.tNumber = value;
    }

    /**
     *
     * @param tOnly
     */
    public void setTOnly(boolean tOnly)
    {
        tPresent = tOnly;
    }

    public boolean isTOnly()
    {
        return tPresent && !tNumberPresent;
    }

    public boolean isTAndNumber()
    {
        return tPresent && tNumberPresent;
    }

    public void setEOnly(boolean eOnly)
    {
        ePresent = eOnly;
    }

    public boolean isEPresent()
    {
        return ePresent;
    }
    
    public void setDOnly(boolean dOnly)
    {
        dPresent = dOnly;
    }

    public boolean isDPresent()
    {
        return dPresent;
    }
        
    public void setCPresent(boolean cPresent)
    {
        this.cPresent = cPresent;
    }
    
    public boolean isCPresent()
    {
        return cPresent;
    }
    
    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        StringBuilder stringToOutput = new StringBuilder();

        stringToOutput.append("M");
        stringToOutput.append(getMNumber());

        if (sPresent)
        {
            stringToOutput.append(" S");
            if (sNumberPresent)
            {
                stringToOutput.append(sNumber);
            }
        }

        if (tPresent)
        {
            stringToOutput.append(" T");
            if (tNumberPresent)
            {
                stringToOutput.append(tNumber);
            }
        }

        if (ePresent)
        {
            stringToOutput.append(" E");
        }

        if (dPresent)
        {
            stringToOutput.append(" D");
        }
        
        if (cPresent)
        {
            stringToOutput.append(" C");
        }

        stringToOutput.append(getCommentText());

        return stringToOutput.toString().trim();
    }
}
