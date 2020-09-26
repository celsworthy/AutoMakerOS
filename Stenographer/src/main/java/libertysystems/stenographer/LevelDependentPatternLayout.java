package libertysystems.stenographer;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author Ian
 */
public class LevelDependentPatternLayout extends Layout
{

    /**
     * Default pattern string for log output. Currently set to the string <b>"%m%n"</b> which just prints the application supplied message.
     */
    public final static String DEFAULT_CONVERSION_PATTERN = "%m%n";

    /**
     * A conversion pattern equivalent to the TTCCCLayout. Current value is <b>%r [%t] %p %c %x - %m%n</b>.
     */
    public final static String TTCC_CONVERSION_PATTERN
        = "%r [%t] %p %c %x - %m%n";

    protected final int BUF_SIZE = 256;
    protected final int MAX_CAPACITY = 1024;

    // output buffer appended to when format() is invoked
    private StringBuffer sbuf = new StringBuffer(BUF_SIZE);

    private String defaultPattern;
    private String detailPattern;
    private String exceptionPattern;

    private PatternConverter defaultPatternConverter;
    private PatternConverter detailPatternConverter;
    private PatternConverter exceptionPatternConverter;

    /**
     * Constructs a PatternLayout using the DEFAULT_LAYOUT_PATTERN.
     *
     * The default pattern just produces the application supplied message.
     */
    public LevelDependentPatternLayout()
    {
        this(DEFAULT_CONVERSION_PATTERN, DEFAULT_CONVERSION_PATTERN, DEFAULT_CONVERSION_PATTERN);
    }

    /**
     * Constructs a PatternLayout using the supplied conversion pattern.
     *
     * @param defaultPattern
     * @param detailPattern
     */
    public LevelDependentPatternLayout(String defaultPattern, String detailPattern, String exceptionPattern)
    {
        this.defaultPattern = defaultPattern;
        this.detailPattern = detailPattern;
        this.exceptionPattern = exceptionPattern;
        defaultPatternConverter = createPatternParser((defaultPattern == null) ? DEFAULT_CONVERSION_PATTERN
            : defaultPattern).parse();
        detailPatternConverter = createPatternParser((detailPattern == null) ? DEFAULT_CONVERSION_PATTERN
            : detailPattern).parse();
        exceptionPatternConverter = createPatternParser((exceptionPattern == null) ? DEFAULT_CONVERSION_PATTERN
            : exceptionPattern).parse();
    }

    /**
     * Set the <b>ConversionPattern</b> option. This is the string which controls formatting and consists of a mix of literal content and conversion specifiers.
     */
    public void setDefaultPattern(String conversionPattern)
    {
        defaultPattern = conversionPattern;
        defaultPatternConverter = createPatternParser(conversionPattern).parse();
    }

    /**
     * Set the <b>ConversionPattern</b> option. This is the string which controls formatting and consists of a mix of literal content and conversion specifiers.
     */
    public void setDetailPattern(String conversionPattern)
    {
        detailPattern = conversionPattern;
        detailPatternConverter = createPatternParser(conversionPattern).parse();
    }

    /**
     * Set the <b>ConversionPattern</b> option. This is the string which controls formatting and consists of a mix of literal content and conversion specifiers.
     */
    public void setExceptionPattern(String conversionPattern)
    {
        exceptionPattern = conversionPattern;
        exceptionPatternConverter = createPatternParser(conversionPattern).parse();
    }

    /**
     * Returns the value of the <b>ConversionPattern</b> option.
     */
    public
        String getDefaultConversionPattern()
    {
        return defaultPattern;
    }

    /**
     * Returns the value of the <b>ConversionPattern</b> option.
     */
    public
        String getDetailConversionPattern()
    {
        return detailPattern;
    }

    /**
     * Does not do anything as options become effective
     */
    public
        void activateOptions()
    {
        // nothing to do.
    }

    /**
     * The PatternLayout does not handle the throwable contained within {@link LoggingEvent LoggingEvents}. Thus, it returns <code>true</code>.
     *
     * @since 0.8.4
     */
    public
        boolean ignoresThrowable()
    {
        return true;
    }

    /**
     * Returns PatternParser used to parse the conversion string. Subclasses may override this to return a subclass of PatternParser which recognize custom conversion characters.
     *
     * @since 0.9.0
     */
    protected PatternParser createPatternParser(String pattern)
    {
        return new PatternParser(pattern);
    }

    /**
     * Produces a formatted string as specified by the conversion pattern.
     */
    public String format(LoggingEvent event)
    {
        // Reset working stringbuffer
        if (sbuf.capacity() > MAX_CAPACITY)
        {
            sbuf = new StringBuffer(BUF_SIZE);
        } else
        {
            sbuf.setLength(0);
        }

        PatternConverter c = defaultPatternConverter;

        if (event.getLevel().equals(Level.FATAL))
        {
            c = exceptionPatternConverter;
        } else if (event.getLevel().equals(Level.DEBUG)
            || event.getLevel().equals(Level.TRACE)
            || event.getLevel().equals(Level.ERROR))
        {
            c = detailPatternConverter;
        }

        while (c != null)
        {
            c.format(sbuf, event);
            c = c.next;
        }

        return sbuf.toString();
    }
}
