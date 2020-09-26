package celtech.roboxbase.utils;

import celtech.roboxbase.postprocessor.PrintJobStatistics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import javax.imageio.ImageIO;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author ianhudson
 */
public class SystemUtils
{

    private static final Stenographer steno = StenographerFactory.getStenographer(SystemUtils.class.
            getName());

    /**
     *
     * @return
     */
    public static String generate16DigitID()
    {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
    }

    /**
     *
     * @param doubleA
     * @param doubleB
     * @param tolerance
     * @return
     */
    public static boolean isDoubleSame(double doubleA, double doubleB, double tolerance)
    {
        return Math.abs(doubleA - doubleB) < tolerance;
    }

    /**
     *
     * @param doubleA
     * @param doubleB
     * @return
     */
    public static boolean isDoubleSame(double doubleA, double doubleB)
    {
        return isDoubleSame(doubleA, doubleB, 1e-12);
    }

    /**
     *
     * @param inputString
     * @return
     * @throws InvalidChecksumException
     */
    public static char generateUPSModulo10Checksum(String inputString) throws InvalidChecksumException
    {
        int sum = 0;

        for (int i = 0; i < inputString.length(); i++)
        {
            int a = 0;
            switch (inputString.charAt(i))
            {
                case '0':
                    a = 0;
                    break;
                case '1':
                    a = 1;
                    break;
                case '2':
                    a = 2;
                    break;
                case '3':
                    a = 3;
                    break;
                case '4':
                    a = 4;
                    break;
                case '5':
                    a = 5;
                    break;
                case '6':
                    a = 6;
                    break;
                case '7':
                    a = 7;
                    break;
                case '8':
                    a = 8;
                    break;
                case '9':
                    a = 9;
                    break;
                case 'a':
                case 'A':
                    a = 2;
                    break;
                case 'b':
                case 'B':
                    a = 3;
                    break;
                case 'c':
                case 'C':
                    a = 4;
                    break;
                case 'd':
                case 'D':
                    a = 5;
                    break;
                case 'e':
                case 'E':
                    a = 6;
                    break;
                case 'f':
                case 'F':
                    a = 7;
                    break;
                case 'g':
                case 'G':
                    a = 8;
                    break;
                case 'h':
                case 'H':
                    a = 9;
                    break;
                case 'i':
                case 'I':
                    a = 0;
                    break;
                case 'j':
                case 'J':
                    a = 1;
                    break;
                case 'k':
                case 'K':
                    a = 2;
                    break;
                case 'l':
                case 'L':
                    a = 3;
                    break;
                case 'm':
                case 'M':
                    a = 4;
                    break;
                case 'n':
                case 'N':
                    a = 5;
                    break;
                case 'o':
                case 'O':
                    a = 6;
                    break;
                case 'p':
                case 'P':
                    a = 7;
                    break;
                case 'q':
                case 'Q':
                    a = 8;
                    break;
                case 'r':
                case 'R':
                    a = 9;
                    break;
                case 's':
                case 'S':
                    a = 0;
                    break;
                case 't':
                case 'T':
                    a = 1;
                    break;
                case 'u':
                case 'U':
                    a = 2;
                    break;
                case 'v':
                case 'V':
                    a = 3;
                    break;
                case 'w':
                case 'W':
                    a = 4;
                    break;
                case 'x':
                case 'X':
                    a = 5;
                    break;
                case 'y':
                case 'Y':
                    a = 6;
                    break;
                case 'z':
                case 'Z':
                    a = 7;
                    break;
            }

            if (i % 2 == 0)
            {
                sum += a;
            } else
            {
                sum += a * 2;
            }
        }

        sum = sum % 10;
        sum = 10 - sum;
        if (sum == 10)
        {
            sum = 0;
        }

        return Character.forDigit(sum, 10);
    }

    /**
     *
     * @param inputString
     * @return
     * @throws InvalidChecksumException
     */
    public static char generateModulo10Checksum(String inputString) throws InvalidChecksumException
    {
        boolean weighter = false;
// allowable characters within identifier
        String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVYWXZ";

// remove leading or trailing whitespace, convert to uppercase
        inputString = inputString.trim().toUpperCase();

// this will be a running total
        int sum = 0;

// loop through digits from right to left
        for (int i = 0; i < inputString.length(); i++)
        {

//set ch to "current" character to be processed
            char ch = inputString
                    .charAt(i);

// throw exception for invalid characters
            if (validChars.indexOf(ch) == -1)
            {
                throw new InvalidChecksumException(
                        "\"" + ch + "\" is an invalid character");
            }

// our "digit" is calculated using ASCII value - 48
            int digit = (int) ch - 48;

            //Weight will alternate between 1 and 3
// weight will be the current digit's contribution to
// the running total
//            int weight;
//            if (i % 2 == 0)
//            {
//
//                // for alternating digits starting with the rightmost, we
//                // use our formula this is the same as multiplying x 2 and
//                // adding digits together for values 0 to 9.  Using the
//                // following formula allows us to gracefully calculate a
//                // weight for non-numeric "digits" as well (from their
//                // ASCII value - 48).
////                weight = (2 * digit) - (int) (digit / 5) * 9;
//                weight = (2 * digit) - (int) (digit / 5) * 9;
//
//            } else
//            {
//
//                // even-positioned digits just contribute their ascii
//                // value minus 48
//                weight = digit;
//
//            }
//            // keep a running total of weights
//            sum += weight;
////            sum += (weighter == false) ? 3 : 1 * digit;
////            weighter = !weighter;
            sum += digit;
        }

// avoid sum less than 10 (if characters below "0" allowed,
// this could happen)
        sum = Math.abs(sum) + 10;

// check digit is amount needed to reach next number
// divisible by ten
        return Character.forDigit((10 - (sum % 10)) % 10, 10);

    }

    /**
     *
     * @param aFile
     * @return
     */
    public static int countLinesInFile(File aFile)
    {
        LineNumberReader reader = null;
        try
        {
            reader = new LineNumberReader(new FileReader(aFile));
            while ((reader.readLine()) != null);
            return reader.getLineNumber();
        } catch (Exception ex)
        {
            return -1;
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (IOException ex)
                {
                    steno.error("Failed to close file during line number read: " + ex);
                }
            }
        }
    }

    /**
     *
     * @param directory
     * @param filename
     * @param fileextension
     * @return
     */
    public static String getIncrementalFilenameOnly(String directory, String filename,
            String fileextension)
    {
        String chosenFilename = null;

        boolean notFound = true;
        int suffix = 1;

//        File testFile = new File(directory + File.separator + filename + "_" + suffix + fileextension);
//
//        if (testFile.exists() == true)
//        {
        while (notFound)
        {
            File outfile = new File(directory + File.separator + filename + "_" + suffix
                    + fileextension);
            if (!outfile.exists())
            {
                chosenFilename = outfile.getName().replaceFirst("\\..*$", "");
                break;
            }

            suffix++;
        }
//        }
//        else
//        {
//            chosenFilename = filename;
//        }

        return chosenFilename;
    }

    /**
     *
     * @param image
     * @return
     * @throws IOException
     */
    public static javafx.scene.image.Image createImage(java.awt.Image image) throws IOException
    {
        if (!(image instanceof RenderedImage))
        {
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = bufferedImage.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();

            image = bufferedImage;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write((RenderedImage) image, "png", out);
        out.flush();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return new javafx.scene.image.Image(in);
    }

    /**
     *
     * @param gcode
     * @return
     */
    public static String cleanGCodeForTransmission(String gcode)
    {
        return gcode.trim().replaceFirst(";.*$", "").replaceFirst("\\s+$", "");
    }
    
    /**
     *
     * @param source
     * @return
     */
    public static String jsonEscape(String source)
    {
        // Return a copy of the source with all unicode characters greater than 128 escaped
        // as backslash u followed by the hex value for the codepoint.
        if (source != null)
        {
            StringBuilder sb = new StringBuilder(source.length() + 4);
            source.codePoints().forEach(c -> 
                                        {
                                            if (c > 0x7f)
                                            {
                                                // Encode as \\uHHHH
                                                String t = Integer.toHexString(c).toUpperCase();
                                                while (t.length() < 4)
                                                    t = "0" + t;
                                                sb.append("\\u");
                                                sb.append(t.substring(t.length() - 4));
                                            }
                                            else
                                            {
                                                sb.appendCodePoint(c);
                                            }
                                        });
            return sb.toString();
        }
        else
            return "";
    }

    public static boolean downloadFromUrl(URL url, String localFilename, PercentProgressReceiver progressReceiver) throws IOException
    {
        boolean success = false;
        InputStream is = null;
        FileOutputStream fos = null;

        try
        {
            URLConnection urlConn = url.openConnection();//connect

            is = urlConn.getInputStream();               //get connection inputstream
            fos = new FileOutputStream(localFilename);   //open outputstream to local file
            int bytesToReceive = urlConn.getContentLength();

            byte[] buffer = new byte[4096];              //declare 4KB buffer
            int len;
            int bytesRead = 0;

            //while we have availble data, continue downloading and storing to local file
            while ((len = is.read(buffer)) > 0)
            {
                bytesRead += len;
                double percentProgress = ((double) bytesRead / (double) bytesToReceive) * 100.0;
                progressReceiver.updateProgressPercent(percentProgress);
                fos.write(buffer, 0, len);
            }
            success = true;
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

        return success;
    }
}
