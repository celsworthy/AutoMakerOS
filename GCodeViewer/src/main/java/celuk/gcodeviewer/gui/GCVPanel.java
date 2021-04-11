package celuk.gcodeviewer.gui;

import celuk.gcodeviewer.engine.RenderParameters;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.*;
import org.lwjgl.nuklear.NkColor;

public class GCVPanel {

    protected boolean panelExpanded = false;
    protected float panelX = 0.0f;
    protected float panelY = 0.0f;
    protected float panelWidth = 0.0f;
    protected float panelHeight = 0.0f;
    
    public GCVPanel() {
    }

    public void setPanelExpanded(boolean panelExpanded) {
        this.panelExpanded = panelExpanded;
    }

    public boolean isPanelExpanded() {
        return panelExpanded;
    }
    
    public int getPanelX() {
        return (int)panelX;
    }
    
    public int getPanelY() {
        return (int)panelY;
    }

    public int getPanelWidth() {
        return (int)panelWidth;
    }

    public int getPanelHeight() {
        return (int)panelHeight;
    }

    protected void layoutSideButton(NkContext ctx, RenderParameters renderParameters) {
        try (MemoryStack stack = stackPush()) {
            NkColor hoverColour = NkColor.mallocStack(stack);
            NkColor activeColour = NkColor.mallocStack(stack);
            hoverColour.set(ctx.style().button().hover().data().color());
            activeColour.set(ctx.style().button().active().data().color());

            ctx.style().button().hover().data().color().set((byte)128, (byte)128, (byte)128, (byte)255);
            ctx.style().button().active().data().color().set((byte)192, (byte)192, (byte)192, (byte)255);
            
            if(nk_button_label(ctx, "")) {
                panelExpanded = !panelExpanded;
                renderParameters.setRenderRequired();
            }
            
            ctx.style().button().hover().data().color().set(hoverColour);
            ctx.style().button().active().data().color().set(activeColour);
        }
    }
}