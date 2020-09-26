/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase.comms;

import celtech.roboxbase.printerControl.model.HardwarePrinter;
import celtech.roboxbase.printerControl.model.HeaterMode;
import celtech.roboxbase.utils.BaseEnvironmentConfiguredTest;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tony
 */
public class DummyPrinterCommandInterfaceTest extends BaseEnvironmentConfiguredTest
{

    private DetectedDevice printerHandle = new DetectedDevice(DeviceDetector.DeviceConnectionType.SERIAL, "Test Printer");

    @ClassRule
    public static TemporaryFolder temporaryUserStorageFolder = new TemporaryFolder();

    @Test
    public void testSetNozzleTargetTemperature() throws Exception
    {
        StatusConsumer statusConsumer = new StatusConsumer();
        DummyPrinterCommandInterface commandInterface = new DummyPrinterCommandInterface(
                statusConsumer, printerHandle, false, 500);
        commandInterface.connectToPrinter();
        
        HardwarePrinter hardwarePrinter = new HardwarePrinter(statusConsumer, commandInterface);

        hardwarePrinter.sendRawGCode("ATTACH HEAD RBX01-DM", true);

        hardwarePrinter.setNozzleHeaterTargetTemperature(0, 200);

        assertEquals(210, commandInterface.nozzleTargetTemperatureS);

//        NozzleHeater nozzleHeater = hardwarePrinter.headProperty().get().getNozzleHeaters().get(0);
//        assertEquals(200, nozzleHeater.nozzleTargetTemperatureProperty().get());
    }

    //@Test DISABLED23/09/15
    public void testGotoTargetNozzleTemperature() throws Exception
    {
        StatusConsumer statusConsumer = new StatusConsumer();
        DummyPrinterCommandInterface commandInterface = new DummyPrinterCommandInterface(
                statusConsumer, printerHandle, false, 500);
        HardwarePrinter hardwarePrinter = new HardwarePrinter(statusConsumer, commandInterface);
        commandInterface.setPrinter(hardwarePrinter);
        hardwarePrinter.goToTargetNozzleHeaterTemperature(0);

        assertEquals(HeaterMode.NORMAL, commandInterface.nozzleHeaterModeS);

    }

    //@Test DISABLED23/09/15
    public void testSwitchAllNozzleHeatersOff() throws Exception
    {
        StatusConsumer statusConsumer = new StatusConsumer();
        DummyPrinterCommandInterface commandInterface = new DummyPrinterCommandInterface(
                statusConsumer, printerHandle, false, 500);
        HardwarePrinter hardwarePrinter = new HardwarePrinter(statusConsumer, commandInterface);
        commandInterface.setPrinter(hardwarePrinter);

        hardwarePrinter.goToTargetNozzleHeaterTemperature(0);
        assertEquals(HeaterMode.NORMAL, commandInterface.nozzleHeaterModeS);
        hardwarePrinter.switchAllNozzleHeatersOff();
        assertEquals(HeaterMode.OFF, commandInterface.nozzleHeaterModeS);

    }

    class StatusConsumer implements PrinterStatusConsumer
    {

        @Override
        public void printerConnected(DetectedDevice printerHandle)
        {
        }

        @Override
        public void disconnected(DetectedDevice printerHandle)
        {
        }
    }

}
