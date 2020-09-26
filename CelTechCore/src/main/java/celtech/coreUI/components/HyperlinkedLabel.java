package celtech.coreUI.components;

import celtech.roboxbase.configuration.BaseConfiguration;
import celtech.roboxbase.configuration.MachineType;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Ian
 */
public class HyperlinkedLabel extends TextFlow
{

    private StringProperty text = new SimpleStringProperty("");
    private static final Pattern hyperlinkPattern = Pattern.compile(
            "\\<a href=\"([^\"]+)\">([^<]+)</a>");
    private Map<String, URI> hyperlinkMap = new HashMap<>();

    public void replaceText(String newText)
    {
        getChildren().clear();
        hyperlinkMap.clear();
        text.set("");
        setTextAlignment(TextAlignment.CENTER);

        Matcher matcher = hyperlinkPattern.matcher(newText);
        int matches = 0;
        int currentIndex = 0;

        while (matcher.find(currentIndex))
        {
            matches++;
            if (matcher.start() > 0)
            {
                String textPortion = newText.substring(currentIndex, matcher.start());
                addPlainText(textPortion);
                currentIndex = matcher.end();
            }
            if (matcher.groupCount() == 2)
            {
                String linkURLString = matcher.group(1);
                String linkText = matcher.group(2);
                try
                {
                    URI linkURI = new URI(linkURLString);
                    hyperlinkMap.put(linkText, linkURI);
                    Hyperlink hyperlink = new Hyperlink();
                    hyperlink.setOnAction((ActionEvent event) ->
                    {
                        Hyperlink newhyperlink = (Hyperlink) event.getSource();
                        final String clickedLinkText = newhyperlink == null ? "" : newhyperlink.
                                getText();
                        if (hyperlinkMap.containsKey(clickedLinkText))
                        {
                            URI linkToVisit = hyperlinkMap.get(clickedLinkText);
                            if (Desktop.isDesktopSupported()
                                    && BaseConfiguration.getMachineType()
                                    != MachineType.LINUX_X86
                                    && BaseConfiguration.getMachineType()
                                    != MachineType.LINUX_X64)
                            {
                                try
                                {
                                    Desktop.getDesktop().browse(linkToVisit);
                                } catch (IOException ex)
                                {
                                    System.err.println("Error when attempting to browse to "
                                            + linkToVisit.
                                                    toString());
                                }
                            } else if (BaseConfiguration.getMachineType() == MachineType.LINUX_X86
                                    || BaseConfiguration.getMachineType() == MachineType.LINUX_X64)
                            {
                                try
                                {
                                    if (Runtime.getRuntime().exec(new String[]
                                    {
                                        "which", "xdg-open"
                                    }).getInputStream().read() != -1)
                                    {
                                        Runtime.getRuntime().exec(new String[]
                                        {
                                            "xdg-open", linkToVisit.toString()
                                        });
                                    }
                                } catch (IOException ex)
                                {
                                    System.err.println("Failed to run linux-specific browser command");
                                }
                            } else
                            {
                                System.err.println(
                                        "Couldn't get Desktop - not able to support hyperlinks");
                            }
                        }
                    });
                    hyperlink.setText(linkText);
                    getChildren().add(hyperlink);
                    currentIndex = matcher.end();
                } catch (URISyntaxException ex)
                {
                    System.err.println("Error attempting to create UI hyperlink from "
                            + linkURLString);
                }
            } else
            {
                System.err.println("Error rendering dialog text: " + newText);
            }
        }
        
        if (matches == 0)
        {
            //We didn't have any hyperlinks here
            currentIndex = newText.length();
            addPlainText(newText);
        }

        if (currentIndex < newText.length()) 
        {
            // Add any final text after the hyperlinks
            String textPortion = newText.substring(currentIndex);
            addPlainText(textPortion);
        }
    }

    private void addPlainText(String textPortion)
    {
        Text plainText = new Text(textPortion);
        plainText.getStyleClass().add("hyperlink-plaintext");
        getChildren().add(plainText);
        text.set(text.get() + textPortion);
    }

    public String getText()
    {
        return text.get();
    }

    public void setText(String text)
    {
        this.text.set(text);
    }

    public StringProperty textProperty()
    {
        return text;
    }
}
