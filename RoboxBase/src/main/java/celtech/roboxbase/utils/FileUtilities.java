package celtech.roboxbase.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Ian
 */
public class FileUtilities
{
    // illegalChars must be sorted in ascending order.
    final static int[] illegalChars = {0, // NULL
                                       1, // Start of Heading (SOH)
                                       2, // Start of Text (STX)
                                       3, // End of Text (ETX)
                                       4, // End of Transmission (EOT)
                                       5, // Enquiry (ENQ)
                                       6, // Acknowledge (ACK)
                                       7, // Bell (BEL)
                                       8, // Backsace (BS)
                                       9, // Horizontal Tabulation (HT)
                                       10, // Line Feed (LF)
                                       11, // Vertical Tabulation (VT)
                                       12, // Form Feed (FF)
                                       13, // Carriage Return (CR)
                                       14, // Shift Out (SO)
                                       15, // Shift In (SI)
                                       16, // Data Link Escape (DLE)
                                       17, // Device Control One (DC1)
                                       18, // Device Control Two (DC2)
                                       19, // Device Control Three (DC3)
                                       20, // Device Control Four (DC4)
                                       21, // Negative Acknowledge (NAK)
                                       22, // Synchronous Idle (SYN)
                                       23, // End of Transmission Block (ETB)
                                       24, // Cancel (CAN)
                                       25, // End of Medium (EM)
                                       26, // Substitute (SUB)
                                       27, // Escape (ESC)
                                       28, // File Separator (FS)
                                       29, // Group Separator (GS)
                                       30, // Record Separator (RS)
                                       31, // Unit Separator (US)
                                       34, // Quotation Mark (")
                                       42, // Asterisk (*)
                                       47, // Forward Slash (Solidus) (/)
                                       58, // Colon (:)
                                       60, // Less-Than Sign (<)
                                       62, // Greater-Than Sign (>)
                                       63, // Question Mark (?)
                                       92, // Back Slash (Reverse Solidus) (\)
                                       124}; // Vertical Line (|)
   
    public static String cleanFileName(String badFileName)
    {
        StringBuilder cleanName = new StringBuilder();
        int len = badFileName.codePointCount(0, badFileName.length());
        for (int i=0; i<len; i++)
        {
            int c = badFileName.codePointAt(i);
            if (Arrays.binarySearch(illegalChars, c) < 0)
                cleanName.appendCodePoint(c);
            else
                cleanName.appendCodePoint(95); // Underscore.
        }
        return cleanName.toString();
    }

    public static void writeStreamToFile(InputStream is, String localFilename) throws IOException
    {
        FileOutputStream fos = null;

        File localFile = new File(localFilename);
        fos = FileUtils.openOutputStream(localFile);

        try
        {

            byte[] buffer = new byte[4096];              //declare 4KB buffer
            int len;

            //while we have availble data, continue downloading and storing to local file
            while ((len = is.read(buffer)) > 0)
            {
                fos.write(buffer, 0, len);
            }
        } finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            } finally
            {
                if (fos != null)
                {
                    fos.close();
                }
            }
        }
    }
    
    /**
     * Return the sub folder specified as a string after a check to see if the file exists.
     * Create the file if not.
     * 
     * @param parentDir the parent directory path
     * @param fileName the file name
     * @return the path to the sub directory as a String
     */
    public static String findOrCreateFileInDir(Path parentDir, String fileName)
    {
        String subDirPath = parentDir + File.separator + fileName;
        File subDir = new File(subDirPath);
        if(!subDir.exists())
        {
            subDir.mkdir();
        }
        return subDirPath;
    }

}
