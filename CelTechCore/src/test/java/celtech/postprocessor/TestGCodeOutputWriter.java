package celtech.postprocessor;

import celtech.roboxbase.postprocessor.GCodeOutputWriter;
import java.io.IOException;

/**
 *
 * @author Ian
 */
public class TestGCodeOutputWriter implements GCodeOutputWriter
{

    private int numberOfLinesOutput = 0;

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
        // if it's not a comment or blank line
        if (!outputLine.trim().startsWith(";") && !"".equals(
                outputLine.trim()))
        {
            numberOfLinesOutput++;
        }
    }

    @Override
    public void incrementLinesOfOutput(int numberToIncrementBy)
    {
        numberOfLinesOutput += numberToIncrementBy;
    }
}
