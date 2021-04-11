package celuk.gcodeviewer.gui;

import celuk.gcodeviewer.engine.GCodeViewerGUIConfiguration;
import celuk.gcodeviewer.engine.LayerDetails;
import celuk.gcodeviewer.engine.RenderParameters;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.BufferUtils.createByteBuffer;

import celuk.gcodeviewer.engine.renderers.GUIRenderer;
import celuk.gcodeviewer.shaders.GUIShader;

import java.io.IOException;
import java.io.InputStream;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.NkAllocator;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkMouse;
import org.lwjgl.nuklear.NkStyleButton;
import org.lwjgl.nuklear.NkStyleEdit;
import org.lwjgl.nuklear.NkStyleProperty;
import org.lwjgl.nuklear.NkStyleToggle;
import org.lwjgl.nuklear.NkStyleWindow;
import org.lwjgl.nuklear.NkStyleWindowHeader;
import org.lwjgl.nuklear.NkUserFont;
import org.lwjgl.nuklear.NkUserFontGlyph;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author George Salter
 */
public class GUIManager {
    
    private static final NkAllocator ALLOCATOR;
    
    static {
        ALLOCATOR = NkAllocator.create()
            .alloc((handle, old, size) -> nmemAllocChecked(size))
            .mfree((handle, ptr) -> nmemFree(ptr));
    }
    
    private final GUIShader guiShader = new GUIShader();
    private final GUIRenderer guiRenderer;
    
    private final ByteBuffer ttf;
    private final RenderParameters renderParameters; 
    
    private NkContext nkContext = NkContext.create();
    private NkUserFont default_font = NkUserFont.create();

    private final double animationFrameInterval;
    private final int animationFrameStep;
    private final int animationFastFactor;
    private double nextFrameTime = 0.0;
    
    public GUIManager(long windowId, boolean showAdvanceOptions, double animationFrameInterval, int animationFrameStep, int animationFastFactor,RenderParameters renderParameters) {
        try {
            this.ttf = ioResourceToByteBuffer("/resources/FiraSans-Regular.ttf", 512 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.animationFrameInterval = animationFrameInterval;
        this.animationFrameStep = animationFrameStep;
        this.animationFastFactor = animationFastFactor;
        this.renderParameters = renderParameters;
        setup(windowId);
        guiRenderer = new GUIRenderer(nkContext, guiShader, showAdvanceOptions, renderParameters);
        guiRenderer.loadMessages();
        setupFont();
        setupStyle();
    }
    
    public void setFromGUIConfiguration(GCodeViewerGUIConfiguration guiConfiguration) {
        guiRenderer.setFromGUIConfiguration(guiConfiguration);
    }
    
    public void saveToGUIConfiguration(GCodeViewerGUIConfiguration guiConfiguration) {
        guiRenderer.saveToGUIConfiguration(guiConfiguration);
    }
    
    public final NkContext setup(long windowId) {
        glfwSetCursorPosCallback(windowId, (window, xpos, ypos) -> nk_input_motion(nkContext, (int)xpos, (int)ypos));
      
        glfwSetMouseButtonCallback(windowId, (window, button, action, mods) -> {
            try (MemoryStack stack = stackPush()) {
                DoubleBuffer cx = stack.mallocDouble(1);
                DoubleBuffer cy = stack.mallocDouble(1);

                glfwGetCursorPos(window, cx, cy);

                int x = (int)cx.get(0);
                int y = (int)cy.get(0);
                
                int nkButton;
                
                switch (button) {
                    case GLFW_MOUSE_BUTTON_RIGHT:
                        nkButton = NK_BUTTON_RIGHT;
                        break;
                    case GLFW_MOUSE_BUTTON_MIDDLE:
                        nkButton = NK_BUTTON_MIDDLE;
                        break;
                    default:
                        nkButton = NK_BUTTON_LEFT;
                }

                nk_input_button(nkContext, nkButton, x, y, action == GLFW_PRESS);
                renderParameters.setRenderRequired();
            }
        });

        nk_init(nkContext, ALLOCATOR, null);
        
/*
        nkContext.clip(it -> it
            .copy((handle, text, len) -> {
                if (len == 0) {
                    return;
                }

                try (MemoryStack stack = stackPush()) {
                    ByteBuffer str = stack.malloc(len + 1);
                    memCopy(text, memAddress(str), len);
                    str.put(len, (byte)0);

                    glfwSetClipboardString(windowId, str);
                    renderParameters.setRenderRequired();
                }
            })
            .paste((handle, edit) -> {
                long text = nglfwGetClipboardString(windowId);
                if (text != NULL) {
                    nnk_textedit_paste(edit, text, nnk_strlen(text));
                    renderParameters.setRenderRequired();
            }
            }));
*/
        return nkContext;
    }
    
    private void setupFont() {
        int BITMAP_W = 1024;
        int BITMAP_H = 1024;

        int FONT_HEIGHT = 28;
        int fontTexID   = glGenTextures();

        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        STBTTPackedchar.Buffer cdata = STBTTPackedchar.create(95);

        float scale;
        float descent;

        try (MemoryStack stack = stackPush()) {
            stbtt_InitFont(fontInfo, ttf);
            scale = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);

            IntBuffer d = stack.mallocInt(1);
            stbtt_GetFontVMetrics(fontInfo, null, d, null);
            descent = d.get(0) * scale;

            ByteBuffer bitmap = memAlloc(BITMAP_W * BITMAP_H);

            STBTTPackContext pc = STBTTPackContext.mallocStack(stack);
            stbtt_PackBegin(pc, bitmap, BITMAP_W, BITMAP_H, 0, 1, NULL);
            stbtt_PackSetOversampling(pc, 4, 4);
            stbtt_PackFontRange(pc, ttf, 0, FONT_HEIGHT, 32, cdata);
            stbtt_PackEnd(pc);

            // Convert R8 to RGBA8
            ByteBuffer texture = memAlloc(BITMAP_W * BITMAP_H * 4);

            for (int i = 0; i < bitmap.capacity(); i++) {
                texture.putInt((bitmap.get(i) << 24) | 0x00FFFFFF);
            }

            texture.flip();

            glBindTexture(GL_TEXTURE_2D, fontTexID);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, BITMAP_W, BITMAP_H, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, texture);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

            memFree(texture);
            memFree(bitmap);
        }

        default_font
            .width((handle, h, text, len) -> {
                float text_width = 0;
                try (MemoryStack stack = stackPush()) {
                    IntBuffer unicode = stack.mallocInt(1);

                    int glyph_len = nnk_utf_decode(text, memAddress(unicode), len);
                    int text_len  = glyph_len;

                    if (glyph_len == 0) {
                        return 0;
                    }

                    IntBuffer advance = stack.mallocInt(1);
                    while (text_len <= len && glyph_len != 0) {
                        if (unicode.get(0) == NK_UTF_INVALID) {
                            break;
                        }

                        /* query currently drawn glyph information */
                        stbtt_GetCodepointHMetrics(fontInfo, unicode.get(0), advance, null);
                        text_width += advance.get(0) * scale;

                        /* offset next glyph */
                        glyph_len = nnk_utf_decode(text + text_len, memAddress(unicode), len - text_len);
                        text_len += glyph_len;
                    }
                }
                
                return text_width;
            })
            .height(FONT_HEIGHT)
            .query((handle, font_height, glyph, codepoint, next_codepoint) -> {
                try (MemoryStack stack = stackPush()) {
                    FloatBuffer x = stack.floats(0.0f);
                    FloatBuffer y = stack.floats(0.0f);

                    STBTTAlignedQuad q= STBTTAlignedQuad.mallocStack(stack);
                    IntBuffer advance = stack.mallocInt(1);
                    
                    // Replace non-ASCII printable characters with a space.
                    if (codepoint < 32 || codepoint > 127)
                        codepoint = 32; // Space

                    stbtt_GetPackedQuad(cdata, BITMAP_W, BITMAP_H, codepoint - 32, x, y, q, false);
                    stbtt_GetCodepointHMetrics(fontInfo, codepoint, advance, null);

                    NkUserFontGlyph ufg = NkUserFontGlyph.create(glyph);

                    ufg.width(q.x1() - q.x0());
                    ufg.height(q.y1() - q.y0());
                    ufg.offset().set(q.x0(), q.y0() + (FONT_HEIGHT + descent));
                    ufg.xadvance(advance.get(0) * scale);
                    ufg.uv(0).set(q.s0(), q.t0());
                    ufg.uv(1).set(q.s1(), q.t1());
                }
            })
            .texture(it -> it
                .id(fontTexID));

        nk_style_set_font(nkContext, default_font);    
    }
    
    public void setupStyle() {
        try (MemoryStack stack = stackPush()) {

            NkColor normalFGColour = NkColor.mallocStack(stack);
            normalFGColour.r((byte)255).g((byte)255).b((byte)255).a((byte)160);
            NkColor normalBGColour = NkColor.mallocStack(stack);
            normalBGColour.r((byte)96).g((byte)96).b((byte)96).a((byte)160);
            NkColor hoverFGColour = NkColor.mallocStack(stack);
            hoverFGColour.r((byte)255).g((byte)255).b((byte)255).a((byte)255);
            NkColor hoverBGColour = NkColor.mallocStack(stack);
            hoverBGColour.r((byte)128).g((byte)128).b((byte)128).a((byte)255);
            NkColor activeFGColour = NkColor.mallocStack(stack);
            activeFGColour.r((byte)64).g((byte)64).b((byte)64).a((byte)255);
            NkColor activeBGColour = NkColor.mallocStack(stack);
            activeBGColour.r((byte)192).g((byte)192).b((byte)192).a((byte)255);

            NkColor translucentLightGrey = NkColor.mallocStack(stack);
            translucentLightGrey.r((byte)96).g((byte)96).b((byte)96).a((byte)160);
            NkColor translucentMidGrey = NkColor.mallocStack(stack);
            translucentMidGrey.r((byte)64).g((byte)64).b((byte)64).a((byte)64);
            
            nkContext.style().text().color().set(normalFGColour);
    
            NkStyleWindow windowStyle = nkContext.style().window();
            windowStyle.fixed_background().data().color().set(translucentMidGrey);
            windowStyle.padding().x(1.0f);
            windowStyle.padding().y(1.0f);
            windowStyle.group_padding().x(1.0f);
            windowStyle.group_padding().y(1.0f);
            
            NkStyleWindowHeader headerStyle = windowStyle.header();
            headerStyle.active().type(NK_STYLE_ITEM_COLOR);
            headerStyle.active().data().color().set(translucentLightGrey);
            headerStyle.label_active().set(normalFGColour);
            headerStyle.minimize_button().normal().type(NK_STYLE_ITEM_COLOR);
            headerStyle.minimize_button().normal().data().color().set(normalBGColour);
            headerStyle.minimize_button().active().type(NK_STYLE_ITEM_COLOR);
            headerStyle.minimize_button().active().data().color().set(activeBGColour);
            headerStyle.minimize_button().hover().type(NK_STYLE_ITEM_COLOR);
            headerStyle.minimize_button().hover().data().color().set(hoverBGColour);

            NkStyleProperty propertyStyle = nkContext.style().property();
            propertyStyle.label_normal().set(normalFGColour);
            propertyStyle.label_hover().set(hoverFGColour);
            propertyStyle.label_active().set(activeFGColour);
            propertyStyle.edit().text_normal().set(normalFGColour);
            propertyStyle.edit().text_hover().set(hoverFGColour);
            propertyStyle.edit().text_active().set(activeFGColour);
            propertyStyle.edit().active().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.edit().active().data().color().set(activeBGColour);
            propertyStyle.edit().hover().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.edit().hover().data().color().set(hoverBGColour);
            propertyStyle.edit().normal().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.edit().normal().data().color().set(normalBGColour);
            propertyStyle.sym_left(NK_SYMBOL_MINUS);
            propertyStyle.sym_right(NK_SYMBOL_PLUS);
            propertyStyle.dec_button().normal().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.dec_button().normal().data().color().set(normalFGColour);
            propertyStyle.dec_button().hover().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.dec_button().hover().data().color().set(hoverFGColour);
            propertyStyle.dec_button().active().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.dec_button().active().data().color().set(activeFGColour);
            propertyStyle.inc_button().normal().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.inc_button().normal().data().color().set(normalFGColour);
            propertyStyle.inc_button().hover().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.inc_button().hover().data().color().set(hoverFGColour);
            propertyStyle.inc_button().active().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.inc_button().active().data().color().set(activeFGColour);
            propertyStyle.active().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.active().data().color(activeFGColour);
            propertyStyle.hover().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.hover().data().color().set(hoverBGColour);
            propertyStyle.normal().type(NK_STYLE_ITEM_COLOR);
            propertyStyle.normal().data().color().set(normalBGColour);

            NkStyleButton buttonStyle = nkContext.style().button();
            buttonStyle.active().type(NK_STYLE_ITEM_COLOR);
            buttonStyle.active().data().color().set(activeBGColour);
            buttonStyle.hover().type(NK_STYLE_ITEM_COLOR);
            buttonStyle.hover().data().color().set(hoverBGColour);
            buttonStyle.normal().type(NK_STYLE_ITEM_COLOR);
            buttonStyle.normal().data().color().set(normalBGColour);
            buttonStyle.text_active().set(activeFGColour);
            buttonStyle.text_hover().set(hoverFGColour);
            buttonStyle.text_normal().set(normalFGColour);

            NkStyleToggle checkboxStyle = nkContext.style().checkbox();
            checkboxStyle.border(1.0f);
            checkboxStyle.border_color().set(normalFGColour);
            checkboxStyle.text_active().set(activeFGColour);
            checkboxStyle.text_hover().set(hoverFGColour);
            checkboxStyle.text_normal().set(normalFGColour);

            NkStyleEdit editStyle = nkContext.style().edit();
            editStyle.text_normal().set(normalFGColour);
            editStyle.text_hover().set(hoverFGColour);
            editStyle.text_active().set(activeFGColour);
            editStyle.active().type(NK_STYLE_ITEM_COLOR);
            editStyle.active().data().color().set(activeBGColour);
            editStyle.hover().type(NK_STYLE_ITEM_COLOR);
            editStyle.hover().data().color().set(hoverBGColour);
            editStyle.normal().type(NK_STYLE_ITEM_COLOR);
            editStyle.normal().data().color().set(normalBGColour);
        }
    }
    
    public void setToolSet(Set<Integer> toolSet) {
        guiRenderer.setToolSet(toolSet);
    }

    public void setTypeSet(Set<String> typeSet) {
        guiRenderer.setTypeSet(typeSet);
    }

    public void setLines(List<String> lines) {
        guiRenderer.setLines(lines);
    }

    public void setLayerMap(Map<Integer, LayerDetails> layerMap) {
        guiRenderer.setLayerMap(layerMap);
    }

public void render() {
        guiShader.start();
        guiRenderer.render();
        guiShader.stop();
    }
    
    public void cleanUp() {
        guiRenderer.cleanUp();
        guiShader.cleanUp();
    }
    
    public boolean overGuiPanel(int x, int y) {
        return guiRenderer.pointOverGuiPanel(x, y);
    }
    
    public void onScroll(long window, double xoffset, double yoffset) {
        try (MemoryStack stack = stackPush()) {
            NkVec2 scroll = NkVec2.mallocStack(stack)
                .x((float)xoffset)
                .y((float)yoffset);
            nk_input_scroll(nkContext, scroll);
            renderParameters.setRenderRequired();
        }
    }

    public void onChar(long window, int codePoint) {
        nk_input_unicode(nkContext, codePoint);
        renderParameters.setRenderRequired();
    }

    public void onKey(long window, int key, int scancode, int action, int mods) {
        boolean press = (action == GLFW_PRESS);

        switch (key) {
            case GLFW_KEY_A:
                if (action == GLFW_PRESS &&
                    ((mods & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL ||
                     (mods & GLFW_MOD_SUPER) == GLFW_MOD_SUPER))
                    renderParameters.setAllLayersToRender();
                break;
            case GLFW_KEY_R:
                if (action == GLFW_PRESS &&
                    ((mods & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL ||
                     (mods & GLFW_MOD_SUPER) == GLFW_MOD_SUPER))
                    renderParameters.setViewResetRequired();
                break;
            case GLFW_KEY_DELETE:
                nk_input_key(nkContext, NK_KEY_DEL, press);
                break;
            case GLFW_KEY_ENTER:
                nk_input_key(nkContext, NK_KEY_ENTER, press);
                break;
            case GLFW_KEY_TAB:
                nk_input_key(nkContext, NK_KEY_TAB, press);
                break;
            case GLFW_KEY_BACKSPACE:
                nk_input_key(nkContext, NK_KEY_BACKSPACE, press);
                break;
            case GLFW_KEY_UP:
            case GLFW_KEY_DOWN:
                if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                    int step = 1;
                    if ((mods & GLFW_MOD_CONTROL) == GLFW_MOD_CONTROL || // Control key on Windows and Linux
                         (mods & GLFW_MOD_SUPER) == GLFW_MOD_SUPER) // Command key on MacOS.
                        step = 5;
                    if ((mods & GLFW_MOD_SHIFT) == GLFW_MOD_SHIFT) {
                        if (key == GLFW_KEY_UP) {
                            if ((mods & (GLFW_MOD_CONTROL | GLFW_MOD_ALT | GLFW_MOD_SUPER)) == GLFW_MOD_ALT) // ALT key and not CONTROL and not SUPER
                                renderParameters.setBottomLayerToRender(renderParameters.getTopLayerToRender());
                            else
                                renderParameters.setBottomLayerToRender(renderParameters.getBottomLayerToRender() + step);
                        }
                        else {
                            if ((mods & (GLFW_MOD_CONTROL | GLFW_MOD_ALT | GLFW_MOD_SUPER)) == GLFW_MOD_ALT) // ALT key and not CONTROL and not SUPER
                                renderParameters.setBottomLayerToRender(renderParameters.getIndexOfBottomLayer());
                            else
                                renderParameters.setBottomLayerToRender(renderParameters.getBottomLayerToRender() - step);                            
                        }
                    }
                    else {
                        if (key == GLFW_KEY_UP) {
                            if ((mods & (GLFW_MOD_CONTROL | GLFW_MOD_ALT | GLFW_MOD_SUPER)) == GLFW_MOD_ALT) // ALT key and not CONTROL and not SUPER
                                renderParameters.setTopLayerToRender(renderParameters.getIndexOfTopLayer());
                            else
                                renderParameters.setTopLayerToRender(renderParameters.getTopLayerToRender() + step);
                        }
                        else {
                            if ((mods & (GLFW_MOD_CONTROL | GLFW_MOD_ALT | GLFW_MOD_SUPER)) == GLFW_MOD_ALT) // ALT key and not CONTROL and not SUPER
                                renderParameters.setTopLayerToRender(renderParameters.getBottomLayerToRender());
                            else
                                renderParameters.setTopLayerToRender(renderParameters.getTopLayerToRender() - step);
                        }
                    }
                }
//                nk_input_key(nkContext, NK_KEY_UP, press);
                break;
//            case GLFW_KEY_DOWN:
//                nk_input_key(nkContext, NK_KEY_DOWN, press);
//                break;
            case GLFW_KEY_HOME:
                nk_input_key(nkContext, NK_KEY_TEXT_START, press);
                nk_input_key(nkContext, NK_KEY_SCROLL_START, press);
                break;
            case GLFW_KEY_END:
                nk_input_key(nkContext, NK_KEY_TEXT_END, press);
                nk_input_key(nkContext, NK_KEY_SCROLL_END, press);
                break;
            case GLFW_KEY_PAGE_DOWN:
                nk_input_key(nkContext, NK_KEY_SCROLL_DOWN, press);
                break;
            case GLFW_KEY_PAGE_UP:
                nk_input_key(nkContext, NK_KEY_SCROLL_UP, press);
                break;
            case GLFW_KEY_LEFT:
                nk_input_key(nkContext, NK_KEY_LEFT, press);
                break;
            case GLFW_KEY_RIGHT:
                nk_input_key(nkContext, NK_KEY_RIGHT, press);
                break;
            case GLFW_KEY_LEFT_SHIFT:
            case GLFW_KEY_RIGHT_SHIFT:
                if (action != GLFW_REPEAT) {
                    nk_input_key(nkContext, NK_KEY_SHIFT, press);
                }
                break;
            case GLFW_KEY_LEFT_CONTROL:
            case GLFW_KEY_RIGHT_CONTROL:
                if (action != GLFW_REPEAT) {
                    nk_input_key(nkContext, NK_KEY_CTRL, press);
                }
//                if (press) {
//                    nk_input_key(nkContext, NK_KEY_COPY, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_PASTE, glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_CUT, glfwGetKey(window, GLFW_KEY_X) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_TEXT_UNDO, glfwGetKey(window, GLFW_KEY_Z) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_TEXT_REDO, glfwGetKey(window, GLFW_KEY_R) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_TEXT_LINE_START, glfwGetKey(window, GLFW_KEY_B) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_TEXT_LINE_END, glfwGetKey(window, GLFW_KEY_E) == GLFW_PRESS);
//                } else {
//                    nk_input_key(nkContext, NK_KEY_LEFT, glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_RIGHT, glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS);
//                    nk_input_key(nkContext, NK_KEY_COPY, false);
//                    nk_input_key(nkContext, NK_KEY_PASTE, false);
//                    nk_input_key(nkContext, NK_KEY_CUT, false);
//                    nk_input_key(nkContext, NK_KEY_SHIFT, false);
//                }
                break;
        }
        renderParameters.setRenderRequired();
    }

    public void onCursorPos(long window, double x, double y) {
        nk_input_motion(nkContext, (int)x, (int)y);
        renderParameters.setRenderRequired();
    }
    
    public void onMouseButton(long window, double x, double y, int mouseButton, int action, int mods) {
        int nkButton;
        switch (mouseButton) {
            case GLFW_MOUSE_BUTTON_RIGHT:
                nkButton = NK_BUTTON_RIGHT;
                break;
            case GLFW_MOUSE_BUTTON_MIDDLE:
                nkButton = NK_BUTTON_MIDDLE;
                break;
            default:
                nkButton = NK_BUTTON_LEFT;
        }

        nk_input_button(nkContext, nkButton, (int)x, (int)y, action == GLFW_PRESS);
        renderParameters.setRenderRequired();
    }
    
    public void pollEvents(long windowId) {
        nk_input_begin(nkContext);
        double currentTime = glfwGetTime();
        boolean animating = renderParameters.getAnimationMode() != RenderParameters.AnimationMode.PAUSE;
        if (renderParameters.getRenderRequired() || 
                (animating && nextFrameTime <= currentTime)) {
            glfwPollEvents(); // Do not wait as we need to go around the render loop again.
        }
        else if (renderParameters.getAnimationMode() != RenderParameters.AnimationMode.PAUSE) {
            glfwWaitEventsTimeout(nextFrameTime - currentTime);
        }        
        else {
            glfwWaitEvents();
        }        
        currentTime = glfwGetTime();
        if (animating) {
            if (nextFrameTime <= currentTime) {
                while (nextFrameTime <= currentTime)
                    nextFrameTime +=  animationFrameInterval;
                // IS THIS THE CORRECT PLACE FOR THIS?
                int startLine = renderParameters.getBottomVisibleLine();
                int endLine = renderParameters.getNumberOfLines() - 1;
                if (renderParameters.getShowOnlySelected() &&
                    renderParameters.getFirstSelectedLine() != renderParameters.getLastSelectedLine()) {
                    if (startLine <  renderParameters.getFirstSelectedLine())
                        startLine = renderParameters.getFirstSelectedLine();
                    if (endLine > renderParameters.getLastSelectedLine())
                        endLine = renderParameters.getLastSelectedLine();
                    if (startLine > renderParameters.getLastSelectedLine())
                    {
                        // The current selection is below the bottom visible line so is not visible,
                        renderParameters.setAnimationMode(RenderParameters.AnimationMode.PAUSE);
                    }
                }
                if (renderParameters.getAnimationMode() != RenderParameters.AnimationMode.PAUSE) {
                    int topLine = renderParameters.getTopVisibleLine();

                    switch (renderParameters.getAnimationMode()) {
                        case BACKWARD_PLAY:
                            topLine -= animationFrameStep;
                            break;

                        case BACKWARD_FAST:
                            if (renderParameters.getLayerMap() == null || renderParameters.getLayerMap().isEmpty()) {
                                topLine -= animationFastFactor * animationFrameStep;
                            }
                            else {
                                int layer = renderParameters.getLayerIndexForLine(topLine);
                                if (layer > renderParameters.getIndexOfBottomLayer())
                                    topLine = renderParameters.getLineIndexForLayer(layer - 1, true);
                                else
                                    topLine = startLine;
                            }
                            break;

                        case FORWARD_PLAY:
                            topLine += animationFrameStep;
                            break;

                        case FORWARD_FAST:
                            if (renderParameters.getLayerMap() == null || renderParameters.getLayerMap().isEmpty()) {
                               topLine += animationFastFactor * animationFrameStep;
                            }
                            else {
                                int layer = renderParameters.getLayerIndexForLine(topLine);
                                if (layer < renderParameters.getIndexOfTopLayer())
                                    topLine = renderParameters.getLineIndexForLayer(layer + 1, true);
                                else
                                    topLine = endLine;
                            }
                            topLine += animationFrameStep;
                            break;

                        default:
                            break;
                    }
                    if (topLine >= endLine) {
                        topLine = endLine;
                        if (renderParameters.getAnimationMode() == RenderParameters.AnimationMode.FORWARD_PLAY ||
                                renderParameters.getAnimationMode() == RenderParameters.AnimationMode.FORWARD_FAST)
                            renderParameters.setAnimationMode(RenderParameters.AnimationMode.PAUSE);
                    }
                    if (topLine <= startLine) {
                        topLine = startLine;
                        if (renderParameters.getAnimationMode() == RenderParameters.AnimationMode.BACKWARD_PLAY ||
                                renderParameters.getAnimationMode() == RenderParameters.AnimationMode.BACKWARD_FAST)
                            renderParameters.setAnimationMode(RenderParameters.AnimationMode.PAUSE);
                    }
                    if (renderParameters.getTopVisibleLine() != topLine) {
                        renderParameters.setTopVisibleLine(topLine);
                    }
                }
            }
        }
        else {
           nextFrameTime = currentTime; 
        }

        // This is copied from the LWJGL demo and seems to be a bit
        // of boiler plate code that hides the mouse pointer when
        // dragging.
        NkMouse mouse = nkContext.input().mouse();
        if (mouse.grab()) {
            glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            renderParameters.setRenderRequired();
        }
        else if (mouse.grabbed()) {
            float prevX = mouse.prev().x();
            float prevY = mouse.prev().y();
            glfwSetCursorPos(windowId, prevX, prevY);
            mouse.pos().x(prevX);
            mouse.pos().y(prevY);
            renderParameters.setRenderRequired();
        }
        else if (mouse.ungrab()) {
            glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            renderParameters.setRenderRequired();
        }

        nk_input_end(nkContext);
    }
    
    public void setRenderRequired() {
        renderParameters.setRenderRequired();
    }
    
    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   the resource to read
     * @param bufferSize the initial buffer size
     *
     * @return the resource data
     *
     * @throws IOException if an IO error occurs
     */
    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) {
                    ;
                }
            }
        } else {
            try (
                InputStream source = GUIManager.class.getResourceAsStream(resource);
                ReadableByteChannel rbc = Channels.newChannel(source)
            ) {
                buffer = createByteBuffer(bufferSize);

                while (true) {
                    int bytes = rbc.read(buffer);

                    if (bytes == -1) {
                        break;
                    }

                    if (buffer.remaining() == 0) {
                        buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
                    }
                }
            }
        }

        buffer.flip();

        return buffer.slice();
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);

        return newBuffer;
    }
}
