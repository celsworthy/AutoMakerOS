package celtech.roboxbase.services.printing;

import celtech.roboxbase.comms.DetectedServer;
import celtech.roboxbase.comms.RemoteDetectedPrinter;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.remote.RoboxRemoteCommandInterface;
import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.fileRepresentation.CameraSettings;
import celtech.roboxbase.configuration.hardwarevariants.PrinterType;
import celtech.roboxbase.postprocessor.PrintJobStatistics;
import celtech.roboxbase.printerControl.comms.commands.GCodeMacros;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.utils.SystemUtils;
import com.jcraft.jsch.SftpProgressMonitor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import javafx.beans.property.IntegerProperty;
import javafx.concurrent.Task;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class TransferGCodeToPrinterTask extends Task<GCodePrintResult>
{
    private class TransferProgressMonitor implements SftpProgressMonitor
    {
        long count = 0;
        long fileSize = 0;
        DetectedServer server;
        
        public TransferProgressMonitor(DetectedServer server)
        {
            this.server = server;
        }

        @Override
        public void init(int op, String src, String dest, long fileSize)
        {
            this.fileSize = fileSize;
            this.count = 0;
            if (server != null)
                server.resetPollCount();
            steno.debug("Initialise file transfer: src = \"" + src + "\", dst = \"" + dest + "\", fileSize = " + Long.toString(fileSize));
        }

        @Override
        public boolean count(long increment)
        {
          count += increment;
          steno.debug("Transfer progress: count = " + Long.toString(count) + " of " + Long.toString(fileSize));
          updateProgress((float) count, (float) fileSize);
          if (server != null)
                server.resetPollCount();
          return !isCancelled();
        }
        
        @Override
        public void end(){
        }
    }
           
    private Printer printerToUse = null;
    private String gcodeFileToPrint = null;
    private String printJobID = null;
    private final Stenographer steno = StenographerFactory.getStenographer(this.getClass().getName());
    private IntegerProperty linesInFile = null;
    private boolean printUsingSDCard = true;
    private boolean dontInitiatePrint = false;
    private int startFromSequenceNumber = 0;
    private boolean thisJobCanBeReprinted = false;
    private int lineCounter = 0;
    private int numberOfLines = 0;
    private final PrintJobStatistics printJobStatistics;
    private final CameraSettings cameraData;

    /**
     *
     * @param printerToUse
     * @param modelFileToPrint
     * @param printJobID
     * @param linesInFile
     * @param printUsingSDCard
     * @param startFromSequenceNumber
     * @param thisJobCanBeReprinted
     * @param dontInitiatePrint
     * @param printJobStatistics
     * @param cameraData
     */
    public TransferGCodeToPrinterTask(Printer printerToUse,
            String modelFileToPrint,
            String printJobID,
            IntegerProperty linesInFile,
            boolean printUsingSDCard,
            int startFromSequenceNumber,
            boolean thisJobCanBeReprinted,
            boolean dontInitiatePrint,
            PrintJobStatistics printJobStatistics,
            CameraSettings cameraData)
    {
        this.printerToUse = printerToUse;
        this.gcodeFileToPrint = modelFileToPrint;
        this.printJobID = printJobID;
        this.linesInFile = linesInFile;
        this.printUsingSDCard = printUsingSDCard;
        this.startFromSequenceNumber = startFromSequenceNumber;
        this.thisJobCanBeReprinted = thisJobCanBeReprinted;
        this.dontInitiatePrint = dontInitiatePrint;
        this.printJobStatistics = printJobStatistics;
        this.cameraData = cameraData;
        updateProgress(0.0, 100.0);
    }

    @Override
    protected GCodePrintResult call() throws Exception
    {
        long startTime = System.currentTimeMillis();
        GCodePrintResult result = new GCodePrintResult();
        result.setPrintJobID(printJobID);

        boolean gotToEndOK = false;

        updateTitle("GCode Print ID:" + printJobID);
        File gcodeFile = new File(gcodeFileToPrint);
        Optional<PrinterType> printerType = Optional.of(printerToUse.findPrinterType());
        numberOfLines = GCodeMacros.countLinesInMacroFile(gcodeFile, ";", printerType);
        linesInFile.setValue(numberOfLines);

        steno.debug("Beginning transfer of file " + gcodeFileToPrint + " to printer from line "
                + startFromSequenceNumber);

        boolean errorTransferringStats = false;
        boolean printerIsRemote = (printerToUse.getCommandInterface() instanceof RoboxRemoteCommandInterface);
        
        if (printerIsRemote)
        {
            //We're talking to a remote printer
            //Send the statistics and camera data if they exist
            if (!gcodeFile.getParent().endsWith("Macros") && printJobStatistics != null)
            {
                try
                {
                    ((RoboxRemoteCommandInterface) printerToUse.getCommandInterface()).sendStatistics(printJobStatistics);
                    if (cameraData != null)
                        ((RoboxRemoteCommandInterface) printerToUse.getCommandInterface()).sendCameraData(printJobID, cameraData);
                        
                } catch (RoboxCommsException ex)
                {
                    errorTransferringStats = true;
                }
            }
        }

        if (!errorTransferringStats)
        {
            updateMessage("Transferring GCode");

            if (printerIsRemote &&
                !gcodeFile.getParent().endsWith("Macros") &&
                printUsingSDCard &&
                startFromSequenceNumber == 0)
                gotToEndOK = transferToRemotePrinter(gcodeFile);
            else
                gotToEndOK = transferToPrinter(gcodeFile);
        }

        long endTime = System.currentTimeMillis();
        steno.info("Print transfer took " + Long.toString(endTime - startTime) + "ms");

        result.setSuccess(gotToEndOK);
        return result;
    }

    private boolean transferToPrinter(File gcodeFile) throws Exception
    {
        FileReader gcodeReader = null;
        Scanner scanner = null;
        boolean gotToEndOK = false;
        //Note that FileReader is used, not File, since File is not Closeable
        try
        {
            gcodeReader = new FileReader(gcodeFile);
            scanner = new Scanner(gcodeReader);

            if (printUsingSDCard && startFromSequenceNumber == 0)
            {
                printerToUse.initialiseDataFileSend(printJobID, thisJobCanBeReprinted);
            }

            printerToUse.resetDataFileSequenceNumber();
            printerToUse.setDataFileSequenceNumberStartPoint(startFromSequenceNumber);

            lineCounter = 0;

            while (scanner.hasNextLine() && !isCancelled())
            {
                String line = scanner.nextLine();
                line = line.trim();

                if (GCodeMacros.isMacroExecutionDirective(line))
                {
                    //Put in contents of macro
                    List<String> macroLines = GCodeMacros.getMacroContents(line,
                            Optional.of(printerToUse.findPrinterType()),
                            printerToUse.headProperty().get().typeCodeProperty().get(),
                            false, false, false);
                    for (String macroLine : macroLines)
                    {
                        outputLine(macroLine);
                    }
                } else
                {
                    outputLine(line);
                }

                if (lineCounter < numberOfLines)
                {
                    updateProgress((float) lineCounter, (float) numberOfLines);
                }
            }
            gotToEndOK = true;
        } catch (FileNotFoundException ex)
        {
            steno.error("Couldn't open gcode file " + gcodeFileToPrint + ": " + ex);
        } catch (RoboxCommsException ex)
        {
            steno.error("Error during print operation - abandoning transfer of " + printJobID + " " + ex.
                    getMessage());
            updateMessage("Printing error");
        } finally
        {
            if (scanner != null)
            {
                scanner.close();
            }

            if (gcodeReader != null)
            {
                gcodeReader.close();
            }
        }
        return gotToEndOK;
    }

    private void outputLine(String line) throws RoboxCommsException, DatafileSendNotInitialised
    {
        if (line.equals("") == false && line.startsWith(";") == false)
        {
            line = SystemUtils.cleanGCodeForTransmission(line);
            if (printUsingSDCard)
            {
                steno.trace("Sending data line " + lineCounter + " to printer");
                printerToUse.sendDataFileChunk(line, lineCounter == numberOfLines - 1,
                        true);
                if (startFromSequenceNumber == 0
                        && !dontInitiatePrint
                        && ((printerToUse.getDataFileSequenceNumber() > 1
                        && printerToUse.isPrintInitiated() == false)
                        || (lineCounter == numberOfLines - 1
                        && printerToUse.isPrintInitiated() == false)))
                {
                    //Start printing!
                    printerToUse.initiatePrint(printJobID);
                }
            } else
            {
                printerToUse.sendRawGCode(line, false);
            }
            lineCounter++;
        }
    }
    
    private boolean transferToRemotePrinter(File gcodeFile)
    {
        // Use sftp to transfer file to remote printer.
        // Note: this does NOT expand macros, which has to
        // be done on the remote printer.
        boolean transferredOK = false;
        RoboxRemoteCommandInterface remoteCI = (RoboxRemoteCommandInterface)printerToUse.getCommandInterface();
        RemoteDetectedPrinter remoteDevice = (RemoteDetectedPrinter)remoteCI.getPrinterHandle();
        String hostAddress = remoteDevice.getServerPrinterIsAttachedTo().getServerIP();
            
        SFTPUtils sftpHelper = new SFTPUtils(hostAddress);
        SftpProgressMonitor monitor = new TransferGCodeToPrinterTask.TransferProgressMonitor(remoteDevice.getServerPrinterIsAttachedTo());
        String remoteDirectory = BaseConfiguration.getRemotePrintJobDirectory() + printJobID;
        if (sftpHelper.transferToRemotePrinter(gcodeFile, remoteDirectory, gcodeFile.getName(), monitor))
        {
            try
            {
                steno.info("Transferred GCode");
                if (printJobStatistics != null)
                    remoteCI.startPrintJob(printJobID);
                else
                    remoteCI.printGCodeFile(remoteDirectory + '/' + gcodeFile.getName());
                transferredOK = true;
            }
            catch (RoboxCommsException ex)
            {
                if (thisJobCanBeReprinted && printJobStatistics != null)
                    steno.exception("Failed to start remote print job\"" + printJobID + "\".", ex);
                else
                    steno.exception("Failed to start remote print of file \"" + gcodeFile.getName() + "\".", ex);
            }
        }
        
        return transferredOK;
    }
}
