package celuk.gcodeviewer.gui;

import celuk.language.I18n;
import celuk.gcodeviewer.engine.RenderParameters;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.joml.Vector3f;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.*;
import org.lwjgl.nuklear.NkColor;

public class GCVControlPanel extends GCVPanel {

    // These values are used GUI GCVControlPanel.
    public static final int GUI_CONTROL_PANEL_WIDTH = 270;
    public static final int GUI_CONTROL_PANEL_OPEN_HEIGHT = 300;
    public static final int GUI_CONTROL_PANEL_ROW_HEIGHT = 35;
    public static final int GUI_CONTROL_PANEL_TOOL_ROW_HEIGHT = 40;
    public static final int GUI_CONTROL_PANEL_CLOSED_HEIGHT = 30;
    public static final int GUI_CONTROL_PANEL_SIDE_WIDTH = 10;

    private String colourAsTypeMsg = "controlPanel.colourAsType";
    private String frameTimeMsg = "controlPanel.frameTime";
    private String loadGCodeMsg = "controlPanel.loadGCode";
    private String reloadGCodeMsg = "controlPanel.reloadGCode";
    private String resetViewMsg = "controlPanel.resetView";
    private String showAnglesMsg = "controlPanel.showAngles";
    private String showMovesMsg = "controlPanel.showMoves";
    private String showOnlySelectedMsg = "controlPanel.showOnlySelected";
    private String showToolNMsg = "controlPanel.showToolN";

    private List<Integer> toolList = new ArrayList<>();
    private List<String> typeList = new ArrayList<>();
    private Map<String, String> typeI18nMap = new HashMap<>();
    private boolean showAdvanceOptions = false;
    
    public GCVControlPanel(boolean showAdvanceOptions) {
        this.showAdvanceOptions = showAdvanceOptions;
    }

    public void loadMessages() {
        colourAsTypeMsg = I18n.t(colourAsTypeMsg);
        frameTimeMsg = I18n.t(frameTimeMsg);
        loadGCodeMsg = I18n.t(loadGCodeMsg);
        resetViewMsg = I18n.t(resetViewMsg);
        reloadGCodeMsg = I18n.t(reloadGCodeMsg);
        showAnglesMsg = I18n.t(showAnglesMsg);
        showMovesMsg = I18n.t(showMovesMsg);
        showOnlySelectedMsg = I18n.t(showOnlySelectedMsg);
        showToolNMsg = I18n.t(showToolNMsg);
    }
    
    public void setToolList(List<Integer> toolList) {
        this.toolList = toolList;
    }
    
    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
        typeI18nMap.clear();
        typeList.forEach(t -> typeI18nMap.put(t, I18n.t(t)));
    }

    public void layout(NkContext ctx, int x, int y, RenderParameters renderParameters) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            float windowPaddingX = ctx.style().window().padding().x();
            float windowPaddingY = ctx.style().window().padding().y();
            float groupPaddingX = ctx.style().window().group_padding().x();
            float groupPaddingY = ctx.style().window().group_padding().y();
            boolean colourAsType = (renderParameters.getColourMode() == RenderParameters.ColourMode.COLOUR_AS_TYPE);
            boolean colourAsData = (renderParameters.getColourMode() == RenderParameters.ColourMode.COLOUR_AS_DATA);
            
            if (panelExpanded) {
                panelWidth = GUI_CONTROL_PANEL_WIDTH;
                panelHeight = GUI_CONTROL_PANEL_OPEN_HEIGHT + toolList.size() * GUI_CONTROL_PANEL_TOOL_ROW_HEIGHT;
                if (showAdvanceOptions)
                    panelHeight += GUI_CONTROL_PANEL_TOOL_ROW_HEIGHT;
                if (colourAsType)
                    panelHeight += typeList.size() * GUI_CONTROL_PANEL_TOOL_ROW_HEIGHT;
            }
            else {
                panelWidth = GUI_CONTROL_PANEL_SIDE_WIDTH + 4.0f * windowPaddingX;
                panelHeight = GUI_CONTROL_PANEL_CLOSED_HEIGHT;
            }
            panelX = x;
            panelY = y;
            nk_rect(panelX, panelY, panelWidth, panelHeight, rect);
                
            if (nk_begin(ctx, "Control Panel", rect, NK_WINDOW_NO_SCROLLBAR)) {
                if (panelExpanded) {
                    float w = rect.w() - 4.0f * windowPaddingX - 4.0f * groupPaddingX - GUI_CONTROL_PANEL_SIDE_WIDTH;
                    nk_layout_row_begin(ctx, NK_STATIC, rect.h() - 2.0f * windowPaddingY, 2);
                    nk_layout_row_push(ctx, w);
                    if (nk_group_begin(ctx, "ControlGroup", NK_WINDOW_NO_SCROLLBAR)) {
                        nk_layout_row_dynamic(ctx, GUI_CONTROL_PANEL_ROW_HEIGHT, 1);
                        if(nk_button_label(ctx, resetViewMsg)) {
                            renderParameters.setViewResetRequired();
                        }
                        nk_layout_row_dynamic(ctx, GUI_CONTROL_PANEL_ROW_HEIGHT, 1);
                        if(nk_button_label(ctx, loadGCodeMsg)) {
                            renderParameters.setLoadGCodeRequested();
                        }
                        nk_layout_row_dynamic(ctx, GUI_CONTROL_PANEL_ROW_HEIGHT, 1);
                        if(nk_button_label(ctx, reloadGCodeMsg)) {
                            renderParameters.setReloadGCodeRequested();
                        }
                        layoutCheckboxRow(ctx,
                                          w,
                                          showMovesMsg,
                                          renderParameters.getShowMoves(),
                                          renderParameters::setShowMoves);

                        if (showAdvanceOptions) {
                            layoutCheckboxRow(ctx,
                                          w,
                                          showAnglesMsg,
                                          renderParameters.getShowAngles(),
                                          renderParameters::setShowAngles);
                        }

                        layoutCheckboxRow(ctx,
                                          w,
                                          showOnlySelectedMsg,
                                          renderParameters.getShowOnlySelected(),
                                          renderParameters::setShowOnlySelected);

                        IntBuffer checkBuffer = stack.mallocInt(1);
                        NkStyleToggle checkboxStyle = ctx.style().checkbox();
                        NkColor cbnc = NkColor.mallocStack();
                        NkColor cbhc = NkColor.mallocStack();
                        cbnc.set(checkboxStyle.cursor_normal().data().color());
                        cbhc.set(checkboxStyle.cursor_hover().data().color());
                        NkColor tc = NkColor.mallocStack();
                        toolList.forEach(t -> {
                            String label = showToolNMsg.replaceAll("#1", Integer.toString(t));
                            boolean currentValue = renderParameters.getShowFlagForTool(t);
                            checkBuffer.put(0, (currentValue ? 1 : 0));
                            if (!colourAsType && !colourAsData)
                            {
                                Vector3f c = renderParameters.getColourForTool(t);
                                tc.set((byte)(255.0f * c.x() + 0.5f), (byte)(255.0f * c.y() + 0.5f), (byte)(255.0f * c.z() + 0.5f), (byte)255);
                                checkboxStyle.cursor_normal().data().color().set(tc);
                                checkboxStyle.cursor_hover().data().color().set(tc);
                            }
                            nk_layout_row_begin(ctx, NK_STATIC, GUI_CONTROL_PANEL_ROW_HEIGHT, 1);
                            nk_layout_row_push(ctx, w);
                            nk_checkbox_label(ctx, label, checkBuffer);
                            nk_layout_row_end(ctx);
                            renderParameters.setShowFlagForTool(t, (checkBuffer.get(0) != 0));
                        });

                        checkboxStyle.cursor_normal().data().color().set(cbhc);
                        checkboxStyle.cursor_hover().data().color().set(cbhc);
                        layoutCheckboxRow(ctx,
                                          w,
                                          colourAsTypeMsg,
                                          (renderParameters.getColourMode() == RenderParameters.ColourMode.COLOUR_AS_TYPE ||
                                           renderParameters.getColourMode() == RenderParameters.ColourMode.COLOUR_AS_DATA),
                                          (f) -> {
                                              if (f) {
                                                  if (colourAsData)
                                                      renderParameters.setColourMode(RenderParameters.ColourMode.COLOUR_AS_DATA);
                                                  else
                                                      renderParameters.setColourMode(RenderParameters.ColourMode.COLOUR_AS_TYPE);
                                              }
                                              else
                                                  renderParameters.setColourMode(RenderParameters.ColourMode.COLOUR_AS_TOOL);
                                          });
                        if (colourAsType) {
                            typeList.forEach(t -> {
                                Vector3f c = renderParameters.getColourForType(t);
                                tc.set((byte)(255.0f * c.x() + 0.5f), (byte)(255.0f * c.y() + 0.5f), (byte)(255.0f * c.z() + 0.5f), (byte)255);
                                checkboxStyle.cursor_normal().data().color().set(tc);
                                checkboxStyle.cursor_hover().data().color().set(tc);
                                nk_layout_row_begin(ctx, NK_STATIC, GUI_CONTROL_PANEL_ROW_HEIGHT, 1);
                                nk_layout_row_push(ctx, w);
                                boolean currentValue = renderParameters.getShowFlagForType(t);
                                checkBuffer.put(0, (currentValue ? 1 : 0));
                                
                                nk_checkbox_label(ctx, typeI18nMap.get(t), checkBuffer);
                                nk_layout_row_end(ctx);
                                renderParameters.setShowFlagForType(t, (checkBuffer.get(0) != 0));
                            });
                        }

                        checkboxStyle.cursor_normal().data().color().set(cbhc);
                        checkboxStyle.cursor_hover().data().color().set(cbhc);
 
                        layoutAnimationRow(ctx, w, renderParameters);
                        // Show the frame rate.
                        nk_layout_row_dynamic(ctx, GUI_CONTROL_PANEL_ROW_HEIGHT, 1);
                        double frameTime = renderParameters.getFrameTime();
                        DecimalFormat ftFormat = new DecimalFormat("0.0"); 
                        nk_label(ctx, frameTimeMsg.replaceAll("#1", ftFormat.format(1000.0 * frameTime)), NK_TEXT_ALIGN_LEFT);

                        nk_group_end(ctx);
                    }
                    nk_layout_row_push(ctx, GUI_CONTROL_PANEL_SIDE_WIDTH);
                    layoutSideButton(ctx, renderParameters); 
                    nk_layout_row_end(ctx);
                }
                else {
                    nk_layout_row_begin(ctx, NK_STATIC, GUI_CONTROL_PANEL_CLOSED_HEIGHT - 2.0f * windowPaddingY, 1);
                    nk_layout_row_push(ctx, GUI_CONTROL_PANEL_SIDE_WIDTH);
                    layoutSideButton(ctx, renderParameters); 
                    nk_layout_row_end(ctx);
                }
            }
            nk_end(ctx);
        }
    }

    private void layoutCheckboxRow(NkContext ctx,
                                   float width,
                                   String label,
                                   boolean currentValue,
                                   Consumer<Boolean> setSelected) {

        try (MemoryStack stack = stackPush()) {
            IntBuffer valueBuffer = stack.mallocInt(1);
            valueBuffer.put(0, currentValue ? 1 : 0);
            nk_layout_row_begin(ctx, NK_STATIC, GUI_CONTROL_PANEL_ROW_HEIGHT, 1);
            nk_layout_row_push(ctx, width);
            nk_checkbox_label(ctx, label, valueBuffer);
            nk_layout_row_end(ctx);
            boolean newValue = (valueBuffer.get(0) != 0);
            setSelected.accept(newValue);
        }
    }

    private void layoutAnimationRow(NkContext ctx,
                                    float width,
                                    RenderParameters renderParameters) {

        try (MemoryStack stack = stackPush()) {
            
            NkStyleButton buttonStyle = ctx.style().button();
            NkColor originalActiveColour = NkColor.mallocStack(stack);
            originalActiveColour.set(buttonStyle.text_active());
            NkColor originalHoverColour = NkColor.mallocStack(stack);
            originalHoverColour.set(buttonStyle.text_hover());
            NkColor originalNormalColour = NkColor.mallocStack(stack);
            originalNormalColour.set(buttonStyle.text_normal());
            
            NkColor normalOnColour = NkColor.mallocStack(stack);
            normalOnColour.r((byte)0).g((byte)0).b((byte)0).a((byte)160);
            NkColor normalOffColour = NkColor.mallocStack(stack);
            normalOffColour.r((byte)255).g((byte)255).b((byte)255).a((byte)160);
            NkColor hoverOnColour = NkColor.mallocStack(stack);
            hoverOnColour.r((byte)0).g((byte)0).b((byte)0).a((byte)255);
            NkColor hoverOffColour = NkColor.mallocStack(stack);
            hoverOffColour.r((byte)255).g((byte)255).b((byte)255).a((byte)255);
            NkColor activeOnColour = NkColor.mallocStack(stack);
            activeOnColour.r((byte)0).g((byte)0).b((byte)0).a((byte)255);
            NkColor activeOffColour = NkColor.mallocStack(stack);
            activeOffColour.r((byte)255).g((byte)255).b((byte)255).a((byte)255);
            
            RenderParameters.AnimationMode currentMode = renderParameters.getAnimationMode();
            RenderParameters.AnimationMode newMode = currentMode;
            float buttonWidth = 0.20f * width - 4.0f; //Account for gap between buttons.
            nk_layout_row_begin(ctx, NK_STATIC, GUI_CONTROL_PANEL_ROW_HEIGHT, 5);
            nk_layout_row_push(ctx, buttonWidth);
            setButtonColours(buttonStyle, 
                             currentMode == RenderParameters.AnimationMode.BACKWARD_FAST,
                             activeOnColour, hoverOnColour, normalOnColour,
                             activeOffColour, hoverOffColour, normalOffColour);
            if (nk_button_label(ctx, "<<"))
                newMode = RenderParameters.AnimationMode.BACKWARD_FAST;
            nk_layout_row_push(ctx, buttonWidth);
            setButtonColours(buttonStyle, 
                             currentMode == RenderParameters.AnimationMode.BACKWARD_PLAY,
                             activeOnColour, hoverOnColour, normalOnColour,
                             activeOffColour, hoverOffColour, normalOffColour);
            if (nk_button_label(ctx, "<"))
                newMode = RenderParameters.AnimationMode.BACKWARD_PLAY;
            nk_layout_row_push(ctx, buttonWidth);
            setButtonColours(buttonStyle, 
                             currentMode == RenderParameters.AnimationMode.PAUSE,
                             activeOnColour, hoverOnColour, normalOnColour,
                             activeOffColour, hoverOffColour, normalOffColour);
            if (nk_button_label(ctx, "II"))
                newMode = RenderParameters.AnimationMode.PAUSE;
            nk_layout_row_push(ctx, buttonWidth);
            setButtonColours(buttonStyle, 
                             currentMode == RenderParameters.AnimationMode.FORWARD_PLAY,
                             activeOnColour, hoverOnColour, normalOnColour,
                             activeOffColour, hoverOffColour, normalOffColour);
            if (nk_button_label(ctx, ">"))
                newMode = RenderParameters.AnimationMode.FORWARD_PLAY;
            nk_layout_row_push(ctx, buttonWidth);
            setButtonColours(buttonStyle, 
                             currentMode == RenderParameters.AnimationMode.FORWARD_FAST,
                             activeOnColour, hoverOnColour, normalOnColour,
                             activeOffColour, hoverOffColour, normalOffColour);
            if (nk_button_label(ctx, ">>"))
                newMode = RenderParameters.AnimationMode.FORWARD_FAST;
            nk_layout_row_end(ctx);
            renderParameters.setAnimationMode(newMode);
            buttonStyle.text_active().set(originalActiveColour);
            buttonStyle.text_hover().set(originalHoverColour);
            buttonStyle.text_normal().set(originalNormalColour);
        }
    }

    private void setButtonColours(NkStyleButton buttonStyle,
                                  boolean setOn,
                                  NkColor activeOnColour,
                                  NkColor hoverOnColour,
                                  NkColor normalOnColour,
                                  NkColor activeOffColour,
                                  NkColor hoverOffColour,
                                  NkColor normalOffColour)
    {
        if (setOn) {
            buttonStyle.text_active().set(activeOnColour);
            buttonStyle.text_hover().set(hoverOnColour);
            buttonStyle.text_normal().set(normalOnColour);
        }
        else {
            buttonStyle.text_active().set(activeOffColour);
            buttonStyle.text_hover().set(hoverOffColour);
            buttonStyle.text_normal().set(normalOffColour);
        }
    }
}