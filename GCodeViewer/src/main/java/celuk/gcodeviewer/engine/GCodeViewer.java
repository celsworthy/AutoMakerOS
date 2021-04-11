package celuk.gcodeviewer.engine;

import celuk.gcodeviewer.comms.CommandHandler;
import celuk.language.I18n;
import com.beust.jcommander.JCommander;
import java.io.File;
import java.io.InputStream;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.Optional;
import libertysystems.configuration.ConfigNotLoadedException;

import libertysystems.configuration.Configuration;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.ARBDebugOutput.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

/**
 * Main entry point for the program. Window initialisation happens here.
 * 
 * @author George Salter
 */
public class GCodeViewer {

    private static final Stenographer STENO = StenographerFactory.getStenographer(GCodeViewer.class.getName());
    
    private static final String PROGRAM_NAME = "G-Code Viewer";
    private static final String WINDOW_ICON_PATH = "/resources/GCodeViewerIcon_#x#.png";
    private GCodeViewerCommandLineArgs commandLineArgs = null;
    private GCodeViewerConfiguration configuration = null;
    private GCodeViewerGUIConfiguration guiConfiguration = null;
    private long hResizeCursor = 0;
    private long windowId;
    private int windowX = -1;
    private int windowY = -1;
    private int windowWidth = -1;
    private int windowHeight = -1;
    
    public CommandHandler commandHandler;
    
    /**
     * Run the program 
     */
    public void run(GCodeViewerCommandLineArgs commandLineArgs) {
        System.out.println("Hello!");
        StenographerFactory.changeAllLogLevels(libertysystems.stenographer.LogLevel.INFO);
        STENO.debug("Running " + PROGRAM_NAME);
        
        this.commandLineArgs = commandLineArgs;
        configuration = GCodeViewerConfiguration.loadFromJSON(commandLineArgs.configDirectory);
        configuration.setExtruderLetterD(commandLineArgs.extruderLetterD);
        configuration.setExtruderLetterE(commandLineArgs.extruderLetterE);
        if (!commandLineArgs.nozzleValves.isEmpty()) {
            configuration.setHasNozzleValves(commandLineArgs.nozzleValves.equalsIgnoreCase("ON"));
        }
        
        guiConfiguration = GCodeViewerGUIConfiguration.loadFromJSON(commandLineArgs.projectDirectory);
        
        I18n.loadMessages(configuration.getApplicationInstallDirectory(),
                          I18n.getDefaultApplicationLocale(commandLineArgs.languageTag));
        
        init();
        loop();
        shutdown();
            
        System.out.println("Goodbye!");
    }

    /**
     * Initialisation happens here, this mostly encompasses GLFW and the 
     * associated window.
     */
    private void init() {
        
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        
        determineWindowDimensions();

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // OpenGL V3.3 or higher needed for geometry shader.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, (commandLineArgs.windowResizable ? GLFW_TRUE : GLFW_FALSE)); // the window will be resizable
        glfwWindowHint(GLFW_DECORATED, (commandLineArgs.windowDecorated ? GLFW_TRUE : GLFW_FALSE)); // the window will be decorated
        glfwWindowHint(GLFW_FLOATING, (commandLineArgs.windowAlwaysOnTop || configuration.getWindowAlwaysOnTop() ? GLFW_TRUE : GLFW_FALSE)); // the window will stay on top.
        windowId = glfwCreateWindow(windowWidth, windowHeight, I18n.t("window.title"), NULL, NULL);
        if ( windowId == NULL ) {
            throw new RuntimeException("Unable to create the GLFW window");
        }
        
        hResizeCursor = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Set window position to the specified position after creation because it is created at a default position.
        glfwSetWindowPos(windowId, windowX, windowY);

        loadWindowIcon();

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowId);

        // Enable v-sync
        glfwSwapInterval(1);
        
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GLCapabilities caps = GL.createCapabilities();
        Callback debugProc = GLUtil.setupDebugMessageCallback();
        
        if (caps.OpenGL43) {
            GL43.glDebugMessageControl(GL43.GL_DEBUG_SOURCE_API,
                                       GL43.GL_DEBUG_TYPE_OTHER,
                                       GL43.GL_DEBUG_SEVERITY_NOTIFICATION,
                                       (IntBuffer)null,
                                       false);
        }
        else if (caps.GL_KHR_debug) {
            KHRDebug.glDebugMessageControl(KHRDebug.GL_DEBUG_SOURCE_API,
                                           KHRDebug.GL_DEBUG_TYPE_OTHER,
                                           KHRDebug.GL_DEBUG_SEVERITY_NOTIFICATION,
                                           (IntBuffer)null,
                                           false);
        }
        else if (caps.GL_ARB_debug_output) {
            glDebugMessageControlARB(GL_DEBUG_SOURCE_API_ARB,
                                     GL_DEBUG_TYPE_OTHER_ARB,
                                     GL_DEBUG_SEVERITY_LOW_ARB,
                                     (IntBuffer)null,
                                     false);
        }
        
        // Make the window visible
        glfwShowWindow(windowId);
    }
    
    private void determineWindowDimensions() {
        // Sort out the window position and size.
        // Get the resolution of the primary monitor
        // Get the thread stack and push a new frame
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        if (vidmode != null)
        {
            if (commandLineArgs.windowNormalised) {
                if (commandLineArgs.windowX > 0.0)
                    commandLineArgs.windowX *= vidmode.width();
                if (commandLineArgs.windowY > 0.0)
                    commandLineArgs.windowY *= vidmode.height();
                if (commandLineArgs.windowWidth > 0.0)
                    commandLineArgs.windowWidth *= vidmode.width();
                if (commandLineArgs.windowHeight > 0.0)
                    commandLineArgs.windowHeight *= vidmode.height();
            }
            
            if (commandLineArgs.windowWidth <= 0.0)
                commandLineArgs.windowWidth = 0.5 * vidmode.width();
            if (commandLineArgs.windowHeight <= 0.0)
                commandLineArgs.windowHeight = 0.5 * vidmode.height();
            if (commandLineArgs.windowCentered || commandLineArgs.windowX < 0.0)
                commandLineArgs.windowX = 0.5 * (vidmode.width() - commandLineArgs.windowWidth);
            if (commandLineArgs.windowCentered || commandLineArgs.windowY < 0.0)
                commandLineArgs.windowY = 0.5 * (vidmode.height() - commandLineArgs.windowHeight);
        }
        else
        {
            if (commandLineArgs.windowWidth <= 0.0)
                commandLineArgs.windowWidth = 640.0;
            if (commandLineArgs.windowHeight <= 0.0)
                commandLineArgs.windowHeight = 480.0;
            if (commandLineArgs.windowCentered || commandLineArgs.windowX < 0.0)
                commandLineArgs.windowX = 5.0;
            if (commandLineArgs.windowCentered || commandLineArgs.windowY < 0.0)
                commandLineArgs.windowY = 55.0;
        }
        
        windowWidth = (int)Math.round(commandLineArgs.windowWidth);
        windowHeight = (int)Math.round(commandLineArgs.windowHeight);
        windowX = (int)Math.round(commandLineArgs.windowX);
        windowY = (int)Math.round(commandLineArgs.windowY);
    }
    
    private void loop() {
        RenderingEngine renderingEngine = new RenderingEngine(windowId,
                                                              windowWidth,
                                                              windowHeight,
                                                              windowX,
                                                              windowY,
                                                              hResizeCursor,
                                                              commandLineArgs.showAdvancedOptions,
                                                              commandLineArgs.printerType,
                                                              configuration,
                                                              guiConfiguration);
        renderingEngine.start(commandLineArgs.gCodeFile
                                             .stream()
                                             .map(File::toString)
                                             .findFirst()
                                             .orElse(""));
    }

    private void shutdown() {
        // Free the window callbacks and destroy the window
        hResizeCursor = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
        
        glfwDestroyCursor(hResizeCursor);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        guiConfiguration.saveToJSON(commandLineArgs.projectDirectory.toString());
    }
    
    // At Java9, ioResourceToByteBuffer can be replaced by the following:
    //
    // InputStream source = GCodeViewer.class.getResourceAsStream(resource);
    // byte[] bytes = is.readAllBytes()
    // ByteBuffer buffer = ByteBuffer.wrap(bytes)
    //    
    private ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) {
        ByteBuffer buffer = null;

        try (InputStream source = GCodeViewer.class.getResourceAsStream(resource);
             ReadableByteChannel rbc = Channels.newChannel(source)) {

            buffer = BufferUtils.createByteBuffer(bufferSize);

            while (true) {
                int bytes = rbc.read(buffer);
                if (bytes == -1) {
                    break;
                }
                if (buffer.remaining() == 0) {
                    ByteBuffer newBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 3 / 2);
                    buffer.flip();
                    newBuffer.put(buffer);
                    buffer = newBuffer; // 50%
                }
            }
        } catch (Exception e) {
        }

        if (buffer != null) {
            buffer.flip();
            return buffer.slice();
        }
        else
            return null;
    }

    private void loadWindowIcon() {
        
        ByteBuffer icon32 = ioResourceToByteBuffer(WINDOW_ICON_PATH.replaceAll("#", "32"), 32*32*4);
        ByteBuffer icon256 = ioResourceToByteBuffer(WINDOW_ICON_PATH.replaceAll("#", "256"), 256*256*4);
        if (icon32 != null && icon256 != null ) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer comp = stack.mallocInt(1);
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);

                GLFWImage.Buffer icons = GLFWImage.malloc(2);
                ByteBuffer pixels32 = stbi_load_from_memory(icon32, w, h, comp, 4);
                icons.position(0)
                     .width(w.get(0))
                     .height(h.get(0))
                     .pixels(pixels32);
                ByteBuffer pixels256 = stbi_load_from_memory(icon256, w, h, comp, 4);
                icons.position(1)
                     .width(w.get(0))
                     .height(h.get(0))
                     .pixels(pixels256);
                icons.position(0);
                glfwSetWindowIcon(windowId, icons);
            }
        }
    }
    
    /**
     * Start of the program.
     * 
     * @param argv 
     */
    public static void main(String[] argv) {
        GCodeViewerCommandLineArgs commandLineArgs = new GCodeViewerCommandLineArgs();
        new JCommander(commandLineArgs).parse(argv);
        GCodeViewer viewer = new GCodeViewer();
        viewer.run(commandLineArgs);
    }
}
