/*
 * Copyright 2014 CEL UK
 */

package celtech.roboxbase.comms.remote;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * StringToBase64Encoder is a text to binary encoder that takes a String and returns
 * a (usually longer) string that only contains characters with byte values
 * less than 0x7F, and that can be decoded back to the original String.
 * Current implementation uses the JAXB variant of MIME64 encoding/decoding,
 * which reduces each byte to a maximum of 6 bits.
 * 
 * @author tony
 */
public class StringToBase64Encoder
{

    /**
     * Encode the given string as a list of characters of byte value less than 07F.
     * @param plainString the String to be encoded
     * @return the encoded String
     */
    public static String encode(String plainString) throws UnsupportedEncodingException
    {
        return Base64.getMimeEncoder().encodeToString(plainString.getBytes("UTF-8"));
    }

    /**
     * Decode the given encoded string and return as a UTF8 String.
     * @param encodedString
     * @return the decoded String
     * @throws UnsupportedEncodingException 
     */
    public static String decode(String encodedString) throws UnsupportedEncodingException
    {
        byte[] decodedData = Base64.getMimeDecoder().decode(encodedString);
        return new String(decodedData, "UTF-8");
    }

    /**
     * Return the encoded string of the given string, also ensuring that the
     * length of the returned string, when converted to a US-ASCII byte array,
     * is less than the given number. If the encoded data is too long then keep 
     * removing the last character from the string to be encoded until the
     * encoded data does not exceed the given length.
     * 
     * @param plainString
     * @param maxEncodedLength
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String encode(String plainString, int maxEncodedLength) throws UnsupportedEncodingException
    {
        while (encode(plainString).getBytes("US-ASCII").length > maxEncodedLength) {
            // remove last character and try again
            plainString = plainString.substring(0, plainString.length() - 1);
        }
        return encode(plainString);
    }

    /**
     * Test if testString is valid base64 encoded data.
     * 
     * @param testString
     * @return 
     * @throws java.io.UnsupportedEncodingException 
     */
    public static boolean isEncodedData(String testString) throws UnsupportedEncodingException
    {
        // Test by decoding the data and reencoding, and seeing if the result
        // is the same as the incoming data
        try {
        String decodedData = decode(testString);
        String encodedData = encode(decodedData);
        return encodedData.equals(testString);
        // I've seen ArrayIndex exceptions here so keep the handler general
        } catch (Exception ex) {
            return false;
        }
    }
    
    
    
}
