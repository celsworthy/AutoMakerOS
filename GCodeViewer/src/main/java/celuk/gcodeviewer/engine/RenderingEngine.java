package celuk.gcodeviewer.engine;

import celuk.gcodeviewer.comms.CommandHandler;
import celuk.gcodeviewer.engine.renderers.MasterRenderer;
import celuk.gcodeviewer.entities.Camera;
import celuk.gcodeviewer.entities.CenterPoint;
import celuk.gcodeviewer.entities.Entity;
import celuk.gcodeviewer.entities.Floor;
import celuk.gcodeviewer.entities.Light;
import celuk.gcodeviewer.entities.PrintVolume;
import celuk.gcodeviewer.gui.GUIManager;
import celuk.language.I18n;
import java.io.File;
import java.nio.IntBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.GLFW_FLOATING;
import static org.lwjgl.glfw.GLFW.GLFW_ICONIFIED;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwGetWindowAttrib;
import static org.lwjgl.glfw.GLFW.glfwHideWindow;
import static org.lwjgl.glfw.GLFW.glfwIconifyWindow;
import static org.lwjgl.glfw.GLFW.glfwRestoreWindow;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import org.lwjgl.util.nfd.NativeFileDialog;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.PointerBuffer;

/**
 *
 * @author George Salter
 */
public class RenderingEngine {
    
    private static final double FPS_UPDATE_INTERVAL = 1.0; // 1 update per second.
    
    private final static float MINIMUM_STEP = 0.0005f;
    private final static Stenographer STENO = StenographerFactory.getStenographer(
            RenderingEngine.class.getName());
    
    private final long windowId;
    
    private float printVolumeWidth;
    private float printVolumeHeight;
    private float printVolumeDepth;
    private float printVolumeOffsetX;
    private float printVolumeOffsetY;
    private float printVolumeOffsetZ;
    
    private RenderParameters renderParameters = new RenderParameters();
    private List<Entity> segments = null;
    private List<Entity> moves = null;
    private Camera camera = null;
    private Light light = null;

    private final MasterRenderer masterRenderer;
    private final GUIManager guiManager;
    
    private final ModelLoader modelLoader = new ModelLoader();
    private final ModelLoader floorLoader = new ModelLoader();

    private final SegmentLoader segmentLoader = new SegmentLoader();
    private final MoveLoader moveLoader = new MoveLoader();
    private final LineLoader lineLoader = new LineLoader();
    
    private final GCodeViewerConfiguration configuration;
    private final GCodeViewerGUIConfiguration guiConfiguration;

    private final CommandHandler commandHandler;

    private RawModel lineModel;
    
    private CenterPoint centerPoint = null;
    Floor floor = null;
    String printerType;
    PrintVolume printVolume = null;
    
    GCodeLoader fileLoader = null;
    String currentFilePath = null;

    private final double minDataValues[];
    private final double maxDataValues[];
    private final long hResizeCursor;
    
    private double previousTime = 0.0;
    private double updateDueTime = 0.0;
    private double frameTimeAccumulator = 0.0;
    private int nFrames = 0;

    public RenderingEngine(long windowId,
                           int windowWidth,
                           int windowHeight,
                           int windowXPos,
                           int windowYPos,
                           long hResizeCursor,
                           boolean showAdvancedOptions,
                           String printerType,
                           GCodeViewerConfiguration configuration,
                           GCodeViewerGUIConfiguration guiConfiguration) {
        this.windowId = windowId;
        this.configuration = configuration;
        this.guiConfiguration = guiConfiguration;
        this.printerType = printerType;
        this.hResizeCursor = hResizeCursor;
        renderParameters.setFromConfiguration(configuration);
        renderParameters.setFromGUIConfiguration(guiConfiguration);
        renderParameters.setWindowWidth(windowWidth);
        renderParameters.setWindowHeight(windowHeight);
        renderParameters.setWindowXPos(windowXPos);
        renderParameters.setWindowYPos(windowYPos);
        this.commandHandler = new CommandHandler();
        commandHandler.setRenderParameters(renderParameters);
        commandHandler.setRenderingEngine(this);
 
        masterRenderer = new MasterRenderer(renderParameters);
        guiManager = new GUIManager(windowId, showAdvancedOptions, configuration.getAnimationFrameInterval(), configuration.getAnimationFrameStep(), configuration.getAnimationFastFactor(), renderParameters);
        guiManager.setFromGUIConfiguration(guiConfiguration);
        lineModel = null;
        
        this.minDataValues = new double[Entity.N_DATA_VALUES];
        this.maxDataValues = new double[Entity.N_DATA_VALUES];
        for (int dataIndex = 0; dataIndex < Entity.N_DATA_VALUES; ++dataIndex)
        {
            this.minDataValues[dataIndex] = 0.0;
            this.maxDataValues[dataIndex] = 0.0;
        }
    }
    
    public void start(String gCodeFile) {      
        STENO.debug("Starting RenderingEngine.");

        createWindowResizeCallback();
        
        Vector3f lightPos = new Vector3f(configuration.getLightPosition().x(),
                                         configuration.getLightPosition().y(),
                                         configuration.getLightPosition().z());
        light = new Light(lightPos, configuration.getLightColour());

        lineModel = modelLoader.loadToVAO(new float[]{-0.5f, 0, 0, 0.5f, 0, 0});
        
        // Set printer type to null to force load of initial printer type.
        setPrinterType(printerType);
        
        glfwSetCharCallback(windowId, (window, codepoint) -> {
            guiManager.onChar(window, codepoint);
        });
        
        glfwSetKeyCallback(windowId, (window, key, scancode, action, mods) -> {
            guiManager.onKey(window, key, scancode, action, mods);
        });

        if (gCodeFile != null)
            startLoadingGCodeFile(gCodeFile);
        
        commandHandler.start();

        STENO.debug("Running rendering loop.");
        previousTime = glfwGetTime();
        boolean  frameRendered = false;
        while (!glfwWindowShouldClose(windowId)) {
            switch (renderParameters.getWindowAction()) {
                case WINDOW_RESTORE:
                    glfwRestoreWindow(windowId);
                    break;

                case WINDOW_ICONIFY:
                    glfwIconifyWindow(windowId);
                    break;

                case WINDOW_FOCUS:
                    if (glfwGetWindowAttrib(windowId, GLFW_ICONIFIED) == 0) {
                        
                        if (System.getProperty("os.name").startsWith("Windows") &&
                            glfwGetWindowAttrib(windowId, GLFW_FLOATING) == 0) {
                            // Calling glfwFocusWindow() once on Windows 10
                            // just flashes the toolbar icon without bringing it
                            // to the front. Waiting, then calling glfwFocusWindow()
                            // again does seem to bring it to the front.
                            glfwFocusWindow(windowId);
                            try {
                                Thread.sleep(50);
                            }
                            catch (InterruptedException ex) {
                                // Carry on!
                            }
                        }
                        glfwFocusWindow(windowId);
                    }
                    break;

                case WINDOW_SHOW:
                    glfwShowWindow(windowId);
                    break;
                    
                case WINDOW_HIDE:
                    glfwHideWindow(windowId);
                    break;

                case WINDOW_NO_ACTION:
                default:
                    break;                    
            }
            renderParameters.setWindowAction(RenderParameters.WindowAction.WINDOW_NO_ACTION);
            
            // Render the frame and update the frame timer.
            double startTime = glfwGetTime();
            frameRendered = renderFrame();
            if (frameRendered) {
                double currentTime = glfwGetTime();
                double iterationTime = currentTime - startTime;
                frameTimeAccumulator += iterationTime;
                ++nFrames;
                if (currentTime > updateDueTime) {
                    renderParameters.setFrameTime(frameTimeAccumulator / nFrames);
                    frameTimeAccumulator = 0.0;
                    nFrames = 0;
                    updateDueTime = currentTime + FPS_UPDATE_INTERVAL;
                }
            }
            
            if (renderParameters.getLoadGCodeRequested()) {
                renderParameters.clearLoadGCodeRequested();
                PointerBuffer outPath = memAllocPointer(1);

                try {
                    if (NativeFileDialog.NFD_OpenDialog("gcode", null, outPath) == NativeFileDialog.NFD_OKAY) {
                        String path = outPath.getStringUTF8();
                        startLoadingGCodeFile(path);
                    }
                } finally {
                    memFree(outPath);
                }
            }
            
            if (renderParameters.getReloadGCodeRequested()) {
                renderParameters.clearReloadGCodeRequested();
                startLoadingGCodeFile(getCurrentFilePath());
            }

            guiManager.pollEvents(windowId);
            
            if (commandHandler.processCommands())
                glfwSetWindowShouldClose(windowId, true);
            
            if (fileLoader != null && fileLoader.loadFinished())
                completeLoadingGCodeFile();

            
            // Not sure if this is strictly necessary.
            // However, if nothing is changing, this loop becomes effectively a busy wait.
            // To prevent this, make the system wait at least the MINIMUM_ITERATION_TIME before
            // starting the next iteration. Currently this would give a maximum of 100 frames per second, which
            // should be enough.
            //double remainingLoopTime = MINIMUM_ITERATION_TIME - iterationTime;
            //if (remainingLoopTime > 0.001) {
            //    try {
            //        Thread.sleep((long)(1000.0 * remainingLoopTime)); // convert seconds to milliseconds.
            //    }
            //    catch (InterruptedException ex) {
                    // Carry on!
            //    }
            //}
        }
        renderParameters.saveToGUIConfiguration(guiConfiguration);
        guiManager.saveToGUIConfiguration(guiConfiguration);
        commandHandler.stop();
        masterRenderer.cleanUp();
        guiManager.cleanUp();
        floorLoader.cleanUp();
        modelLoader.cleanUp();
        segmentLoader.cleanUp();
        moveLoader.cleanUp();
        lineLoader.cleanUp();
        fileLoader = null;
    }
    
    private boolean renderFrame() {
        boolean frameRendered = false;

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            glfwGetFramebufferSize(windowId, w, h);

            int displayWidth = w.get(0);
            int displayHeight = h.get(0);
            if (displayWidth != renderParameters.getDisplayWidth() ||
                displayHeight != renderParameters.getDisplayHeight()) {
                renderParameters.setDisplayWidth(w.get(0));
                renderParameters.setDisplayHeight(h.get(0));
                masterRenderer.createProjectionMatrix(displayWidth, displayHeight);
                masterRenderer.reloadProjectionMatrix();
            }
        }
        GL11.glViewport(0, 0, renderParameters.getDisplayWidth(), renderParameters.getDisplayHeight());

        if (renderParameters.getViewResetRequired())
        {
            PrintVolumeDetails printVolumeDetails = configuration.getPrintVolumeDetailsForType(printerType);
            Vector3f centerPointStartPos = new Vector3f(printVolumeOffsetX + 0.5f * printVolumeWidth, printVolumeOffsetY + 0.5f * printVolumeDepth, printVolumeOffsetZ + 0.5f * printVolumeHeight);
            camera.reset(centerPointStartPos, printVolumeDetails.getDefaultCameraDistance());
            renderParameters.clearViewResetRequired();
        }
        camera.move();

        // Set the light to the camera position so that it behaves like a headtorch on the camera.
        light.getPosition().set(camera.getPosition());

        renderParameters.checkLimits();

        if (renderParameters.getRenderRequired())
        {
            frameRendered = true;
            masterRenderer.render(camera, light);

            guiManager.render();

            glfwSwapInterval(1);
            glfwSwapBuffers(windowId); // swap the color buffers
            renderParameters.clearRenderRequired();
            if (renderParameters.getUseResizeCursor())
                glfwSetCursor(windowId, hResizeCursor);
            else
                glfwSetCursor(windowId, 0);
        }
        
        return frameRendered;
    }

    private void createWindowResizeCallback() {
        glfwSetWindowSizeCallback(windowId, (window, width, height) -> {
//            System.out.println("createWindowResizeCallback(" + Long.toHexString(window) + ", " + Integer.toString(width) + ", " + Integer.toString(height) + ")");
            renderParameters.setWindowWidth(width);
            renderParameters.setWindowHeight(height);
        });
        glfwSetWindowPosCallback(windowId, (window, xPos, yPos) -> {
//            System.out.println("glfwSetWindowPosCallback(" + Long.toHexString(window) + ", " + Integer.toString(xPos) + ", " + Integer.toString(yPos) + ")");
            renderParameters.setWindowXPos(xPos);
            renderParameters.setWindowYPos(yPos);
        });
        glfwSetFramebufferSizeCallback(windowId, (window, width, height) -> {
//            System.out.println("glfwSetFramebufferSizeCallback(" + Long.toHexString(window) + ", " + Integer.toString(width) + ", " + Integer.toString(height) + ")");
            renderParameters.setRenderRequired();
            renderFrame();
        });
    }

    public void startLoadingGCodeFile(String gCodeFile) {
        if (gCodeFile != null && !gCodeFile.isEmpty()) {
            fileLoader = new GCodeLoader(gCodeFile, renderParameters, configuration);
            fileLoader.start();
        }
    }
    
    private int processNullLayer(int numberOfBottomLayer, Map<Integer, LayerDetails> layerMap) {
        // There may be a NULL layer that contains the lines before the first layer.
        // If so, assign it the layer number before the bottom, so it becomes
        // the bottom layer.
        int nbl = numberOfBottomLayer;
        LayerDetails nullLayer = layerMap.getOrDefault(Entity.NULL_LAYER, null);
        if (nullLayer != null)
        {
            --nbl;
            layerMap.remove(Entity.NULL_LAYER);
            nullLayer.setLayerNumber(nbl);
            layerMap.put(nbl, nullLayer);
            
            Iterator<Entity> entityIterator = segments.iterator();
            while(entityIterator.hasNext()) {
                Entity s = entityIterator.next();
                if (s.getLayer() == Entity.NULL_LAYER)
                    s.setLayer(nbl);
                else
                    break;
            }

            entityIterator = moves.iterator();
            while(entityIterator.hasNext()) {
                Entity s = entityIterator.next();
                if (s.getLayer() == Entity.NULL_LAYER)
                    s.setLayer(nbl);
                else
                    break;
            }
        }
        return nbl;
    }

    public void completeLoadingGCodeFile() {
        if (fileLoader != null && fileLoader.loadFinished()) {
            try {
                if (fileLoader.loadSuccess())
                {
                    GCodeProcessor processor = fileLoader.getProcessor();
                    GCodeLineProcessor lineProcessor = fileLoader.getLineProcessor();

                    segments = lineProcessor.getSegments();
                    moves = lineProcessor.getMoves();
                    for (int dataIndex = 0; dataIndex < Entity.N_DATA_VALUES; ++dataIndex)
                    {
                        minDataValues[dataIndex] = lineProcessor.getMinDataValue(dataIndex);
                        maxDataValues[dataIndex] = lineProcessor.getMaxDataValue(dataIndex);
                    }

                    Map<String, Double> settingsMap = processor.getSettings();
                    double ejectVolume = settingsMap.getOrDefault("nozzle0_ejectionvolume", -1.0);
                    if (ejectVolume > 0.0)
                        renderParameters.setNozzleEjectVolumeForTool(0, ejectVolume);
                    ejectVolume = settingsMap.getOrDefault("nozzle1_ejectionvolume", -1.0);
                    if (ejectVolume > 0.0)
                        renderParameters.setNozzleEjectVolumeForTool(1, ejectVolume);

                    renderParameters.setNumberOfLines(processor.getLines().size());
                    renderParameters.setFirstSelectedLine(0);
                    renderParameters.setLastSelectedLine(0);
                    STENO.info("Number of lines = " + Integer.toString(renderParameters.getNumberOfLines()));

                    if (processor.getNumberOfTopLayer() > Entity.NULL_LAYER)
                        renderParameters.setIndexOfTopLayer(processor.getNumberOfTopLayer());
                    else
                        renderParameters.setIndexOfTopLayer(renderParameters.getNumberOfLines());
                    if (processor.getNumberOfBottomLayer() > Entity.NULL_LAYER) {
                        int nbl = processNullLayer(processor.getNumberOfBottomLayer(), lineProcessor.getLayerMap());
                        renderParameters.setIndexOfBottomLayer(nbl);
                    }
                    else
                        renderParameters.setIndexOfBottomLayer(0);
                    
                    renderParameters.setLayerMap(lineProcessor.getLayerMap());
                    renderParameters.setTopLayerToRender(renderParameters.getIndexOfTopLayer());
                    renderParameters.setBottomLayerToRender(renderParameters.getIndexOfBottomLayer());
                    guiManager.setToolSet(lineProcessor.getToolSet());
                    guiManager.setTypeSet(lineProcessor.getTypeSet());
                    guiManager.setLines(processor.getLines());
                    guiManager.setLayerMap(lineProcessor.getLayerMap());
                    currentFilePath = fileLoader.getFilePath();
                    
                    // Update the window title with the file name.
                    File f = new File(currentFilePath);
                    glfwSetWindowTitle(windowId, I18n.t("window.titleWithFileName").replaceAll("#1", f.getName()));
                }
            }
            catch (RuntimeException ex)
            {
                renderParameters.clearLinesAndLayer();
                STENO.error("Parsing error");
            }
            fileLoader = null;
            
            masterRenderer.clearEntities();
            segmentLoader.cleanUp();
            if (segments != null && segments.size() > 0) {
                masterRenderer.processSegmentEntity(segmentLoader.loadToVAO(segments));           
            }
            moveLoader.cleanUp();
            if (moves != null && moves.size() > 0) {
                masterRenderer.processMoveEntity(moveLoader.loadToVAO(moves));           
            }
        }
    }
    
    public String getCurrentFilePath() {
            return currentFilePath;
    }

    public void clearGCode() {
        renderParameters.clearLinesAndLayer();
        masterRenderer.clearEntities();
        segmentLoader.cleanUp();
        moveLoader.cleanUp();
    }

    public void reloadSegments() {
        masterRenderer.processSegmentEntity(null);
        segmentLoader.cleanUp();
        if (segments != null && segments.size() > 0) {
            masterRenderer.processSegmentEntity(segmentLoader.loadToVAO(segments));           
        }
    }
    
    public void reloadSegmentColours() {
        if (segments != null &&
            segments.size() > 0 &&
            masterRenderer.getSegmentEntity() != null) {
            segmentLoader.reloadColours(masterRenderer.getSegmentEntity(), segments);
            renderParameters.setRenderRequired();
        }
    }
        
    public void colourSegmentsFromType() {
        if (segments != null && segments.size() > 0) {
            segments.forEach(segment -> {
                segment.setColour(segment.getTypeColour());
            });
        }
    }
    
    public void colourSegmentsFromData(int dataIndex) {
        
        List<Vector3f> colourPalette = renderParameters.getDataColourPalette();
        if (dataIndex >= 0 &&
            dataIndex < Entity.N_DATA_VALUES &&
            colourPalette.size() > 0 &&
            minDataValues[dataIndex] < maxDataValues[dataIndex])
        {
            double minValue = minDataValues[dataIndex];
            double maxValue = maxDataValues[dataIndex];
            int nSteps = colourPalette.size();
            double span = maxValue - minValue;
            double step = span / nSteps;
            if (colourPalette.size() > 1 && step > MINIMUM_STEP) {
                segments.forEach(segment -> {
                    //System.out.println("Data[" + Integer.toString(dataIndex) + "] = " + Float.toString(segment.getDataValue(dataIndex)));
                    double index = (segment.getDataValue(dataIndex) - minValue) / step;
                    Vector3f segmentColour;
                    if (index < 1.0)
                        segmentColour = colourPalette.get(0);
                    else if (index >= nSteps)
                        segmentColour = colourPalette.get(nSteps - 1);
                    else
                        segmentColour = colourPalette.get((int)index);
                    segment.setColour(segmentColour);
                });
            }
            else {
                Vector3f segmentColour = colourPalette.get(0);
                segments.forEach(segment -> {
                   segment.setColour(segmentColour);
                });
            }
        }
        else {
            Vector3f defaultColour = renderParameters.getDefaultColour();
            segments.forEach(segment -> {
               segment.setColour(defaultColour);
            });
        }
    }
    
    public void setPrinterType(String printerType) {
        // Camera is null during initialisation, so this is a
        // sneaky way to force the printer type to be setup.
        if (camera == null || !this.printerType.equalsIgnoreCase(printerType)) {
            this.printerType = printerType.toUpperCase();
            PrintVolumeDetails printVolumeDetails = configuration.getPrintVolumeDetailsForType(printerType);
            this.printVolumeWidth = printVolumeDetails.getDimensions().x();
            this.printVolumeDepth = printVolumeDetails.getDimensions().y();
            this.printVolumeHeight = printVolumeDetails.getDimensions().z();
            this.printVolumeOffsetX = printVolumeDetails.getOffset().x();
            this.printVolumeOffsetY = printVolumeDetails.getOffset().y();
            this.printVolumeOffsetZ = printVolumeDetails.getOffset().z();

            Vector3f centerPointStartPos = new Vector3f(printVolumeOffsetX + 0.5f * printVolumeWidth, printVolumeOffsetY + 0.5f * printVolumeDepth, printVolumeOffsetZ + 0.5f * printVolumeHeight);
            centerPoint = new CenterPoint(centerPointStartPos, lineModel);
            camera = new Camera(windowId, centerPoint, printVolumeDetails.getDefaultCameraDistance(), guiManager);
            floorLoader.cleanUp();
            floor = new Floor(printVolumeWidth, printVolumeDepth, printVolumeOffsetX, printVolumeOffsetY, printVolumeOffsetZ, floorLoader);

            lineLoader.cleanUp();
            printVolume = new PrintVolume(lineLoader, printVolumeWidth, printVolumeDepth, printVolumeHeight, printVolumeOffsetX, printVolumeOffsetY, printVolumeOffsetZ);

            masterRenderer.processFloor(floor);
            masterRenderer.processCentrePoint(centerPoint);
            masterRenderer.processPrintVolume(printVolume);

            renderParameters.setRenderRequired();
        }
    }

    public boolean getHasNozzleValves() {
        return configuration.getHasNozzleValves();
    }

    public void setHasNozzleValves(boolean hasNozzleValves) {
        configuration.setHasNozzleValves(hasNozzleValves);
    }

    public char getExtruderLetterD() {
        return configuration.getExtruderLetterD();
    }

    public void setExtruderLetterD(String extruderLetter) {
        configuration.setExtruderLetterD(extruderLetter);
    }

    public char getExtruderLetterE() {
        return configuration.getExtruderLetterE();
    }

    public void setExtruderLetterE(String extruderLetter) {
        configuration.setExtruderLetterE(extruderLetter);
    }
}
