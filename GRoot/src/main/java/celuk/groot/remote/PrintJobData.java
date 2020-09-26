package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Ian
 */
public class PrintJobData
{
    private String printJobID;
    private String printJobName;
    private String printJobPath;
    private String printProfileName;
    private double durationInSeconds;
    private double eVolume;
    private double dVolume;
    private String creationDate;

    @JsonProperty
    public double getDurationInSeconds()
    {
        return durationInSeconds;
    }

    @JsonProperty
    public void setDurationInSeconds(double durationInSeconds)
    {
        this.durationInSeconds = durationInSeconds;
    }

    @JsonProperty
    public String getPrintJobID()
    {
        return printJobID;
    }

    @JsonProperty
    public void setPrintJobID(String printJobID)
    {
        this.printJobID = printJobID;
    }

    @JsonProperty
    public String getPrintJobName()
    {
        return printJobName;
    }

    @JsonProperty
    public void setPrintJobName(String printJobName)
    {
        this.printJobName = printJobName;
    }

    @JsonProperty
    public String getPrintJobPath() 
    {
        return printJobPath;
    }

    @JsonProperty
    public void setPrintJobPath(String printJobPath) 
    {
        this.printJobPath = printJobPath;
    }
    
    @JsonProperty
    public String getPrintProfileName()
    {
        return printProfileName;
    }

    @JsonProperty
    public void setPrintProfileName(String printProfileName)
    {
        this.printProfileName = printProfileName;
    }

    @JsonProperty
    public double getdVolume()
    {
        return dVolume;
    }

    @JsonProperty
    public void setdVolume(double dVolume)
    {
        this.dVolume = dVolume;
    }

    @JsonProperty
    public double geteVolume()
    {
        return eVolume;
    }

    @JsonProperty
    public void seteVolume(double eVolume)
    {
        this.eVolume = eVolume;
    }

    @JsonProperty
    public String getCreationDate()
    {
        return creationDate;
    }

    @JsonProperty
    public void setCreationDate(String creationDate)
    {
        this.creationDate = creationDate;
    }
}
