/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.services.printing;

import celtech.roboxbase.comms.rx.ListFilesResponse;
import java.util.ArrayList;

/**
 *
 * @author tony
 */
class TestListFilesResponse extends ListFilesResponse
{

    private final String jobId;

    public TestListFilesResponse()
    {
        this.jobId = null;
    }

    public TestListFilesResponse(String jobId)
    {
        this.jobId = jobId;
    }

    @Override
    public boolean populatePacket(byte[] byteData, float useFirmwareVersion)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ArrayList<String> getPrintJobIDs()
    {
        ArrayList<String> jobIds = new ArrayList<>();
        if (jobId != null)
        {
            jobIds.add(jobId);
        }
        return jobIds;
    }

    @Override
    public int packetLength(float requiredFirmwareVersion)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
