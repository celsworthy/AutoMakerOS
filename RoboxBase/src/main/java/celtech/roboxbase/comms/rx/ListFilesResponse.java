/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.comms.rx;

import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import java.util.ArrayList;

/**
 *
 * @author tony
 */
public abstract class ListFilesResponse extends RoboxRxPacket
{

    /**
     *
     */
    public ListFilesResponse()
    {
        super(RxPacketTypeEnum.LIST_FILES_RESPONSE, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public abstract boolean populatePacket(byte[] byteData, float requiredFirmwareVersion);

    /**
     *
     * @return
     */
    public abstract ArrayList<String> getPrintJobIDs();

}
