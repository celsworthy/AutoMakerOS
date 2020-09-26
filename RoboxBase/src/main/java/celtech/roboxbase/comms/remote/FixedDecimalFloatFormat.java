/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.remote;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.ParseException;
import java.util.Locale;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 * This class provides a formatter suitable for used in Robox comms.
 *
 * The output string is 8 characters maximum, hence the largest possible number
 * is 99999999 The minimum possible value is 0.000001
 *
 * Leading spaces are used - e.g. ' 123.3' Negative numbers are also represented
 * using leading spaces - e.g. ' -1232'
 *
 * NB - DecimalFormat is NOT threadsafe, so instantiate one of these per thread
 * at a minimum
 *
 * @author Ian
 */
public class FixedDecimalFloatFormat extends DecimalFormat
{

    private static final Stenographer steno = StenographerFactory.getStenographer(FixedDecimalFloatFormat.class.getName());
    private final int fieldLength = 8;
    private String decimalSeparator = "";

    public FixedDecimalFloatFormat()
    {
        super("#.######", new DecimalFormatSymbols(Locale.UK));
        this.setGroupingUsed(false);
        this.decimalSeparator = decimalSeparator + this.getDecimalFormatSymbols().getDecimalSeparator();
    }

    private StringBuffer padResult(StringBuffer output)
    {
        if (output.length() > fieldLength && (output.indexOf(decimalSeparator) == -1 || output.indexOf(decimalSeparator) > fieldLength - 1))
        {
            throw new NumberFormatException("Number length exceeds maximum (" + fieldLength + ") : " + output);
        }

        int charactersToPad = fieldLength - output.length();

        if (charactersToPad < 0)
        {
            String originalNumber = output.toString();
            output.delete(fieldLength, output.length());
            steno.warning("Truncated float from " + originalNumber + " to " + output);
        }

        while (charactersToPad > 0)
        {
            output.insert(0, " ");
            charactersToPad--;
        }

        return output;
    }

    @Override
    public StringBuffer format(double d, StringBuffer sb, FieldPosition fp)
    {
        StringBuffer formattedString = super.format(d, sb, fp);

        return padResult(formattedString);
    }

    @Override
    public StringBuffer format(long l, StringBuffer sb, FieldPosition fp)
    {
        StringBuffer formattedString = super.format(l, sb, fp);

        return padResult(formattedString);
    }

    @Override
    public Number parse(String string) throws ParseException
    {
        String stringToParse = string.trim();

        if (stringToParse.length() > fieldLength)
        {
            throw new NumberFormatException("Number length exceeds maximum (" + fieldLength + ") : " + stringToParse);
        }

        return super.parse(stringToParse);
    }
}
