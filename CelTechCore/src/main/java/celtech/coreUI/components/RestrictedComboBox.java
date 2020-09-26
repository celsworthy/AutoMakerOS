package celtech.coreUI.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;

/**
 *
 * @author George Salter
 */
public class RestrictedComboBox<T> extends ComboBox<T>
{
    private final static Pattern BAD_RESTRICT_PATTERN = Pattern.compile("(.*)\\\\p\\{L\\}\\\\p\\{M\\}\\*\\+?(.*)");
    private final static String STANDARD_ALLOWED_CHARACTERS = "\u0008\u007f"; // Backspace and Delete

    private final StringProperty restrict = new SimpleStringProperty("");
    private final IntegerProperty maxLength = new SimpleIntegerProperty(-1);
    private final BooleanProperty directorySafeName = new SimpleBooleanProperty(false);

    
    public RestrictedComboBox()
    {
        super();
        
        getEditor().textProperty().addListener(new ChangeListener<String>() 
        {
            private boolean ignore;
            
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) 
            {
                if (ignore || newValue == null)
                {
                    return;
                }

                String restrictedString = applyRestriction(newValue);

                if (newValue.length() > maxLength.get()) 
                {
                    restrictedString = restrictedString.substring(0, maxLength.get());
                }
                
                try {
                    if (!restrictedString.isEmpty() && !restrictedString.matches(restrict.get())) 
                    {
                        restrictedString = oldValue;
                    }
                }
                catch (PatternSyntaxException pex) {
                }
                
                ignore = true;
                getEditor().setText(restrictedString);
                ignore = false;
            }
        });
    }
    
    /**
     * Sets a regular expression character class which restricts the user input.
     * E.g. 0-9 only allows numeric values.
     *
     * @param restrict The regular expression.
     */
    public void setRestrict(String restrict)
    {
        // A common value of restrict is: " -_0-9a-zA-Z\p{L}\p{M}*+"
        // but this breaks when in square brackets with the standard characters:
        //
        //     "[ -_0-9a-zA-Z\p{L}\p{M}*+\u0008\u007f]"
        //
        // The " -_" becomes all characters between space and underscore, and
        // \p{L}\p{M}*+, which is intended to match any unicode codepoint
        // followed by modifers, actually match any unicode point
        // or any modifier or asterisk or plus.
        //
        // This attempts to correct the uicode error by using the pattern:
        //
        //   "([ -_0-9a-zA-Z\u0008\u007f]|(\p{L}\p{M}*))+"
        //
        // It doesn't attempt to correct the " -_" error.
        //
        String restrictString;
        Matcher m = BAD_RESTRICT_PATTERN.matcher(restrict);
        if (m.matches()) {
            restrictString = "([" + m.group(1) + m.group(2) + STANDARD_ALLOWED_CHARACTERS + "]|(\\p{L}\\p{M}*))+";
        }
        else
            restrictString = "[" + restrict + STANDARD_ALLOWED_CHARACTERS + "]+";
        this.restrict.set(restrictString);
    }

    /**
     *
     * @return
     */
    public String getRestrict()
    {
        return restrict.get();
    }

    /**
     *
     * @return
     */
    public StringProperty restrictProperty()
    {
        return restrict;
    }
    
    /**
     *
     * @return
     */
    public int getMaxLength()
    {
        return maxLength.get();
    }

    /**
     * Sets the max length of the text field.
     *
     * @param maxLength The max length.
     */
    public void setMaxLength(int maxLength)
    {
        this.maxLength.set(maxLength);
    }

    /**
     *
     * @return
     */
    public IntegerProperty maxLengthProperty()
    {
        return maxLength;
    }
    
    /**
     * @return the directorySafeName
     */
    public boolean getDirectorySafeName()
    {
        return directorySafeName.get();
    }

    /**
     * @param directorySafeName the directorySafeName to set
     */
    public void setDirectorySafeName(boolean directorySafeName)
    {
        this.directorySafeName.set(directorySafeName);
    }

    private String applyRestriction(String text)
    {
        if (directorySafeName.get())
        {
            for (char disallowedChar : "/<>:\"\\|?*".toCharArray())
            {
                char[] toReplace = new char[1];
                toReplace[0] = disallowedChar;
                text = text.replace(new String(toReplace), "");
            }
        }
        return text;
    }
}
