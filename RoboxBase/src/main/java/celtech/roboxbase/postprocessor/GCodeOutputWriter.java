package celtech.roboxbase.postprocessor;

import java.io.IOException;

/**
 *
 * @author Ian
 */
public interface GCodeOutputWriter
{

    void close() throws IOException;

    void flush() throws IOException;

    /**
     * @return the numberOfLinesOutput
     */
    int getNumberOfLinesOutput();

    void newLine() throws IOException;

    void writeOutput(String outputLine) throws IOException;

    public void incrementLinesOfOutput(int numberToIncrementBy);
}
