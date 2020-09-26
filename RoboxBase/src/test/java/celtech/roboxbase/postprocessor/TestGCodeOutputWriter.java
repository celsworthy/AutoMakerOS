package celtech.roboxbase.postprocessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ian
 */
public class TestGCodeOutputWriter implements GCodeOutputWriter
{

    private int numberOfLinesOutput = 0;
    List<String> writtenLines = new ArrayList<>();

    public TestGCodeOutputWriter(String filename) throws IOException
    {
    }

    @Override
    public void close() throws IOException
    {
    }

    @Override
    public void flush() throws IOException
    {
    }

    @Override
    public int getNumberOfLinesOutput()
    {
        return numberOfLinesOutput;
    }

    @Override
    public void newLine() throws IOException
    {
    }

    @Override
    public void writeOutput(String outputLine) throws IOException
    {
        writtenLines.add(outputLine);
        numberOfLinesOutput++;
    }

    @Override
    public void incrementLinesOfOutput(int numberToIncrementBy)
    {
        numberOfLinesOutput += numberToIncrementBy;
    }
}
