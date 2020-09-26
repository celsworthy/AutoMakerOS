/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.comms.remote.tx;

import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.comms.tx.SetReelLEDColour;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tony
 */
public class SetReelLEDColourTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"@class\":\"celtech.roboxbase.comms.tx.SetReelLEDColour\",\"packetType\":\"SET_REEL_LED_COLOUR\",\"messagePayload\":\"aabbcc\",\"sequenceNumber\":44,\"includeSequenceNumber\":false,\"includeCharsOfDataInOutput\":false}";

    @Test
    public void serializesToJSON() throws Exception
    {
        final SetReelLEDColour packet = getTestPacket();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final SetReelLEDColour packet = getTestPacket();

        try
        {
            RoboxTxPacket packetRec = mapper.readValue(jsonifiedClass, RoboxTxPacket.class);
            assertEquals(packet, packetRec);
        } catch (Exception e)
        {
            System.out.println(e.getCause().getMessage());
            fail();
        }
    }

    private SetReelLEDColour getTestPacket()
    {
        SetReelLEDColour packet = new SetReelLEDColour();

        packet.setSequenceNumber(44);
        packet.setLEDColour("aabbcc");

        return packet;
    }

}
