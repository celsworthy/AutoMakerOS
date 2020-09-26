package celtech.coreUI.components;

import celtech.FXTest;
import celtech.JavaFXConfiguredTest;
import javafx.scene.control.Hyperlink;
import javafx.scene.text.Text;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.experimental.categories.Category;

/**
 *
 * @author Ian
 */
@Category(FXTest.class)
public class HyperlinkedLabelTest extends JavaFXConfiguredTest
{

    @Before
    @Override
    public void setUp()
    {
    }

    @Test
    public void testReplaceText_plaintextOnly()
    {
        String newText = "Some plain text";
        HyperlinkedLabel instance = new HyperlinkedLabel();
        instance.replaceText(newText);

        assertEquals(1, instance.getChildren().size());
        assertTrue(instance.getChildren().get(0) instanceof Text);
        assertThat(((Text) instance.getChildren().get(0)).getText(), is(newText));
    }

    @Test
    public void testReplaceText_plaintextAndHyperlink()
    {
        String newText = "Robox firmware update <a href=\"https://robox.freshdesk.com/solution/categories/1000090870/folders/1000214277/articles/1000180224-the-filament-isn-t-moving-as-expected\">Other article</a>";
        String expectedTextContent = "Robox firmware update ";
        String expectedHyperlinkContent = "Other article";
        HyperlinkedLabel instance = new HyperlinkedLabel();
        instance.replaceText(newText);

        assertEquals(2, instance.getChildren().size());
        assertTrue(instance.getChildren().get(0) instanceof Text);
        assertTrue(instance.getChildren().get(1) instanceof Hyperlink);
        assertThat(((Text) instance.getChildren().get(0)).getText(), is(expectedTextContent));
        assertThat(((Hyperlink) instance.getChildren().get(1)).getText(), is(expectedHyperlinkContent));
    }

    @Test
    public void testReplaceText_plaintextAndTwoHyperlinks()
    {
        String newText = "Robox firmware update <a href=\"https://robox.freshdesk.com/support/home\">Robox solutions</a>more text<a href=\"https://robox.freshdesk.com/solution/categories/1000090870/folders/1000214277/articles/1000180224-the-filament-isn-t-moving-as-expected\">Other article</a>";

        String expectedTextContent1 = "Robox firmware update ";
        String expectedTextContent2 = "more text";
        String expectedHyperlinkContent1 = "Robox solutions";
        String expectedHyperlinkContent2 = "Other article";
        HyperlinkedLabel instance = new HyperlinkedLabel();
        instance.replaceText(newText);

        assertEquals(4, instance.getChildren().size());
        assertTrue(instance.getChildren().get(0) instanceof Text);
        assertTrue(instance.getChildren().get(1) instanceof Hyperlink);
        assertTrue(instance.getChildren().get(2) instanceof Text);
        assertTrue(instance.getChildren().get(3) instanceof Hyperlink);
        assertThat(((Text) instance.getChildren().get(0)).getText(), is(expectedTextContent1));
        assertThat(((Hyperlink) instance.getChildren().get(1)).getText(), is(expectedHyperlinkContent1));
        assertThat(((Text) instance.getChildren().get(2)).getText(), is(expectedTextContent2));
        assertThat(((Hyperlink) instance.getChildren().get(3)).getText(), is(expectedHyperlinkContent2));
    }

    @Test
    public void testReplaceText_HyperlinkOnly()
    {
        String newText = "<a href=\"https://robox.freshdesk.com/solution/categories/1000090870/folders/1000214277/articles/1000180224-the-filament-isn-t-moving-as-expected\">Other article</a>";
        String expectedHyperlinkContent = "Other article";
        HyperlinkedLabel instance = new HyperlinkedLabel();
        instance.replaceText(newText);

        assertEquals(1, instance.getChildren().size());
        assertTrue(instance.getChildren().get(0) instanceof Hyperlink);
        assertThat(((Hyperlink) instance.getChildren().get(0)).getText(), is(
                   expectedHyperlinkContent));
    }
    
    @Test
    public void testReplaceText_HyperlinkInMiddleOfText()
    {
        final String NEW_TEXT = "PRECEDING TEXT <a href=\"https://example.web.page/home\">LINK</a> FOLLOWING TEXT";
        
        final String EXPECTED_TEXT_CONTENT_1 = "PRECEDING TEXT ";
        final String EXPECTED_TEXT_CONTENT_2 = " FOLLOWING TEXT";
        final String EXPECTED_HYPERLINK_CONTENT = "LINK";
        
        HyperlinkedLabel hyperLinkLabel = new HyperlinkedLabel();
        hyperLinkLabel.replaceText(NEW_TEXT);
        
        assertEquals(3, hyperLinkLabel.getChildren().size());
        assertTrue("The first element of the TextFlow should be plain text", hyperLinkLabel.getChildren().get(0) instanceof Text);
        assertTrue("The second element of the TextFlow should be a hyperlink", hyperLinkLabel.getChildren().get(1) instanceof Hyperlink);
        assertTrue("The third element of the TextFlow should be plain text", hyperLinkLabel.getChildren().get(2) instanceof Text);
        
        assertThat(((Text) hyperLinkLabel.getChildren().get(0)).getText(), is(EXPECTED_TEXT_CONTENT_1));
        assertThat(((Hyperlink) hyperLinkLabel.getChildren().get(1)).getText(), is(EXPECTED_HYPERLINK_CONTENT));
        assertThat(((Text) hyperLinkLabel.getChildren().get(2)).getText(), is(EXPECTED_TEXT_CONTENT_2));
    }
}
