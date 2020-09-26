/*
 * Copyright 2014 CEL UK
 */
package celtech.roboxbase;

import celtech.roboxbase.comms.remote.StringToBase64Encoder;
import celtech.roboxbase.comms.remote.StringToBase64Encoder;
import java.io.UnsupportedEncodingException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author tony
 */
public class StringToBase64EncoderTest
{

    static final String TAJRIBA = "تجربة";
    static final String TAJRIBA_ENCODED = "2KrYrNix2KjYqQ==";
    static final String CHINESE = "会外后 五将";
    static final String CHINESE_ENCODED = "5Lya5aSW5ZCOIOS6lOWwhg==";
    static final String ENGLISH = "This is a long name";
    static final String ENGLISH_ENCODED = "VGhpcyBpcyBhIGxvbmcgbmFtZQ==";
    static final String ENGLISH_LONG = "abcdefghijklmnopqrstuvwxyz";
    static final String ENGLISH_LONG_ENCODED = "YWJjZGVmZ2hpamtsbW5vcHFy";
    static final String ENGLISH_LONG_TRUNCATED = "abcdefghijklmnopqr";
    static final String ARABIC_LONG = "هذه الجملة تحتوي التجربة";
    static final String ARABIC_LONG_ENCODED = "2YfYsNmHINin2YTYrNmF2YQ=";
    static final String ARABIC_LONG_TRUNCATED = "هذه الجمل";

    @Test
    public void testEncodeUTF8StringArabic() throws UnsupportedEncodingException
    {

        String encodedData = StringToBase64Encoder.encode(TAJRIBA);
        assertEquals(TAJRIBA_ENCODED, encodedData);
    }

    @Test
    public void testDecodeUTF8StringArabic() throws UnsupportedEncodingException
    {
        String decodedData = StringToBase64Encoder.decode(TAJRIBA_ENCODED);
        assertEquals(TAJRIBA, decodedData);
    }

    @Test
    public void testEncodeUTF8StringChinese() throws UnsupportedEncodingException
    {

        String encodedData = StringToBase64Encoder.encode(CHINESE);
        assertEquals(CHINESE_ENCODED, encodedData);
    }

    @Test
    public void testDecodeUTF8StringChinese() throws UnsupportedEncodingException
    {
        String decodedData = StringToBase64Encoder.decode(CHINESE_ENCODED);
        assertEquals(CHINESE, decodedData);
    }

    @Test
    public void testEncodeUTF8StringEnglish() throws UnsupportedEncodingException
    {

        String encodedData = StringToBase64Encoder.encode(ENGLISH);
        assertEquals(ENGLISH_ENCODED, encodedData);
    }

    @Test
    public void testDecodeUTF8StringEnglish() throws UnsupportedEncodingException
    {
        String decodedData = StringToBase64Encoder.decode(ENGLISH_ENCODED);
        assertEquals(ENGLISH, decodedData);
    }

    @Test
    public void testEncodeUTF8StringEnglishDoesNotExceedMaxEncodedLength() throws UnsupportedEncodingException
    {
        String encodedData = StringToBase64Encoder.encode(ENGLISH_LONG, 25);
        assertTrue(encodedData.getBytes("US-ASCII").length <= 25);
        assertEquals(ENGLISH_LONG_ENCODED, encodedData);
    }

    @Test
    public void testDecodeUTF8StringEnglishHitMaxEncodedLength() throws UnsupportedEncodingException
    {
        String decodedData = StringToBase64Encoder.decode(ENGLISH_LONG_ENCODED);
        assertEquals(ENGLISH_LONG_TRUNCATED, decodedData);
    }

    @Test
    public void testEncodeUTF8StringArabicDoesNotExceedMaxEncodedLength() throws UnsupportedEncodingException
    {
        String encodedData = StringToBase64Encoder.encode(ARABIC_LONG, 25);
        assertTrue(encodedData.getBytes("US-ASCII").length <= 25);
        assertEquals(ARABIC_LONG_ENCODED, encodedData);
    }

    @Test
    public void testDecodeUTF8StringArabicHitMaxEncodedLength() throws UnsupportedEncodingException
    {
        String decodedData = StringToBase64Encoder.decode(ARABIC_LONG_ENCODED);
        System.out.println(decodedData);
        assertEquals(ARABIC_LONG_TRUNCATED, decodedData);
    }
    
    @Test
    public void testPlainTextIsNotValidEncodedData() throws UnsupportedEncodingException {
        String plainString = "andy's printer";
        assertFalse(StringToBase64Encoder.isEncodedData(plainString));
    }
    
    @Test
    public void testEncodedTextIsValidEncodedData() throws UnsupportedEncodingException {
        assertTrue(StringToBase64Encoder.isEncodedData(TAJRIBA_ENCODED));
    }    

}
