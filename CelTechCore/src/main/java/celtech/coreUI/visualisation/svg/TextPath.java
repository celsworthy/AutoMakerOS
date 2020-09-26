package celtech.coreUI.visualisation.svg;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.controlsfx.control.PopOver;

/**
 *
 * @author Ian
 */
public class TextPath extends SVGPath implements PrintableShape
{

    private String textToDisplay = "Text";
    private GraphicsEnvironment ge;
    private Font fontInUse;
    private FontRenderContext frc;
    private final TextPath thisTextPath;
    private final TextField textEditor = new TextField();
    private final ComboBox<String> fontChooser = new ComboBox();
    private final ComboBox<Integer> fontSizeChooser = new ComboBox();
    private final ComboBox<String> fontStyleChooser = new ComboBox();

    public TextPath()
    {
        thisTextPath = this;
        ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        VBox editBox = new VBox();
        editBox.setSpacing(5);
        
        textEditor.setText(textToDisplay);
        textEditor.textProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1)
            {
                textToDisplay = t1;
                updateTextPath();
            }
        });

        String[] fonts = ge.getAvailableFontFamilyNames();
        fontChooser.setItems(FXCollections.observableArrayList(fonts));
        fontChooser.getSelectionModel().selectFirst();
        fontChooser.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>()
        {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1)
            {
                updateFont();
            }
        });
        
        String[] fontStyles =
        {
            "Plain", "Bold", "Italic"
        };
        fontStyleChooser.setItems(FXCollections.observableArrayList(fontStyles));
        fontStyleChooser.getSelectionModel().selectFirst();
        fontStyleChooser.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                updateFont();
            }
        });
        
        ObservableList<Integer> fontSizes = FXCollections.observableArrayList();
        fontSizes.addAll(18, 20, 24, 28, 33, 36, 40, 44, 48, 52, 56);
        fontSizeChooser.setItems(fontSizes);
        fontSizeChooser.getSelectionModel().selectFirst();
        fontSizeChooser.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
            {
                updateFont();
            }
        });
        
        editBox.getChildren().addAll(textEditor, fontChooser, fontStyleChooser, fontSizeChooser);

        PopOver popOver = new PopOver(editBox);
        popOver.setTitle("Text Editor");

        updateFont();

        setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t)
            {
                if (t.getClickCount() > 1)
                {
                    popOver.show(thisTextPath);
                }
            }
        });
    }

    private void updateFont()
    {
        Font fontWeWishToUse = new Font(fontChooser.getSelectionModel().getSelectedItem(),
                fontStyleChooser.getSelectionModel().getSelectedIndex(),
                fontSizeChooser.getSelectionModel().getSelectedItem());
        fontInUse = fontWeWishToUse;
        frc = new FontRenderContext(fontInUse.getTransform(), true, true);
        updateTextPath();
    }

    private void updateTextPath()
    {
        GlyphVector v = fontInUse.createGlyphVector(frc, textToDisplay);
        Shape s = v.getOutline();
        PathIterator pathIterator = s.getPathIterator(null);

        StringBuilder svgContent = new StringBuilder();

        float[] coords = new float[6];

        while (!pathIterator.isDone())
        {
            int pathType = pathIterator.currentSegment(coords);
            switch (pathType)
            {
                case PathIterator.SEG_CLOSE:
//                    steno.info("Got a close");
                    svgContent.append("z ");
                    break;
                case PathIterator.SEG_CUBICTO:
//                    steno.info("Got a cubic to "
//                            + "x1:" + coords[0]
//                            + "y1:" + coords[1]
//                            + "x2:" + coords[2]
//                            + "y2:" + coords[3]
//                            + "x3:" + coords[4]
//                            + "y3:" + coords[5]);
                    svgContent.append("C");
                    svgContent.append(coords[0]);
                    svgContent.append(" ");
                    svgContent.append(coords[1]);
                    svgContent.append(" ");
                    svgContent.append(coords[2]);
                    svgContent.append(" ");
                    svgContent.append(coords[3]);
                    svgContent.append(" ");
                    svgContent.append(coords[4]);
                    svgContent.append(" ");
                    svgContent.append(coords[5]);
                    svgContent.append(" ");
                    break;
                case PathIterator.SEG_LINETO:
//                    steno.info("Got a line to "
//                            + "x1:" + coords[0]
//                            + "y1:" + coords[1]);
                    svgContent.append("L");
                    svgContent.append(coords[0]);
                    svgContent.append(" ");
                    svgContent.append(coords[1]);
                    svgContent.append(" ");
                    break;
                case PathIterator.SEG_MOVETO:
//                    steno.info("Got a move to "
//                            + "x1:" + coords[0]
//                            + "y1:" + coords[1]);
                    svgContent.append("M");
                    svgContent.append(coords[0]);
                    svgContent.append(" ");
                    svgContent.append(coords[1]);
                    svgContent.append(" ");
                    break;
                case PathIterator.SEG_QUADTO:
//                    steno.info("Got a quad to "
//                            + "x1:" + coords[0]
//                            + "y1:" + coords[1]
//                            + "x2:" + coords[2]
//                            + "y2:" + coords[3]);
                    svgContent.append("Q");
                    svgContent.append(coords[0]);
                    svgContent.append(" ");
                    svgContent.append(coords[1]);
                    svgContent.append(" ");
                    svgContent.append(coords[2]);
                    svgContent.append(" ");
                    svgContent.append(coords[3]);
                    svgContent.append(" ");
                    break;
            }
            pathIterator.next();
        }

        this.setContent(svgContent.toString());
    }

    public void setText(String newText)
    {
        textToDisplay = newText;
        updateTextPath();
    }

    @Override
    public void relativeTranslate(double x, double y)
    {
        setTranslateX(getTranslateX() + x);
        setTranslateY(getTranslateY() + y);
    }

    @Override
    public String getSVGPathContent()
    {
        return getContent();
    }
}
