/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.gcodeviewer.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joml.Vector3f;

/**
 *
 * @author Tony
 */
public class RenderParameters {

    public static enum ColourMode {
        COLOUR_AS_TYPE,
        COLOUR_AS_DATA,
        COLOUR_AS_TOOL
    }
      
    public static enum WindowAction {
        WINDOW_NO_ACTION,
        WINDOW_FOCUS,
        WINDOW_HIDE,
        WINDOW_ICONIFY,
        WINDOW_RESTORE,
        WINDOW_SHOW
    }

    public static enum AnimationMode {
        PAUSE,
        FORWARD_PLAY,
        FORWARD_FAST,
        BACKWARD_PLAY,
        BACKWARD_FAST
    }

    public static int MAX_NUMBER_OF_TOOLS = 16;
     
    private int indexOfTopLayer = 0;
    private int indexOfBottomLayer = 0;
    private int topLayerToRender = 0;
    private int bottomLayerToRender = 0;
    
    private int numberOfLines = 0;
    private int firstSelectedLine = 0;
    private int lastSelectedLine = 0;
    private int topVisibleLine = 0;
    private int bottomVisibleLine = 0;
    
    private boolean showMoves = false;
    private boolean showAngles = false;
    private boolean showOnlySelected = false;
    private boolean showStylus = false;
    private AnimationMode animationMode = AnimationMode.PAUSE;
    private int showTools = 0xFFFF; 
    private ColourMode colourMode = ColourMode.COLOUR_AS_TYPE;
    private Vector3f moveColour = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f selectColour = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f stylusColour = new Vector3f(0.0f, 0.0f, 0.0f);
    private float stylusHeight = 0.0f;
    private List<Vector3f> toolColours = new ArrayList<>();
    private List<Vector3f> dataColourPalette = new ArrayList<>();
    private int showTypes = 0xFFFF; 
    private Map<String, Vector3f> typeColourMap = new HashMap<>();
    List<String> types = new ArrayList<>();
    private List<Vector3f> typeColours = new ArrayList<>();
    private Map<String, Integer> typeIndexMap = new HashMap<>();
    private Vector3f defaultColour = new Vector3f(0.0f, 0.0f, 0.0f);
    private List<Double> toolFilamentFactors = new ArrayList<>();
    private double defaultFilamentFactor = 0.0;
    private List<Double> toolNozzleEjectVolumes = new ArrayList<>();
    private double defaultNozzleEjectVolume = 0.0;
    private int displayWidth = 0;
    private int displayHeight = 0;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private int windowXPos = 0;
    private int windowYPos = 0;
    private double frameTime = 0.0;
    private boolean viewResetRequired = false;
    private boolean loadGCodeRequested = false;
    private boolean reloadGCodeRequested = false;
    private boolean useResizeCursor = false;
    private WindowAction windowAction = WindowAction.WINDOW_NO_ACTION;
    private Map<Integer, LayerDetails> layerMap = null;

    // Nuklear GUI requires 2 renders to update properly - the first updates the state
    // the second updates the GUI. Easy way to do this is it always set the render flag
    // to 2, and decrement by one when cleared.
    private int renderRequired = 2;

    RenderParameters(){
    }
    
    public void setFromConfiguration(GCodeViewerConfiguration configuration) {
        moveColour = configuration.getMoveColour();
        selectColour = configuration.getSelectColour();
        toolColours = configuration.getToolColours();
        setTypeColourMap(configuration.getTypeColourMap());
        dataColourPalette = configuration.getDataColourPalette();
        defaultColour = configuration.getDefaultColour();
        toolFilamentFactors = configuration.getToolFilamentFactors();
        defaultFilamentFactor = configuration.getDefaultFilamentFactor();
        toolNozzleEjectVolumes = configuration.getToolNozzleEjectVolumes();
        defaultNozzleEjectVolume = configuration.getDefaultNozzleEjectVolume();
        stylusHeight = configuration.getStylusHeight();
        stylusColour = configuration.getStylusColour();
    }
            
    public void setFromGUIConfiguration(GCodeViewerGUIConfiguration guiConfiguration) {
        this.topLayerToRender = guiConfiguration.getTopLayerToRender();
        this.bottomLayerToRender = guiConfiguration.getBottomLayerToRender();
        this.topVisibleLine = guiConfiguration.getTopVisibleLine();
        this.bottomVisibleLine = guiConfiguration.getBottomVisibleLine();
        this.firstSelectedLine = guiConfiguration.getFirstSelectedLine();
        this.lastSelectedLine = guiConfiguration.getLastSelectedLine();
        this.showAngles = guiConfiguration.getShowAngles();
        this.showMoves = guiConfiguration.getShowMoves();
        this.showStylus = guiConfiguration.getShowStylus();
        this.showOnlySelected = guiConfiguration.getShowOnlySelected();
        this.showTools = guiConfiguration.getShowTools(); 
        this.showTypes = guiConfiguration.getShowTypes(); 
        this.colourMode = guiConfiguration.getColourMode();
    }

    public void saveToGUIConfiguration(GCodeViewerGUIConfiguration guiConfiguration) {
        guiConfiguration.setTopLayerToRender(this.topLayerToRender);
        guiConfiguration.setTopVisibleLine(this.topVisibleLine);
        guiConfiguration.setBottomVisibleLine(this.bottomVisibleLine);
        guiConfiguration.setLastSelectedLine(this.lastSelectedLine);
        guiConfiguration.setFirstSelectedLine(this.firstSelectedLine);
        guiConfiguration.setLastSelectedLine(this.lastSelectedLine);
        guiConfiguration.setShowAngles(this.showAngles);
        guiConfiguration.setShowMoves(this.showMoves);
        guiConfiguration.setShowStylus(this.showStylus);
        guiConfiguration.setShowOnlySelected(this.showOnlySelected);
        guiConfiguration.setShowTools(this.showTools); 
        guiConfiguration.setShowTypes(this.showTypes); 
        guiConfiguration.setColourMode(this.colourMode);
    }

    public void clearLinesAndLayer() {
        numberOfLines = 0;
        firstSelectedLine = 0;
        lastSelectedLine = 0;

        indexOfTopLayer = 0;
        indexOfBottomLayer = 0;
        topLayerToRender = 0;
        bottomLayerToRender = 0;
        frameTime = 0.0;
        renderRequired = 2;
    }

    public Map<Integer, LayerDetails> getLayerMap() {
        return layerMap;
    }

    public void setLayerMap(Map<Integer, LayerDetails> layerMap) {
        this.layerMap = layerMap;
    }

    public int getIndexOfTopLayer() {
        return indexOfTopLayer;
    }

    public void setIndexOfTopLayer(int indexOfTopLayer) {
        this.indexOfTopLayer = indexOfTopLayer;
        renderRequired = 2;
    }

    public int getIndexOfBottomLayer() {
        return indexOfBottomLayer;
    }

    public void setIndexOfBottomLayer(int indexOfBottomLayer) {
        this.indexOfBottomLayer = indexOfBottomLayer;
        renderRequired = 2;
    }

    public int getTopLayerToRender() {
        return topLayerToRender;
    }

    public void setTopLayerToRender(int topLayerToRender) {
        if (this.topLayerToRender != topLayerToRender) {
            if (topLayerToRender > this.bottomLayerToRender)
                this.topLayerToRender = topLayerToRender;
            else
                this.topLayerToRender = this.bottomLayerToRender;
            this.topVisibleLine = this.topLayerToRender;
            if (this.layerMap != null && !layerMap.isEmpty()) {
                LayerDetails details = layerMap.get(this.topLayerToRender);
                if (details != null)
                    this.topVisibleLine =  details.getEndLine();
            }
            renderRequired = 2;
        }
    }

    public int getBottomLayerToRender() {
        return bottomLayerToRender;
    }

    public void setBottomLayerToRender(int bottomLayerToRender) {
        if (this.bottomLayerToRender != bottomLayerToRender) {
            if (bottomLayerToRender < this.topLayerToRender)
                this.bottomLayerToRender = bottomLayerToRender;
            else
                this.bottomLayerToRender = this.topLayerToRender;
            this.bottomVisibleLine = this.bottomLayerToRender;
            if (this.layerMap != null && !layerMap.isEmpty()) {
                LayerDetails details = layerMap.get(this.bottomLayerToRender);
                if (details != null)
                    this.bottomVisibleLine =  details.getStartLine();
            }
            renderRequired = 2;
        }
    }

    public void setAllLayersToRender() {
        setAllLinesVisible();
    }
 
    public void setNoLayersToRender() {
        setNoLinesVisible();
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines) {
        this.numberOfLines = numberOfLines;
        renderRequired = 2;
    }

    public int getFirstSelectedLine() {
        return firstSelectedLine;
    }

    public void setFirstSelectedLine(int firstSelectedLine) {
        if (this.firstSelectedLine != firstSelectedLine) {
            this.firstSelectedLine = firstSelectedLine;
            renderRequired = 2;
        }
    }

    public int getLastSelectedLine() {
        return lastSelectedLine;
    }

    public void setLastSelectedLine(int lastSelectedLine) {
        if (this.lastSelectedLine != lastSelectedLine) {
            this.lastSelectedLine = lastSelectedLine;
            renderRequired = 2;
        }
    }

    public void clearSelectedLines() {
        if (firstSelectedLine != lastSelectedLine)
            renderRequired = 2;
        firstSelectedLine = 0;
        lastSelectedLine = 0;
    }

	public int getTopVisibleLine() {
        return topVisibleLine;
    }

    public void setTopVisibleLine(int topVisibleLine) {
        if (this.topVisibleLine != topVisibleLine) {
            this.topVisibleLine = topVisibleLine;
            this.topLayerToRender = getLayerIndexForLine(this.topVisibleLine);
            renderRequired = 2;
        }
    }

    public int getBottomVisibleLine() {
        return bottomVisibleLine;
    }

    public void setBottomVisibleLine(int bottomVisibleLine) {
        if (this.bottomVisibleLine != bottomVisibleLine) {
            this.bottomVisibleLine = bottomVisibleLine;
            this.bottomLayerToRender = getLayerIndexForLine(this.bottomVisibleLine);

            renderRequired = 2;
        }
    }

    public void setAllLinesVisible() {
        topVisibleLine = numberOfLines - 1;
        bottomVisibleLine = 0;
        this.topLayerToRender = indexOfTopLayer;
        this.bottomLayerToRender = indexOfBottomLayer;
        renderRequired = 2;
    }

    public void setNoLinesVisible() {
        topVisibleLine = 0;
        bottomVisibleLine = 0;
        this.topLayerToRender = indexOfBottomLayer;
        this.bottomLayerToRender = indexOfBottomLayer;
        renderRequired = 2;
    }

    public boolean getShowAngles() {
        return showAngles;
    }

    public void setShowAngles(boolean showAngles) {
        if (this.showAngles != showAngles) {
            this.showAngles = showAngles;
            renderRequired = 2;
        }
    }

    public boolean getShowMoves() {
        return showMoves;
    }

    public void setShowMoves(boolean showMoves) {
        if (this.showMoves != showMoves) {
            this.showMoves = showMoves;
            renderRequired = 2;
        }
    }

    public boolean getShowOnlySelected() {
        return showOnlySelected;
    }

    public void setShowOnlySelected(boolean showOnlySelected) {
        if (this.showOnlySelected != showOnlySelected) {
            this.showOnlySelected = showOnlySelected;
            renderRequired = 2;
        }
    }

    public boolean getShowStylus() {
        return showStylus;
    }

    public void setShowStylus(boolean showStylus) {
        if (this.showStylus != showStylus) {
            this.showStylus = showStylus;
            renderRequired = 2;
        }
    }

    public AnimationMode getAnimationMode() {
        return animationMode;
    }

    public void setAnimationMode(AnimationMode mode) {
        if (this.animationMode != mode) {
            this.animationMode = mode;
            renderRequired = 2;
        }
    }

    public ColourMode getColourMode() {
        return colourMode;
    }

    public void setColourMode(ColourMode colourMode) {
        if (this.colourMode != colourMode) {
            this.colourMode = colourMode;
            renderRequired = 2;
        }
    }
    
    public Vector3f getMoveColour() {
        return moveColour;
    }

    public void setMoveColour(Vector3f moveColour) {
        this.moveColour = moveColour;
        renderRequired = 2;
    }
    
    public Vector3f getSelectColour() {
        return selectColour;
    }

    public void setSelectColour(Vector3f selectColour) {
        this.selectColour = selectColour;
        renderRequired = 2;
    }
    
    public Vector3f getStylusColour() {
        return stylusColour;
    }

    public void setStylusColour(Vector3f stylusColour) {
        this.stylusColour = stylusColour;
        renderRequired = 2;
    }

    public float getStylusHeight() {
        return stylusHeight;
    }

    public void setStylusHeight(float stylusHeight) {
        this.stylusHeight = stylusHeight;
        renderRequired = 2;
    }
  
    public List<Vector3f> getToolColours() {
        return toolColours;
    }

    public void setToolColours(List<Vector3f> toolColours) {
        this.toolColours = toolColours;
        renderRequired = 2;
    }
    
    public Vector3f getColourForTool(int toolNumber) {
        if (toolNumber >= 0 && toolNumber < toolColours.size())
            return toolColours.get(toolNumber);
        else
            return defaultColour;
    }

    public void setColourForTool(int toolNumber, Vector3f c) {
        if (toolNumber >= 0 && toolNumber < MAX_NUMBER_OF_TOOLS) {
            if (toolNumber >= toolColours.size()) {
                for (int i = toolColours.size(); i <= toolNumber; ++i)
                    toolColours.add(defaultColour);
            }
            toolColours.set(toolNumber, c);
        }
        renderRequired = 2;
    }

    public Map<String, Vector3f> getTypeColourMap() {
        return typeColourMap;
    }

    public void setTypeColourMap(Map<String, Vector3f> typeColourMap) {
        this.typeColourMap = typeColourMap;
        
        this.types = typeColourMap.keySet()
                             .stream()
                             .collect(Collectors.toList());
        Collections.sort(types);
        this.typeIndexMap = new HashMap<>();
        this.typeColours = new ArrayList<>();
        for (int index = 0; index < types.size(); ++index) {
            String type = types.get(index);
            Vector3f colour = typeColourMap.get(type);
            typeIndexMap.put(type, index);
            typeColours.add(typeColourMap.get(type));
        }
        renderRequired = 2;
        
    }
    
    public int getIndexForType(String type) {
        return typeIndexMap.getOrDefault(type, -1);
    }

    public String getTypeForIndex(int index) {
        if (index >= 0 && index < types.size())
            return types.get(index);
        else
            return "";
    }

    public Vector3f getColourForType(String type) {
        return typeColourMap.getOrDefault(type, defaultColour);
    }

    public void setColourForType(String type, Vector3f c) {
        typeColourMap.put(type, c);
        renderRequired = 2;
    }

    public List<Vector3f> getTypeColours() {
        return typeColours;
    }

    public List<Double> getToolFilamentFactors() {
        return toolFilamentFactors;
    }

    public void setToolFilamentFactors(List<Double> toolFilamentFactors) {
        this.toolFilamentFactors = toolFilamentFactors;
        renderRequired = 2;
    }
    
    public double getFilamentFactorForTool(int toolNumber) {
        if (toolNumber >= 0 && toolNumber < toolFilamentFactors.size())
            return toolFilamentFactors.get(toolNumber);
        else
            return defaultFilamentFactor;
    }

    public void setFilamentFactorForTool(int toolNumber, double filamentFactor) {
        if (toolNumber >= 0 && toolNumber < MAX_NUMBER_OF_TOOLS) {
            if (toolNumber >= toolFilamentFactors.size()) {
                for (int i = toolFilamentFactors.size(); i <= toolNumber; ++i)
                    toolFilamentFactors.add(defaultFilamentFactor);
            }
            toolFilamentFactors.set(toolNumber, filamentFactor);
        }
        renderRequired = 2;
    }

    public List<Double> getToolNozzleEjectVolumes() {
        return toolFilamentFactors;
    }

    public void setToolNozzleEjectVolumes(List<Double> toolFilamentFactors) {
        this.toolFilamentFactors = toolFilamentFactors;
        renderRequired = 2;
    }
    
    public double getNozzleEjectVolumeForTool(int toolNumber) {
        if (toolNumber >= 0 && toolNumber < toolNozzleEjectVolumes.size())
            return toolNozzleEjectVolumes.get(toolNumber);
        else
            return defaultNozzleEjectVolume;
    }

    public void setNozzleEjectVolumeForTool(int toolNumber, double nozzleEjectVolume) {
        if (toolNumber >= 0 && toolNumber < MAX_NUMBER_OF_TOOLS) {
            if (toolNumber >= toolNozzleEjectVolumes.size()) {
                for (int i = toolNozzleEjectVolumes.size(); i <= toolNumber; ++i)
                    toolNozzleEjectVolumes.add(defaultNozzleEjectVolume);
            }
            toolNozzleEjectVolumes.set(toolNumber, nozzleEjectVolume);
        }
        renderRequired = 2;
    }

    public List<Vector3f> getDataColourPalette() {
        return dataColourPalette;
    }

    public void setDataColourPalette(List<Vector3f> dataColourPalette) {
        this.dataColourPalette = dataColourPalette;
        renderRequired = 2;
    }

    public Vector3f getDefaultColour() {
        return defaultColour;
    }

    public void setDefaultColour(Vector3f defaultColour) {
        this.defaultColour = defaultColour;
        renderRequired = 2;
    }
    
    public int getShowFlags() {
        int showFlags = (showMoves ? 1 : 0);
        switch (colourMode) {
            case COLOUR_AS_TYPE:
                showFlags += 2;
                break;
            case COLOUR_AS_DATA:
                showFlags += 4;
                break;
            case COLOUR_AS_TOOL:
            default:
                break;
        }
            
        if (showOnlySelected)
            showFlags += 8;

        if (showStylus)
            showFlags += 16;

        return showFlags;
    }

    public int getShowTools() {
        return showTools;
    }

    public void setShowTools(int showTools) {
        if (this.showTools != showTools) {
            this.showTools = showTools;
            renderRequired = 2;
        }
    }

    private boolean getFlagFromFlags(int flags, int flagIndex) {
        boolean showFlag = false;
        if (flagIndex >= 0 && flagIndex < 16)
        {
            int flag = 1 << flagIndex;
            showFlag = ((flags & flag) == flag);
        }
        return showFlag;
    }

    private int setFlagInFlags(int flags, int flagIndex, boolean showFlag) {
        if (flagIndex >= 0 && flagIndex < 16)
        {
            int flag = 1 << flagIndex;
            if (showFlag != ((flags & flag) == flag)) {
                if (showFlag)
                    flags |= flag;
                else
                    flags &= ~flag;
                renderRequired = 2;
            }
        }
        return flags;
    }

    public boolean getShowFlagForTool(int toolIndex) {
        return getFlagFromFlags(showTools, toolIndex);
    }

    public void setShowFlagForTool(int toolIndex, boolean showFlag) {
        showTools = setFlagInFlags(showTools, toolIndex, showFlag);
    }

    public int getShowTypes() {
        return showTypes;
    }

    public void setShowTypes(int showTypes) {
        if (this.showTypes != showTypes) {
            this.showTypes = showTypes;
            renderRequired = 2;
        }
    }

    public boolean getShowFlagForTypeIndex(int typeIndex) {
        return getFlagFromFlags(showTypes, typeIndex);
    }

    public boolean getShowFlagForType(String type) {
        return getFlagFromFlags(showTypes, getIndexForType(type));
    }

    public void setShowFlagForTypeIndex(int typeIndex, boolean showFlag) {
        showTypes = setFlagInFlags(showTypes, typeIndex, showFlag);
    }

    public void setShowFlagForType(String type, boolean showFlag) {
        showTypes = setFlagInFlags(showTypes, getIndexForType(type), showFlag);
    }

    public void checkLimits() {
        if (bottomLayerToRender < indexOfBottomLayer) {
            bottomLayerToRender = indexOfBottomLayer;
            renderRequired = 2;
        }

        if (topLayerToRender > indexOfTopLayer) {
            topLayerToRender = indexOfTopLayer;
            renderRequired = 2;
        }
        if (topLayerToRender < bottomLayerToRender) {
            topLayerToRender = bottomLayerToRender;
            renderRequired = 2;
        }

        if (firstSelectedLine < 0) {
            firstSelectedLine = 0;
            renderRequired = 2;
        }
        if (lastSelectedLine > numberOfLines) {
            lastSelectedLine = numberOfLines;
            renderRequired = 2;
        }
        if (lastSelectedLine < firstSelectedLine) {
            lastSelectedLine = firstSelectedLine;
            renderRequired = 2;
        }
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayWidth(int displayWidth) {
        if (this.displayWidth != displayWidth) {
            this.displayWidth = displayWidth;
            renderRequired = 2;
        }
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
        if (this.displayHeight != displayHeight) {
            this.displayHeight = displayHeight;
            renderRequired = 2;
        }
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        
        if (this.windowWidth != windowWidth) {
            this.windowWidth = windowWidth;
            renderRequired = 2;
        }
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        if (this.windowHeight != windowHeight) {
            this.windowHeight = windowHeight;
            renderRequired = 2;
        }
    }
    
    public int getWindowXPos() {
        return windowXPos;
    }

    public void setWindowXPos(int windowXPos) {
        
        if (this.windowXPos != windowXPos) {
            this.windowXPos = windowXPos;
        }
    }

    public int setWindowYPos() {
        return windowYPos;
    }

    public void setWindowYPos(int windowYPos) {
        if (this.windowYPos != windowYPos) {
            this.windowYPos = windowYPos;
        }
    }

    public double getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(double frameTime) {
        if (this.frameTime != frameTime) {
            this.frameTime = frameTime;
            renderRequired = 2;
        }
    }

    public void setViewResetRequired() {
        viewResetRequired = true;
        renderRequired = 2;
    }

    public void clearViewResetRequired() {
        viewResetRequired = false;
    }

    public boolean getViewResetRequired() {
        return viewResetRequired;
    }

    public void setLoadGCodeRequested() {
        loadGCodeRequested = true;
    }

    public void clearLoadGCodeRequested() {
        loadGCodeRequested = false;
    }

    public boolean getLoadGCodeRequested() {
        return loadGCodeRequested;
    }

    public void setReloadGCodeRequested() {
        reloadGCodeRequested = true;
    }

    public void clearReloadGCodeRequested() {
        reloadGCodeRequested = false;
    }

    public boolean getReloadGCodeRequested() {
        return reloadGCodeRequested;
    }

    public boolean getUseResizeCursor() {
        return useResizeCursor;
    }

    public void setUseResizeCursor(boolean useResizeCursor) {
        this.useResizeCursor = useResizeCursor;
    }

    public void setWindowAction(WindowAction state) {
        windowAction = state;
        if (windowAction != WindowAction.WINDOW_NO_ACTION)
            renderRequired = 2;
    }

    public WindowAction getWindowAction() {
        return windowAction;
    }

    public void setRenderRequired() {
        renderRequired = 2;
    }

    public void clearRenderRequired() {
        if (renderRequired > 0)
            --renderRequired;
    }

    public boolean getRenderRequired() {
        return renderRequired > 0;
    }
    
    public boolean isLayerMapEmpty() {
        return (layerMap == null || layerMap.isEmpty());
    }

    public int getLayerIndexForLine(int lineIndex) {
        int index = lineIndex;
        if (!isLayerMapEmpty()) {
            for (int layerIndex = getIndexOfBottomLayer(); layerIndex <= this.getIndexOfTopLayer(); ++layerIndex) {
                LayerDetails details = layerMap.get(layerIndex);
                if (details != null && details.getStartLine() <= lineIndex && details.getEndLine() >= lineIndex) {
                    index = layerIndex;
                    break;
                }
            }
        }
        return index;
    }

    public int getLineIndexForLayer(int layerIndex, boolean startLine) {
        int index = layerIndex;
        if (!isLayerMapEmpty()) {
            LayerDetails details = layerMap.get(layerIndex);
            if (details != null)
                index = startLine ? details.getStartLine() : details.getEndLine();
        }
        return index;
    }
}
