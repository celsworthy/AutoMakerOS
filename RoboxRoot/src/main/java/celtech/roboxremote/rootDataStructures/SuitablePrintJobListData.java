package celtech.roboxremote.rootDataStructures;

import celtech.roboxbase.comms.remote.clear.SuitablePrintJob;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author taldhous
 */
public class SuitablePrintJobListData
{
    public enum ListStatus {
        OK,
        NO_SUITABLE_JOBS,
        NO_JOBS,
        NO_MEDIA,
        NO_PRINTER,
        ERROR
    }
    
    @JsonIgnore
    private List<SuitablePrintJob> jobs = new ArrayList<>();
    @JsonIgnore
    private ListStatus status = ListStatus.OK;

    public SuitablePrintJobListData()
    {
        // Jackson deserialization
    }

    @JsonProperty
    public ListStatus getStatus()
    {
        return status;
    }

    @JsonProperty
    public void setStatus(ListStatus status)
    {
        this.status = status;
    }

    @JsonProperty
    public List<SuitablePrintJob> getJobs()
    {
        return jobs;
    }

    @JsonProperty
    public void setJobs(List<SuitablePrintJob> jobs)
    {
        this.jobs = jobs;
    }
}
