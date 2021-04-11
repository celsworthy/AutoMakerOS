package celuk.gcodeviewer.gui;

import celuk.gcodeviewer.engine.LayerDetails;
import celuk.gcodeviewer.engine.RenderParameters;
import static celuk.gcodeviewer.engine.renderers.GUIRenderer.GUI_GCODE_PANEL_X;
import static celuk.gcodeviewer.gui.GCVControlPanel.GUI_CONTROL_PANEL_WIDTH;
import celuk.language.I18n;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import org.joml.Vector3f;

public class GCVGCodePanel extends GCVPanel {

    public static final int GUI_GCODE_PANEL_WIDTH = 550;
    public static final int GUI_GCODE_MIN_PANEL_WIDTH = 200;
    public static final int GUI_GCODE_MIN_PANEL_HEIGHT = 200;
    public static final int GUI_GCODE_PANEL_SIDE_WIDTH = 10;
    public static final int GUI_GCODE_RESIZE_BAR_WIDTH = 5;
    public static final int GUI_GCODE_PANEL_ROW_HEIGHT = 35;
    public static final int GUI_GCODE_PANEL_BUTTON_WIDTH = 35;
    public static final int GUI_GCODE_PANEL_CHECKBOX_WIDTH = 180;
    public static final int GUI_GCODE_PANEL_LINE_HEIGHT = 25;
    public static final int GUI_GCODE_PANEL_CHAR_WIDTH = 14;
    public static final int GUI_GCODE_PANEL_LINE_WIDTH = 80 * GUI_GCODE_PANEL_CHAR_WIDTH;
    public static final int GUI_GCODE_PANEL_CLOSED_HEIGHT = 30;
    public static final int GUI_GCODE_PANEL_TOP_HEIGHT = 82;
    public static final int GUI_GCODE_PANEL_SCROLL_BAR_HEIGHT = 30;
    public static final int GUI_GCODE_PANEL_FIDDLE_FACTOR = 10;
    public static final int GUI_GCODE_PANEL_MIN_SCROLLBAR_SIZE = 25;
    
    public static final int GUI_GCODE_PANEL_MAX_WIDTH = GUI_GCODE_PANEL_LINE_WIDTH
                                                            + GUI_GCODE_RESIZE_BAR_WIDTH
                                                            + GUI_GCODE_PANEL_SIDE_WIDTH
                                                            + 30; // Fiddle factor to make it work.
    private static final String LINE_NUMBER_POSTFIX = " :";

    private String firstSelectedMsg = "gcodePanel.firstSelected";
    private String lastSelectedMsg = "gcodePanel.lastSelected";
    private String goToLineMsg = "gcodePanel.goToLine";
    private String goToFirstSelectedMsg = "gcodePanel.goToFirstSelected";
    private String goToLastSelectedMsg = "gcodePanel.goToLastSelected";
    private String showLineNumbersMsg = "gcodePanel.showLineNumbers";

    private boolean draggingResizeBar = false;
    private float resizeBarAnchorX = 0.0f;
    private float expandedPanelWidth = GUI_GCODE_PANEL_WIDTH;
    
    private List<String> lines = null;
    private Map<Integer, LayerDetails> layerMap = null;
    private List<Integer> layerList = null;
    private int totalNumberOfLines = 0;
    private int offsetToFirstLayer = 0;
    private int lineOffset = 0;
    private float totalColumnWidth = GUI_GCODE_PANEL_LINE_WIDTH;
    private float columnOffset = 0;
    
    boolean dragging = false;
    int dragStartLine = 0;

    private float lineHeight = GUI_GCODE_PANEL_LINE_HEIGHT;

    private boolean goToLineBlank = true;
    private int goToLine = 0;
    private int topLine = 0;
    private int lineNumberSpace = GUI_GCODE_PANEL_CHAR_WIDTH;
    private boolean topLineEdited = false;
    private boolean showLineNumbers = false;
    private final NkPluginFilter numberFilter;
    private boolean linesReloaded = false;
    
    public GCVGCodePanel() {
        numberFilter = NkPluginFilter.create(Nuklear::nnk_filter_float);
    }
    
    public void loadMessages() {
        firstSelectedMsg = I18n.t(firstSelectedMsg);
        lastSelectedMsg = I18n.t(lastSelectedMsg);
        goToLineMsg = I18n.t(goToLineMsg);
        goToFirstSelectedMsg = I18n.t(goToFirstSelectedMsg);
        goToLastSelectedMsg = I18n.t(goToLastSelectedMsg);
        showLineNumbersMsg = I18n.t(showLineNumbersMsg);
    }
    
    public void setLines(List<String> lines) {
        linesReloaded = true;
        this.lines = lines;
    }

    public void setLayerMap(Map<Integer, LayerDetails> layerMap) {
        this.layerMap = layerMap;
        linesReloaded = true;
        if (layerMap != null) {
            this.layerList = new ArrayList( this.layerMap.keySet());
            Collections.sort(this.layerList);
            calculateLayerOffsets(true);
        }
        else
            layerList = null;
    }
    
    private void calculateLayerOffsets(boolean closeAllLayers) {
        // This can be done by a reduce, but looks better
        // using an old-school for loop because it has the
        // side effect of setting the offsets in the layer
        // details.
        int lineCount = 0;
        if (layerList == null || layerList.size() == 0) {
            offsetToFirstLayer = lines.size();
            lineCount = lines.size();
        }
        else
        {
            lineCount = 0;
            for (int i = 0; i < layerList.size(); ++i) {
                int layer = layerList.get(i);
                LayerDetails details = layerMap.get(layer);
                if (i == 0) {
                    offsetToFirstLayer = details.getStartLine();
                    lineCount = offsetToFirstLayer;
                }
                details.setStartOffset(lineCount);
                if (closeAllLayers)
                    details.setLayerOpen(false);
                if (details.getLayerOpen())
                    lineCount += details.getNumberOfLines();
                else
                    ++lineCount;

                details.setEndOffset(lineCount);
            }
        }
        totalNumberOfLines = lineCount;
    }

    private int lineToOffset(int line, boolean autoExpand) {
        int offset = line;
        if (line >= offsetToFirstLayer && layerList != null) {
            for (int i = 0; i < layerList.size(); ++i) {
                int layer = layerList.get(i);
                LayerDetails details = layerMap.get(layer);
                if (line >= details.getStartLine() && line < details.getEndLine()) {
                    if (!autoExpand || line == details.getStartLine())
                        offset = details.getStartOffset();
                    else {
                        if (!details.getLayerOpen()) {
                            details.setLayerOpen(true);
                            calculateLayerOffsets(false);
                        }
                        offset = details.getStartOffset() + line - details.getStartLine();
                    }
                    break;
                }
            }
        }
        
        return offset;
    }
    
    public boolean getShowLineNumbers() {
        return showLineNumbers;
    }

    public void setShowLineNumbers(boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers;
    }

    public void layout(NkContext ctx, int x, int y, RenderParameters renderParameters) {
        // This is a hand-built copy of the nuklear list view, which spoofs a
        // nuklear scrolled group into thinking it contains a much larger list than it
        // actually does. The list view is not used because there is no way to scroll
        // it to a specified point. This automatically scrolls to the selected
        // line when it changes.
        
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            float windowPaddingX = ctx.style().window().padding().x();
            float windowPaddingY = ctx.style().window().padding().y();
            float groupPaddingX = ctx.style().window().group_padding().x();
            lineHeight = GUI_GCODE_PANEL_LINE_HEIGHT + ctx.style().window().spacing().y();
            if (panelExpanded) {
                // It seems that we can get thro
                if (renderParameters.getWindowWidth() > 0) {
                    // It seems this called at least once when the window has been iconised so is
                    // zero width, which caused the panel to shrink.
                    float maxWidth = renderParameters.getWindowWidth() - GUI_CONTROL_PANEL_WIDTH - 2.0f * GUI_GCODE_PANEL_X;
                    if (maxWidth > GUI_GCODE_PANEL_MAX_WIDTH)
                        maxWidth = GUI_GCODE_PANEL_MAX_WIDTH;
                    if (maxWidth < GUI_GCODE_MIN_PANEL_WIDTH)
                        maxWidth = GUI_GCODE_MIN_PANEL_WIDTH;
                    if (expandedPanelWidth < GUI_GCODE_MIN_PANEL_WIDTH || expandedPanelWidth > maxWidth)
                        expandedPanelWidth = maxWidth;
                }
                panelWidth = expandedPanelWidth;
                panelHeight = renderParameters.getWindowHeight() - 2 * y;
                if (panelHeight < GUI_GCODE_MIN_PANEL_HEIGHT)
                    panelHeight = GUI_GCODE_MIN_PANEL_HEIGHT;
            }
            else {
                panelWidth = GUI_GCODE_PANEL_SIDE_WIDTH + 4.0f * windowPaddingX;
                panelHeight = GUI_GCODE_PANEL_CLOSED_HEIGHT;
            }
            panelX = renderParameters.getWindowWidth() - panelWidth - x;
            panelY = y;
            nk_rect(panelX, panelY, panelWidth, panelHeight, rect);
                
            if (nk_begin(ctx, "GCode Panel", rect, NK_WINDOW_NO_SCROLLBAR)) {
                if (linesReloaded) {
                    // If the lines were reloaded, then the scroll needs to be reset to the
                    // start, otherwise it might crash trying to display non-existent lines.
                    linesReloaded = false;
                    lineOffset = 0;
                    columnOffset = 0.0f;
                }

                if (panelExpanded) {                    
                    float w = rect.w() - 4.0f * windowPaddingX - GUI_GCODE_PANEL_SIDE_WIDTH - GUI_GCODE_RESIZE_BAR_WIDTH;
                    nk_layout_row_begin(ctx, NK_STATIC, rect.h() - 2.0f * windowPaddingY, 3);
                    nk_layout_row_push(ctx, GUI_GCODE_PANEL_SIDE_WIDTH);
                    layoutSideButton(ctx, renderParameters);
                    nk_layout_row_push(ctx, GUI_GCODE_RESIZE_BAR_WIDTH);
                    layoutResizeBar(ctx, renderParameters);

                    nk_layout_row_push(ctx, w);
                    if (nk_group_begin(ctx, "GCodeGroup", NK_WINDOW_NO_SCROLLBAR)) {
                        w -= 4.0f * groupPaddingX + GUI_GCODE_PANEL_FIDDLE_FACTOR;
                        layoutTopRow(ctx, w, renderParameters);
                        layoutScrolled(ctx, w, renderParameters);
                        nk_group_end(ctx);
                    }
                }
                else {
                    nk_layout_row_begin(ctx, NK_STATIC, GUI_GCODE_PANEL_CLOSED_HEIGHT - 2.0f * windowPaddingY, 1);
                    nk_layout_row_push(ctx, GUI_GCODE_PANEL_SIDE_WIDTH);
                    layoutSideButton(ctx, renderParameters); 
                }
            }
            nk_end(ctx);
        }
    }
    private void layoutTopRow(NkContext ctx, float width, RenderParameters renderParameters) {
        try (MemoryStack stack = stackPush()) {
            nk_layout_row_begin(ctx, NK_STATIC, GUI_GCODE_PANEL_ROW_HEIGHT, 7); // 7 = 5 widgets + 2 dummy buttons (see layoutProperty method).
            int step = 1;
            if (renderParameters.getNumberOfLines() < 1000)
                step = 10;
            else if (renderParameters.getNumberOfLines() < 10000)
                step = 50;
            else if (renderParameters.getNumberOfLines() < 100000)
                step = 100;
            else if (renderParameters.getNumberOfLines() < 1000000)
                step = 500;
            else
                step = 1000;
            float pWidth = 0.5f * (width - 3.0f * GUI_GCODE_PANEL_BUTTON_WIDTH - 5.0f * ctx.style().window().group_padding().x());
            pWidth -= 9.0f; // Fiddle factor to allow for dummy buttons.
            layoutProperty(ctx,
                           firstSelectedMsg,
                           pWidth,
                           0,
                           renderParameters.getLastSelectedLine(),
                           1,
                           step,
                           renderParameters.getFirstSelectedLine(),
                           renderParameters::setFirstSelectedLine);
            nk_layout_row_push(ctx, GUI_GCODE_PANEL_BUTTON_WIDTH);
            if (nk_button_label(ctx, goToFirstSelectedMsg) &&
                renderParameters.getFirstSelectedLine() != renderParameters.getLastSelectedLine()) {
                lineOffset = lineToOffset(renderParameters.getFirstSelectedLine(), true);
            }
            layoutProperty(ctx,
                           lastSelectedMsg,
                           pWidth,
                           renderParameters.getFirstSelectedLine(),
                           renderParameters.getNumberOfLines(),
                           1,
                           step,
                           renderParameters.getLastSelectedLine(),
                           renderParameters::setLastSelectedLine);
            nk_layout_row_push(ctx, GUI_GCODE_PANEL_BUTTON_WIDTH);
            if (nk_button_label(ctx, goToLastSelectedMsg) &&
                renderParameters.getFirstSelectedLine() != renderParameters.getLastSelectedLine()) {
                lineOffset = lineToOffset(renderParameters.getLastSelectedLine() - 1, true);
            }
            nk_layout_row_push(ctx, GUI_GCODE_PANEL_BUTTON_WIDTH);
            if (nk_button_label(ctx, "*"))
                renderParameters.clearSelectedLines();
            nk_layout_row_end(ctx);
            
            nk_layout_row_begin(ctx, NK_STATIC, GUI_GCODE_PANEL_ROW_HEIGHT, 3);
            float eWidth = (width - GUI_GCODE_PANEL_BUTTON_WIDTH - GUI_GCODE_PANEL_CHECKBOX_WIDTH - 3.0f * ctx.style().window().group_padding().x());
            nk_layout_row_push(ctx, eWidth);
            ByteBuffer editBuffer = stack.calloc(256);
            String lineString;
            if (topLineEdited)
                lineString = (goToLineBlank ? "" : Integer.toString(goToLine));
            else
                lineString = Integer.toString(topLine);
            int length = memASCII(lineString, false, editBuffer);
            IntBuffer len = stack.ints(length);
            int flags = nk_edit_string(ctx, NK_EDIT_SIMPLE, editBuffer, len, 255, numberFilter);
            topLineEdited = ((flags & NK_EDIT_ACTIVE) == NK_EDIT_ACTIVE);
            if (topLineEdited) {
                try {
                    goToLineBlank = (len.get(0) == 0);
                    if (goToLineBlank)
                        goToLine = 0;
                    else {
                        int l = Integer.parseInt(memASCII(editBuffer, len.get(0)));
                        if (l >= 0 && l < renderParameters.getNumberOfLines())
                            goToLine = l;
                    }
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }
            }
            nk_layout_row_push(ctx, GUI_GCODE_PANEL_BUTTON_WIDTH);
            if (nk_button_label(ctx, goToLineMsg) && !goToLineBlank && lines != null) {
                lineOffset = lineToOffset(goToLine, true);
            }
            IntBuffer checkBuffer = stack.mallocInt(1);
            checkBuffer.put(0, (showLineNumbers ? 1 : 0));
            nk_layout_row_push(ctx, GUI_GCODE_PANEL_CHECKBOX_WIDTH);
            nk_checkbox_label(ctx, showLineNumbersMsg, checkBuffer);
            nk_layout_row_end(ctx);
            showLineNumbers = (checkBuffer.get(0) != 0);
        }
    }
    
    private void layoutProperty(NkContext ctx,
                                String label,
                                float width,
                                int minValue,
                                int maxValue,
                                int step,
                                int incPerPixel,
                                int currentValue,
                                Consumer<Integer> setValue) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer valueBuffer = stack.mallocInt(1);
            valueBuffer.put(0, currentValue);
            // Nuklear properties seem to show as active of hovered if the last created button is
            // active or hovered. To prevent this, a zero width button is created before the property.
            nk_layout_row_push(ctx, 0);
            nk_button_label(ctx, "");
            nk_layout_row_push(ctx, width);
            if (nk_input_is_key_down(ctx.input(), NK_KEY_CTRL))
                nk_button_set_behavior(ctx, NK_BUTTON_REPEATER);
            if (nk_input_is_key_down(ctx.input(), NK_KEY_SHIFT))
                step = 1;
            nk_property_int(ctx, "#"+label, minValue, valueBuffer, maxValue, step, incPerPixel);
            nk_button_set_behavior(ctx, NK_BUTTON_DEFAULT);
            setValue.accept(valueBuffer.get(0));
        }
    }

    private int layoutLayers(NkContext ctx, int nViewLines, RenderParameters renderParameters) {
        int nLinesShown = 0;
        try (MemoryStack stack = stackPush()) {
            float viewHeight = nk_window_get_height(ctx);
            
            if (lines != null && layerList != null)
            {
                NkColor textColour = NkColor.mallocStack(stack);
                textColour.set((byte)255, (byte)255, (byte)255, (byte)255);
                NkColor selectedTextColour = NkColor.mallocStack(stack);
                Vector3f selectedColour = renderParameters.getSelectColour();
                selectedTextColour.set((byte)(255 * selectedColour.x()), (byte)(255 * selectedColour.y()), (byte)(255 * selectedColour.z()), (byte)255);

                NkColor originalTextColour = NkColor.mallocStack(stack);
                originalTextColour.set(ctx.style().text().color());
                // Find the layer in which the view begins.
                LayerDetails currentDetails = null;
                int currentLayerIndex = 0;
                if (lineOffset >= offsetToFirstLayer)
                {
                    for (int i = 0; currentDetails == null && i < layerList.size(); ++i) {
                         LayerDetails details = layerMap.get(layerList.get(i));
                         if (lineOffset >= details.getStartOffset() &&
                             lineOffset < details.getEndOffset()) {
                             currentDetails = details;
                             currentLayerIndex = i;
                         }
                    }
                }

                nk_layout_space_begin(ctx, NK_STATIC, viewHeight, 10000);

                int labelSpace = (showLineNumbers ? lineNumberSpace : GUI_GCODE_PANEL_CHAR_WIDTH);
                int lineIndex = 0;
                NkRect currentBounds = NkRect.mallocStack(stack);
                currentBounds.set(-columnOffset, 0.0f, 0.0f, lineHeight);
                for (int i = 0; i < nViewLines; ++i) {
                    int index = lineOffset + i;
                    if (index == offsetToFirstLayer &&
                        currentDetails == null &&
                        layerList != null &&
                        layerList.size() > 0) {
                        currentLayerIndex = 0;
                        currentDetails = layerMap.get(layerList.get(currentLayerIndex));
                    }
                    else {
                        if (index >= offsetToFirstLayer &&
                           (currentDetails == null || index >= currentDetails.getEndOffset())) {
                            ++currentLayerIndex;
                            if (currentLayerIndex >= layerList.size())
                                break;
                            currentDetails = layerMap.get(layerList.get(currentLayerIndex));
                        }
                    }

                    boolean mouseIsClicked = false;
                    currentBounds.w(labelSpace);
                    nk_layout_space_push(ctx, currentBounds);
                    if (currentDetails != null && index == currentDetails.getStartOffset())
                    {
                        // Draw a tree entry
                        if (nk_widget_is_mouse_clicked(ctx, NK_BUTTON_LEFT)) {
                            currentDetails.setLayerOpen(!currentDetails.getLayerOpen());
                            calculateLayerOffsets(false);
                        }
                        ctx.style().text().color(textColour);
                        lineIndex = currentDetails.getStartLine();
                        if (lineIndex >= renderParameters.getFirstSelectedLine() && lineIndex < renderParameters.getLastSelectedLine())
                            ctx.style().text().color(selectedTextColour);
                        else
                            ctx.style().text().color(textColour);
                        String prefix;
                        if (currentDetails.getLayerOpen())
                            prefix = "V ";
                        else
                            prefix = "> ";
                        if (showLineNumbers)
                            prefix += Integer.toString(lineIndex) + LINE_NUMBER_POSTFIX;
                        nk_label(ctx, prefix, NK_TEXT_LEFT);
                        currentBounds.x(currentBounds.x() + currentBounds.w());
                        currentBounds.w(GUI_GCODE_PANEL_LINE_WIDTH - labelSpace);
                        nk_layout_space_push(ctx, currentBounds);
                        if (nk_widget_is_mouse_clicked(ctx, NK_BUTTON_LEFT)) {
                            mouseIsClicked = true;
                        }
                        nk_label(ctx, lines.get(lineIndex), NK_TEXT_LEFT);
                    }
                    else
                    {
                        // Draw a line
                        if (index < offsetToFirstLayer)
                            lineIndex = index;
                        else
                            lineIndex = currentDetails.getStartLine() + index - currentDetails.getStartOffset();
                        if (nk_widget_is_mouse_clicked(ctx, NK_BUTTON_LEFT)) {
                            mouseIsClicked = true;
                        }
                        if (lineIndex >= renderParameters.getFirstSelectedLine() && lineIndex < renderParameters.getLastSelectedLine())
                            ctx.style().text().color(selectedTextColour);
                        else
                            ctx.style().text().color(textColour);
                        String lineLabel = "   ";
                        if (showLineNumbers)
                            lineLabel += Integer.toString(lineIndex) + LINE_NUMBER_POSTFIX;
                        nk_label(ctx, lineLabel, NK_TEXT_LEFT);
                        currentBounds.x(currentBounds.x() + currentBounds.w());
                        currentBounds.w(GUI_GCODE_PANEL_LINE_WIDTH - labelSpace);
                        nk_layout_space_push(ctx, currentBounds);
                        if (nk_widget_is_mouse_clicked(ctx, NK_BUTTON_LEFT)) {
                            mouseIsClicked = true;
                        }
                        nk_label(ctx, lines.get(lineIndex), NK_TEXT_LEFT);
                    }
                    currentBounds.x(-columnOffset);
                    currentBounds.y(currentBounds.y() + lineHeight);
                    
                    if (mouseIsClicked) {
                        // Annoyingly, mouse clicks are registered when they
                        // are in the scrolled area or in the scroll bar. The following
                        // code checks the mouse click is in the scrolled area.
                        NkRect areaBounds = NkRect.mallocStack(stack);
                        nk_layout_space_bounds(ctx, areaBounds);
                        if (nk_input_has_mouse_click_in_rect(ctx.input(), NK_BUTTON_LEFT, areaBounds)) {
                            if (nk_input_is_key_down(ctx.input(), NK_KEY_SHIFT) &&
                                renderParameters.getFirstSelectedLine() != renderParameters.getLastSelectedLine()) {
                                // Extend existing selection.
                                if (lineIndex >= offsetToFirstLayer && lineIndex == currentDetails.getStartLine() && !currentDetails.getLayerOpen()) {
                                    // Extend over the whole layer.
                                    if (lineIndex < renderParameters.getFirstSelectedLine())
                                        renderParameters.setFirstSelectedLine(lineIndex);
                                    else if (currentDetails.getEndLine() > renderParameters.getLastSelectedLine())
                                        renderParameters.setLastSelectedLine(currentDetails.getEndLine());
                                }
                                else
                                {
                                    if (lineIndex < renderParameters.getFirstSelectedLine())
                                        renderParameters.setFirstSelectedLine(lineIndex);
                                    else if (lineIndex >= renderParameters.getLastSelectedLine())
                                        renderParameters.setLastSelectedLine(lineIndex + 1);
                                }
                            }
                            else {
                                if (lineIndex >= offsetToFirstLayer &&
                                    lineIndex == currentDetails.getStartLine() &&
                                    !currentDetails.getLayerOpen()) {

                                    if (lineIndex == renderParameters.getFirstSelectedLine() &&
                                        currentDetails.getEndLine() == renderParameters.getLastSelectedLine())
                                    {
                                        renderParameters.clearSelectedLines();
                                    }
                                    else {
                                        // Set selection to cover the whole of the layer.
                                        renderParameters.setFirstSelectedLine(currentDetails.getStartLine());
                                        renderParameters.setLastSelectedLine(currentDetails.getEndLine()); 
                                    }
                                }
                                else if (lineIndex == renderParameters.getFirstSelectedLine() && 
                                         lineIndex + 1 == renderParameters.getLastSelectedLine())
                                {
                                    renderParameters.clearSelectedLines();
                                }
                                else {
                                    // Set selection to line index.
                                    renderParameters.setFirstSelectedLine(lineIndex);
                                    renderParameters.setLastSelectedLine(lineIndex + 1);
                                }
                            }
                        }
                    }
                    if (i == 0 && topLine != lineIndex) {
                        topLine = lineIndex;
                        renderParameters.setRenderRequired();
                    }
                    nLinesShown = i;
                }
                

                lineNumberSpace = ((int)Math.floor(Math.log10(lineIndex)) + 3) * GUI_GCODE_PANEL_CHAR_WIDTH;
                ctx.style().text().color(originalTextColour);
            }
            nk_layout_space_end(ctx);
        }
        
        return nLinesShown;
    }

    private void layoutScrolled(NkContext ctx, float width, RenderParameters renderParameters) {
        try (MemoryStack stack = stackPush()) {
            NkStyleScrollbar barStyle = ctx.style().scrollv();
            float barWidth = ctx.style().window().scrollbar_size().x();
            float barHeight = ctx.style().window().scrollbar_size().y();
            float viewWidth = width - barWidth - ctx.style().window().group_padding().x();
            float viewHeight = panelHeight - barHeight - 2.0f * (ctx.style().window().spacing().y() + GUI_GCODE_PANEL_ROW_HEIGHT);
            int viewLines = (int)(viewHeight / lineHeight); // Truncate so only whole lines are shown.
            nk_layout_space_begin(ctx, NK_STATIC, viewHeight, 10000);
            NkRect currentBounds = NkRect.mallocStack(stack);

            currentBounds.set(0.0f, 0.0f, viewWidth, viewHeight);
            nk_layout_space_push(ctx, currentBounds);
            if (nk_group_begin(ctx, "ScrolledLayers", NK_WINDOW_NO_SCROLLBAR)) {
                layoutLayers(ctx, viewLines, renderParameters);
                nk_group_end(ctx);
            }
            
            currentBounds.set(viewWidth, 0.0f, barWidth, viewHeight);
            nk_layout_space_push(ctx, currentBounds);
            rectLocalToScreen(ctx, stack, currentBounds);
            layoutVerticalScrollbar(ctx, currentBounds, viewLines, renderParameters);

            currentBounds.set(0.0f, viewHeight, viewWidth, barHeight);
            nk_layout_space_push(ctx, currentBounds);
            rectLocalToScreen(ctx, stack, currentBounds);
            layoutHorizontalScrollbar(ctx, currentBounds, viewWidth, renderParameters);
            nk_layout_space_end(ctx);
        }
    }

    private void rectLocalToScreen(NkContext ctx, MemoryStack stack, NkRect rect) {
        NkVec2 v2 = NkVec2.mallocStack(stack);
        v2.set(rect.x(), rect.y());
        nk_layout_space_to_screen(ctx, v2);
        rect.x(v2.x());
        rect.y(v2.y());
    }
    
    private void layoutHorizontalScrollbar(NkContext ctx, NkRect barBounds, float viewWidth, RenderParameters renderParameters) {
        try (MemoryStack stack = stackPush()) {
            NkStyleScrollbar barStyle = ctx.style().scrollv();
            float maxOffset = totalColumnWidth - viewWidth;
            float cursorWidth = 0.0f;
            float beforeWidth = 0.0f;
            float afterWidth = 0.0f;
            if (maxOffset <= 0.0f)
            {
                columnOffset = 0.0f;
                cursorWidth = barBounds.w();
            }
            else
            {
                float cursorFraction = viewWidth / totalColumnWidth;
                cursorWidth = Math.max(cursorFraction * barBounds.w(), GUI_GCODE_PANEL_MIN_SCROLLBAR_SIZE);

                float remainingWidth = barBounds.w() - cursorWidth;
                beforeWidth = remainingWidth * columnOffset / maxOffset;
                afterWidth = remainingWidth - beforeWidth;
            }
            NkRect afterBounds = NkRect.mallocStack(stack);
            NkRect beforeBounds = NkRect.mallocStack(stack);
            NkRect cursorBounds = NkRect.mallocStack(stack);
            
            beforeBounds.set(barBounds.x(), barBounds.y(), beforeWidth, barBounds.h());
            cursorBounds.set(barBounds.x() + beforeWidth, barBounds.y(), cursorWidth, barBounds.h());
            afterBounds.set(barBounds.x() + beforeWidth + cursorWidth, barBounds.y(), afterWidth, barBounds.h());

            int[] state = new int[1];
            columnOffset = scrollbarBehaviour(ctx,
                                              state,
                                              true,
                                              barBounds,
                                              cursorBounds,
                                              beforeBounds,
                                              afterBounds,
                                              columnOffset,
                                              totalColumnWidth,
                                              viewWidth,
                                              false,
                                              renderParameters);

            NkColor backgroundColour;
            NkColor cursorColour;
            if ((state[0] &  NK_WIDGET_STATE_ACTIVE) ==  NK_WIDGET_STATE_ACTIVE) {
                backgroundColour = barStyle.active().data().color();
                cursorColour = barStyle.cursor_active().data().color();
            }
            else if ((state[0] &  NK_WIDGET_STATE_HOVERED) ==  NK_WIDGET_STATE_HOVERED) {
                backgroundColour = barStyle.hover().data().color();
                cursorColour = barStyle.cursor_hover().data().color();
            }
            else {
                backgroundColour = barStyle.normal().data().color();
                cursorColour = barStyle.cursor_normal().data().color();
            }
            
            // Draw the background and cursor.
            NkCommandBuffer canvas = nk_window_get_canvas(ctx);
            nk_fill_rect(canvas, barBounds, barStyle.rounding(), backgroundColour);
            nk_fill_rect(canvas, cursorBounds, barStyle.rounding_cursor(), cursorColour);
        }
    }

    private void layoutVerticalScrollbar(NkContext ctx, NkRect barBounds, float viewLines, RenderParameters renderParameters) {
        try (MemoryStack stack = stackPush()) {
            NkStyleScrollbar barStyle = ctx.style().scrollv();
            float maxOffset = totalNumberOfLines - viewLines;
            float cursorHeight = 0.0f;
            float beforeHeight = 0.0f;
            float afterHeight = 0.0f;
            if (maxOffset <= 0)
            {
                lineOffset = 0;
                cursorHeight = barBounds.h();
            }
            else
            {
                float cursorFraction = viewLines / (float)totalNumberOfLines;
                cursorHeight = Math.max(cursorFraction * barBounds.h(), GUI_GCODE_PANEL_MIN_SCROLLBAR_SIZE);

                float remainingHeight = barBounds.h() - cursorHeight;
                beforeHeight = remainingHeight * (lineOffset / (float)maxOffset);
                afterHeight = remainingHeight - beforeHeight;
            }
            NkRect afterBounds = NkRect.mallocStack(stack);
            NkRect beforeBounds = NkRect.mallocStack(stack);
            NkRect cursorBounds = NkRect.mallocStack(stack);
            
            beforeBounds.set(barBounds.x(), barBounds.y(), barBounds.w(), beforeHeight);
            cursorBounds.set(barBounds.x(), barBounds.y() + beforeHeight, barBounds.w(), cursorHeight);
            afterBounds.set(barBounds.x(), barBounds.y() + beforeHeight + cursorHeight, barBounds.w(), afterHeight);

            int[] state = new int[1];
            lineOffset = Math.round(scrollbarBehaviour(ctx,
                                                       state,
                                                       true,
                                                       barBounds,
                                                       cursorBounds,
                                                       beforeBounds,
                                                       afterBounds,
                                                       lineOffset,
                                                       totalNumberOfLines,
                                                       viewLines,
                                                       true,
                                                       renderParameters));

            NkColor backgroundColour;
            NkColor cursorColour;
            if ((state[0] &  NK_WIDGET_STATE_ACTIVE) ==  NK_WIDGET_STATE_ACTIVE) {
                backgroundColour = barStyle.active().data().color();
                cursorColour = barStyle.cursor_active().data().color();
            }
            else if ((state[0] &  NK_WIDGET_STATE_HOVERED) ==  NK_WIDGET_STATE_HOVERED) {
                backgroundColour = barStyle.hover().data().color();
                cursorColour = barStyle.cursor_hover().data().color();
            }
            else {
                backgroundColour = barStyle.normal().data().color();
                cursorColour = barStyle.cursor_normal().data().color();
            }
            
            // Draw the background and cursor.
            NkCommandBuffer canvas = nk_window_get_canvas(ctx);
            nk_fill_rect(canvas, barBounds, barStyle.rounding(), backgroundColour);
            nk_fill_rect(canvas, cursorBounds, barStyle.rounding_cursor(), cursorColour);
        }
    }

    private float scrollbarBehaviour(NkContext ctx,
                                   int[] state,
                                   boolean hasScrolling,
                                   NkRect barBounds,
                                   NkRect cursorBounds,
                                   NkRect beforeBounds,
                                   NkRect afterBounds,
                                   float scrollOffset,
                                   float maxSize,
                                   float viewSize,
                                   boolean isVertical,
                                   RenderParameters renderParameters)
    {
        // Port of nk_scrollbar_behavior() from nuklear.h
        NkInput input = ctx.input();
        float newOffset = scrollOffset;
        if ((state[0] & NK_WIDGET_STATE_MODIFIED) == NK_WIDGET_STATE_MODIFIED)
            state[0] = NK_WIDGET_STATE_INACTIVE | NK_WIDGET_STATE_MODIFIED;
        else
            state[0] = NK_WIDGET_STATE_INACTIVE;
        if (input == null)
            return 0;

        int[] dummyState = new int[1];
        boolean left_mouse_down = input.mouse().buttons(NK_BUTTON_LEFT).down() != 0;
        boolean left_mouse_clicked = input.mouse().buttons(NK_BUTTON_LEFT).clicked() != 0;
        boolean left_mouse_click_in_cursor = nk_input_has_mouse_click_down_in_rect(input, NK_BUTTON_LEFT, cursorBounds, 1);
        if (nk_input_is_mouse_hovering_rect(input, barBounds))
            state[0] = NK_WIDGET_STATE_HOVERED;

        if (maxSize <= viewSize)
            return 0;
        
        float maxOffset = maxSize - viewSize;
        if (maxOffset < 0)
            maxOffset = 0;
        if (left_mouse_down && left_mouse_click_in_cursor && !left_mouse_clicked) {
            /* update cursor by mouse dragging */
            state[0] = NK_WIDGET_STATE_ACTIVE;
            if (isVertical) {
                if (input.mouse().pos().y() < barBounds.y() - 10.0f) {
                    newOffset = 0.0f;
                }
                else if (input.mouse().pos().y() > barBounds.y() + barBounds.h() + 10.0f) {
                    newOffset = maxOffset;
                }
                else {
                    newOffset += input.mouse().delta().y() * maxOffset / (barBounds.h() - cursorBounds.h());
                    if (newOffset < 0)
                        newOffset = 0;
                    if (newOffset > maxOffset)
                        newOffset = maxOffset;
                }
                float cursorY = barBounds.y() + (newOffset / (float)maxOffset) * (barBounds.h() - cursorBounds.h());
                input.mouse().buttons(NK_BUTTON_LEFT).clicked_pos().y(cursorY + 0.5f * cursorBounds.h());
            } 
            else {
                if (input.mouse().pos().x() < barBounds.x() - 10.0f) {
                    newOffset = 0;
                }
                else if (input.mouse().pos().x() > barBounds.x() + barBounds.w() + 10.0f) {
                    newOffset = maxOffset;
                }
                else {
                    newOffset += input.mouse().delta().x() * maxOffset / (barBounds.w() - cursorBounds.w());
                    if (newOffset < 0)
                        newOffset = 0;
                    if (newOffset > maxOffset)
                        newOffset = maxOffset;
                }                
                float cursorX = barBounds.x() + (newOffset / (float)maxOffset) * (barBounds.w() - cursorBounds.w());
                input.mouse().buttons(NK_BUTTON_LEFT).clicked_pos().x(cursorX + 0.5f * cursorBounds.w());
            }
        }
        else if (nk_window_is_hovered(ctx)) // Only respond to keyboard or mouse clicks inside the window.
        {
            if ((isVertical &&
                        (hasScrolling && nk_input_is_key_pressed(input, NK_KEY_SCROLL_UP)) ||
                         nk_input_is_key_pressed(input, NK_KEY_UP)) ||
                     (!isVertical && nk_input_is_key_pressed(input, NK_KEY_LEFT))) {
                // Scroll up by keyboard.
                if (nk_input_is_key_down(input, NK_KEY_SHIFT))
                    newOffset -= 1.0f;
                else if (nk_input_is_key_down(input, NK_KEY_CTRL))
                    newOffset -= 5.0 * viewSize;
                else
                    newOffset -= viewSize;
                if (newOffset < 0)
                    newOffset = 0;
            } 
            else if (buttonBehaviour(dummyState, beforeBounds, input, NK_BUTTON_DEFAULT)) {
                // Scroll page up by click on space before cursor.
                if (nk_input_is_key_down(input, NK_KEY_SHIFT))
                    newOffset -= 1;
                else if (nk_input_is_key_down(input, NK_KEY_CTRL))
                    newOffset = scrollToMouse(input, isVertical, maxOffset, barBounds, cursorBounds);
                else
                    newOffset -= viewSize;
                if (newOffset < 0)
                    newOffset = 0;
            }
            else if ((isVertical &&
                        (hasScrolling && nk_input_is_key_pressed(input, NK_KEY_SCROLL_DOWN)) ||
                         nk_input_is_key_pressed(input, NK_KEY_DOWN)) ||
                     (!isVertical && nk_input_is_key_pressed(input, NK_KEY_RIGHT))) {
                // Scroll down by keyboard.

                if (nk_input_is_key_down(input, NK_KEY_SHIFT))
                    ++newOffset;
                else if (nk_input_is_key_down(input, NK_KEY_CTRL))
                    newOffset += 5.0 * viewSize;
                else
                    newOffset += viewSize;
                if (newOffset > maxOffset)
                    newOffset = maxOffset;
            } 
            else if (buttonBehaviour(dummyState, afterBounds, input, NK_BUTTON_DEFAULT)) {
                // Scroll down by click on space after cursor.
                if (nk_input_is_key_down(input, NK_KEY_SHIFT))
                    newOffset += 1.0f;
                else if (nk_input_is_key_down(input, NK_KEY_CTRL))
                    newOffset = scrollToMouse(input, isVertical, maxOffset, barBounds, cursorBounds);
                else
                    newOffset += viewSize;
                if (newOffset > maxOffset)
                    newOffset = maxOffset;
            } 
            else if (hasScrolling) {
                float scrollDelta = (isVertical) ? input.mouse().scroll_delta().y() : input.mouse().scroll_delta().x();
                if (scrollDelta != 0.0f) {
                    int scrollStep = 10;
                    /* move cursor by mouse scrolling */
                    if (nk_input_is_key_down(input, NK_KEY_SHIFT))
                            scrollStep = 1;
                    else if (nk_input_is_key_down(input, NK_KEY_CTRL))
                            scrollStep = 50;

                    newOffset += scrollStep * (-scrollDelta);
                    if (newOffset < 0)
                        newOffset = 0;
                    if (newOffset > maxOffset)
                        newOffset = maxOffset;
                }
                else if (isVertical && nk_input_is_key_pressed(input, NK_KEY_SCROLL_START)) {
                    // Move cursor to the beginning.
                    newOffset = 0;
                } 
                else if (isVertical && nk_input_is_key_pressed(input, NK_KEY_SCROLL_END)) {
                    // Move cursor to the end.
                    newOffset = maxOffset;
                }
            }
        }
        if (((state[0] & NK_WIDGET_STATE_HOVER) ==  NK_WIDGET_STATE_HOVER )&&
            !nk_input_is_mouse_prev_hovering_rect(input, barBounds)) {
            state[0] |= NK_WIDGET_STATE_ENTERED;
        }
        else if (nk_input_is_mouse_prev_hovering_rect(input, barBounds))
            state[0] |= NK_WIDGET_STATE_LEFT;

        if (newOffset != scrollOffset)
            renderParameters.setRenderRequired();

        return newOffset;
    }
    
    private float scrollToMouse(NkInput input, boolean isVertical, float maxOffset, NkRect barBounds, NkRect cursorBounds) {
        float scrollOffset = 0.0f;
        if (isVertical) {
            float mouseHeight = input.mouse().buttons(NK_BUTTON_LEFT).clicked_pos().y() - barBounds.y();
            mouseHeight -= 0.5f * cursorBounds.h(); // Adjust height so cursor is centered on position.
            scrollOffset = maxOffset * mouseHeight / (barBounds.h() - cursorBounds.h());
        }
        else {
            float mouseWidth = input.mouse().buttons(NK_BUTTON_LEFT).clicked_pos().x() - barBounds.x();
            mouseWidth -= 0.5f * cursorBounds.w(); // Adjust width so cursor is centered on position.
            scrollOffset = maxOffset * mouseWidth / (barBounds.w() - cursorBounds.w());
        }
        
        return scrollOffset;
    }
    
    private boolean buttonBehaviour(int[] state, NkRect bounds, NkInput input, int behavior)
    {
        // Port of nk_button_behavior() from nuklear.h
        boolean activated = false;
        if ((state[0] & NK_WIDGET_STATE_MODIFIED) == NK_WIDGET_STATE_MODIFIED)
            state[0] = NK_WIDGET_STATE_INACTIVE | NK_WIDGET_STATE_MODIFIED;
        else
            state[0] = NK_WIDGET_STATE_INACTIVE;
        
        if (input != null)
        {
            if (nk_input_is_mouse_hovering_rect(input, bounds)) {
                if (nk_input_is_mouse_down(input, NK_BUTTON_LEFT)) {
                    state[0] = NK_WIDGET_STATE_ACTIVE;
                }
                else
                    state[0] = NK_WIDGET_STATE_HOVERED;
                if (nk_input_has_mouse_click(input, NK_BUTTON_LEFT) &&
                    nk_input_has_mouse_click_in_rect(input, NK_BUTTON_LEFT, bounds)) {
                    if (behavior == NK_BUTTON_DEFAULT) {
                        // Original C has
                        //     activated = nk_input_is_mouse_released(input, NK_BUTTON_LEFT);
                        // but it doesn't seem to work.
                        activated = !nk_input_is_mouse_down(input, NK_BUTTON_LEFT);
                    }
                    else {
                        activated = nk_input_is_mouse_down(input, NK_BUTTON_LEFT);
                    }
                }
            }
            if (((state[0] & NK_WIDGET_STATE_HOVER) == NK_WIDGET_STATE_HOVER) && !nk_input_is_mouse_prev_hovering_rect(input, bounds))
                state[0] |= NK_WIDGET_STATE_ENTERED;
            else if (nk_input_is_mouse_prev_hovering_rect(input, bounds))
                state[0] |= NK_WIDGET_STATE_LEFT;
        }
       return activated;
    }
    
    @Override
    protected void layoutSideButton(NkContext ctx, RenderParameters renderParameters) {
        try (MemoryStack stack = stackPush()) {
            NkColor hoverColour = NkColor.mallocStack(stack);
            NkColor activeColour = NkColor.mallocStack(stack);

            hoverColour.set(ctx.style().button().hover().data().color());
            activeColour.set(ctx.style().button().active().data().color());
            if (draggingResizeBar) {
                // Stop the side button from flashing during a resize drag. It happens because as the mouse
                // is dragged, it can hover over the side button.
                ctx.style().button().hover().data().color().set((byte)96, (byte)96, (byte)96, (byte)160);
                ctx.style().button().active().data().color().set((byte)96, (byte)96, (byte)96, (byte)160);
            }
            else {
                ctx.style().button().hover().data().color().set((byte)128, (byte)128, (byte)128, (byte)255);
                ctx.style().button().active().data().color().set((byte)192, (byte)192, (byte)192, (byte)255);
            }
            
            if(nk_button_label(ctx, "")) {
                panelExpanded = !panelExpanded;
                renderParameters.setRenderRequired();
            }
            
            ctx.style().button().hover().data().color().set(hoverColour);
            ctx.style().button().active().data().color().set(activeColour);
        }
    }
    
    private void layoutResizeBar(NkContext ctx, RenderParameters renderParameters) {
        if (draggingResizeBar) {
            if (nk_input_is_mouse_down(ctx.input(), NK_BUTTON_LEFT)) {
                float delta = resizeBarAnchorX - ctx.input().mouse().pos().x();
                resizeBarAnchorX = ctx.input().mouse().pos().x();
                expandedPanelWidth +=  delta;
                if (expandedPanelWidth < GUI_GCODE_MIN_PANEL_WIDTH)
                    expandedPanelWidth = GUI_GCODE_MIN_PANEL_WIDTH;
                float maxWidth = renderParameters.getWindowWidth() - GUI_CONTROL_PANEL_WIDTH - 2.0f * GUI_GCODE_PANEL_X;
                if (expandedPanelWidth > maxWidth)
                    expandedPanelWidth = maxWidth;
                renderParameters.setRenderRequired();
            }
            else {
                draggingResizeBar = false;
                resizeBarAnchorX = 0.0f;
            }
        }
        else if (nk_widget_has_mouse_click_down(ctx, NK_BUTTON_LEFT, true)) {
                draggingResizeBar = true;
                resizeBarAnchorX = ctx.input().mouse().pos().x();
                renderParameters.setRenderRequired();
        }
        if (draggingResizeBar || nk_widget_is_hovered(ctx))
            renderParameters.setUseResizeCursor(true);
        nk_label(ctx, "", NK_TEXT_LEFT);
    }
}