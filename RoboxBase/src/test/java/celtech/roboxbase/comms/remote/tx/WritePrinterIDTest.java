/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.comms.remote.tx;

import celtech.roboxbase.comms.tx.RoboxTxPacket;
import celtech.roboxbase.comms.tx.WritePrinterID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tony
 */
public class WritePrinterIDTest
{

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String jsonifiedClass = "{\"@class\":\"celtech.roboxbase.comms.tx.WritePrinterID\",\"packetType\":\"WRITE_PRINTER_ID\",\"messagePayload\":\"RBX01B C D E      F   G1\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000                                                                                                SQ==\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000\\u0000abcdef\",\"sequenceNumber\":44,\"includeSequenceNumber\":false,\"includeCharsOfDataInOutput\":false,\"model\":\"RBX01\",\"edition\":\"B\",\"weekOfManufacture\":\"C\",\"yearOfManufacture\":\"D\",\"poNumber\":\"E\",\"serialNumber\":\"F\",\"checkByte\":\"G\",\"electronicsVersion\":\"1\",\"printerFriendlyName\":\"I\",\"colourWebString\":\"abcdef\"}";

    @Test
    public void serializesToJSON() throws Exception
    {
        final WritePrinterID packet = getTestPacket();

        String mappedValue = mapper.writeValueAsString(packet);
        assertEquals(jsonifiedClass, mappedValue);
    }

    @Test
    public void deserializesFromJSON() throws Exception
    {
        final WritePrinterID packet = getTestPacket();

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

    private WritePrinterID getTestPacket()
    {
        WritePrinterID packet = new WritePrinterID();

        packet.setSequenceNumber(44);
        packet.setIDAndColour("RBX01",
                "B",
                "C",
                "D",
                "E",
                "F",
                "G",
                "1",
                "I",
                "abcdef");

        return packet;
    }

}
