package celuk.gcodeviewer.engine;

import celuk.gcodeviewer.entities.Entity;
import celuk.gcodeviewer.utils.VectorUtils;
import celuk.gcodeviewer.gcode.GCodeLine;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.joml.Vector3f;

/**
 *
 * @author Tony Aldhous
 */
public class GCodeLineProcessor implements GCodeConsumer
{
    private final Stenographer steno = StenographerFactory.getStenographer(GCodeLineProcessor.class.getName());
    private final double MINIMUM_STEP = 0.01;
    private final double MINIMUM_EXTRUSION = 0.0001;
    private final double MINIMUM_HEIGHT_DIFFERENCE = 0.0001;
    private final String VALVE_MOVE_TYPE = "VALVE-MOVE";
    
    private final GCodeViewerConfiguration configuration;
    private final RenderParameters renderParameters;
    private final Map<String, Double> settingsMap; // Settings read from comments in the GCode.

    private double previousX = 0.0;
    private double previousY = 0.0;
    private double previousZ = 0.0;
    private double previousA = 0.0;
    private double previousB = 0.0;
    private double previousC = 0.0;
    private double previousD = 0.0;
    private double previousE = 0.0;
    private double previousF = 0.0;

    private double currentX = 0.0;
    private double currentY = 0.0;
    private double currentZ = 0.0;
    private double currentA = 0.0;
    private double currentB = 0.0;
    private double currentC = 0.0;
    private double currentD = 0.0;
    private double currentE = 0.0;
    private double currentF = 0.0;
    private double bScale = 1.0;

    private double minDataValues[] = new double[Entity.N_DATA_VALUES];
    private double maxDataValues[] = new double[Entity.N_DATA_VALUES];

    private int currentTool = 0;
    private Set<Integer> toolSet = new HashSet<>();
 
    private boolean relativeXMoves = false;
    private boolean relativeYMoves = false;
    private boolean relativeZMoves = false;
    private boolean relativeAMoves = false;
    private boolean relativeBMoves = false;
    private boolean relativeCMoves = false;
    private boolean relativeDMoves = false;
    private boolean relativeEMoves = false;
    private boolean hasNozzleValves = false;
    private char extruderLetterD = 'D';
    private char extruderLetterE = 'E';
    private int currentLayer = Entity.NULL_LAYER;
    private int currentLine = 0;
    private double currentLayerHeight = -Double.MAX_VALUE;
    private double currentLayerThickness = -Double.MAX_VALUE;
    private boolean layerHeightUpdateRequired = false;

    private Map<Integer, LayerDetails> layerMap = new HashMap<>();

    private String currentType = "";
    private Set<String> typeSet = new HashSet<>();

    // Moves a rendered separately from lines.
    private List<Entity> segments = new ArrayList<>();
    private List<Entity> moves = new ArrayList<>();
    
    static private final double RADIANS_TO_DEGREES = 57.2957795131;
    
    public GCodeLineProcessor(RenderParameters renderParameters, GCodeViewerConfiguration configuration, Map<String, Double> settingsMap)
    {
        this.configuration = configuration;
        this.renderParameters = renderParameters;
        this.settingsMap = settingsMap;
        this.relativeDMoves = configuration.getRelativeExtrusionAsDefault();
        this.relativeEMoves = configuration.getRelativeExtrusionAsDefault();
        this.hasNozzleValves = configuration.getHasNozzleValves();
        this.extruderLetterD = configuration.getExtruderLetterD();
        this.extruderLetterE = configuration.getExtruderLetterE();
 
        for (int dataIndex = 0; dataIndex < Entity.N_DATA_VALUES; ++dataIndex)
        {
            minDataValues[dataIndex] = 0.0;
            maxDataValues[dataIndex] = 0.0;
        }
    }

    public int getCurrentLayer()
    {
        return currentLayer;
    }
    
    public double getCurrentLayerHeight()
    {
        return currentLayerHeight;
    }
    
    public double getCurrentLayerThickness()
    {
        return currentLayerThickness;
    }

    public Map<Integer, LayerDetails> getLayerMap()
    {
        return layerMap;
    }
    
    public List<Entity> getSegments()
    {
        return segments;
    }
    
    public List<Entity> getMoves()
    {
        return moves;
    }

    public Set<Integer> getToolSet()
    {
        // If the toolSet is empty when
        // retrieved, then no tool was selected.
        // The default tool is tool 0, so add
        // it to the toolSet.
        if (toolSet.size() == 0)
            toolSet.add(currentTool);
        return toolSet;
    }

    public Set<String> getTypeSet()
    {
        return typeSet;
    }
    
    private void updateDataRange(int dataIndex, double value)
    {
        if (dataIndex >= 0 && dataIndex < Entity.N_DATA_VALUES)
        {
            if (minDataValues[dataIndex] > value)
                minDataValues[dataIndex] = value;
            if (maxDataValues[dataIndex] < value)
                maxDataValues[dataIndex] = value;
        }
    }
    
    public double getMinDataValue(int dataIndex)
    {
        double minValue = Double.POSITIVE_INFINITY;
        if (dataIndex >= 0 && dataIndex < Entity.N_DATA_VALUES)
            minValue = minDataValues[dataIndex];
        return minValue;
    }

    public double getMaxDataValue(int dataIndex)
    {
        double maxValue = Double.NEGATIVE_INFINITY;
        if (dataIndex >= 0 && dataIndex < Entity.N_DATA_VALUES)
            maxValue = maxDataValues[dataIndex];
        return maxValue;
    }

    @Override
    public void reset()
    {
        previousX = 0.0;
        previousY = 0.0;
        previousZ = 0.0;
        previousA = 0.0;
        previousB = 0.0;
        previousC = 0.0;
        previousD = 0.0;
        previousE = 0.0;
        previousF = 0.0;

        currentX = 0.0;
        currentY = 0.0;
        currentZ = 0.0;
        currentA = 0.0;
        currentB = 0.0;
        currentC = 0.0;
        currentD = 0.0;
        currentE = 0.0;
        currentF = 0.0;
        
        for (int dataIndex = 0; dataIndex < Entity.N_DATA_VALUES; ++dataIndex)
        {
            minDataValues[dataIndex] = Double.POSITIVE_INFINITY;
            maxDataValues[dataIndex] = Double.NEGATIVE_INFINITY;
        }

        currentTool = 0;
        toolSet.clear();
        
        relativeXMoves = false;
        relativeYMoves = false;
        relativeZMoves = false;
        relativeAMoves = false;
        relativeBMoves = false;
        relativeCMoves = false;
        relativeDMoves = configuration.getRelativeExtrusionAsDefault();
        relativeEMoves = configuration.getRelativeExtrusionAsDefault();

        currentLayer = Entity.NULL_LAYER;
        currentLayerHeight = 0;
        layerMap.clear();
        currentType = "";
        typeSet.clear();
        
        segments.clear();
        moves.clear();
    }

    @Override
    public void processLine(GCodeLine line)
    {
        currentLine = line.lineNumber;
        
        switch (line.commandLetter)
        {
            case 'G':
                processGCode(line);
                break;

            case 'M':
                processMCode(line);
                break;

            case 'T':
                processTCode(line);
                break;
                
            default:
                processCommentLine(line);
        }
    }

    public void processGCode(GCodeLine line)
    {
        switch (line.commandNumber)
        {
            case 0:
            case 1:
                processMove(line);//Coordinated Movement
                break;

            case 28:
                processG28(line); // Home
                break;

            case 90:
                setRelativeMoves(line, false); // Use absolute coordinates on specified axes.
                break;

            case 91:
                setRelativeMoves(line, true); // Use relative coordinates on specified axes.
                break;

            case 92:
                processG92(line); // Set position to coordinates given.
                break;

            default:
                // Ignore code that does not affect visualisation.
                break;
        }
    }

    public void processMCode(GCodeLine line)
    {
        switch (line.commandNumber)
        {
            case 82:
                // Use absolute coordinates on extruder axes.
                relativeDMoves = false;
                relativeEMoves = false;
                break;

            case 83:
                // Use relative coordinates on extruder axes.
                relativeDMoves = true;
                relativeEMoves = true;
                break;

            default:
                // Ignore code that does not affect visualisation.
                break;
        }
    }

    public void processTCode(GCodeLine line)
    {
        currentTool = line.commandNumber;
        toolSet.add(currentTool);
        processMove(line);
    }

    public void processCommentLine(GCodeLine line)
    {
        if (line.layerNumber > Entity.NULL_LAYER &&
            line.layerNumber != currentLayer)
        {
            if (currentLayer == Entity.NULL_LAYER) {
                // Add a "NULL" layer that contains the lines before the first layer comment.
                layerMap.put(Entity.NULL_LAYER, new LayerDetails(Entity.NULL_LAYER, 0, currentLine,
                                                                 currentZ, currentZ));
            }
 
            completeLayerDetails();
            currentLayer = line.layerNumber;
            if (line.layerHeight > -Double.MAX_VALUE)
            {
                currentLayerThickness = line.layerHeight - currentLayerHeight;
                currentLayerHeight = line.layerHeight;
                layerHeightUpdateRequired = false;
            }
            else
            {
                layerHeightUpdateRequired = true;
            }
            layerMap.put(currentLayer, new LayerDetails(currentLayer, currentLine, currentLine + 1,
                                                        currentLayerHeight, currentLayerThickness));
        }
        if (!line.type.isEmpty() &&
            line.type != currentType)
        {
            currentType = line.type;
            typeSet.add(currentType);
        }
    }
    
    public void processMove(GCodeLine line)
    {
        if (relativeAMoves)
            currentA += line.getValue('A', 0.0);
        else
            currentA = line.getValue('A', currentA);
        
        if (relativeCMoves)
            currentC += line.getValue('C', 0.0);
        else
            currentC = line.getValue('C', currentC);
        
        if (relativeXMoves)
            currentX += line.getValue('X', 0.0);
        else
            currentX = line.getValue('X', currentX)
                    ;
        if (relativeYMoves)
            currentY += line.getValue('Y', 0.0);
        else
            currentY = line.getValue('Y', currentY);
        
        if (relativeZMoves)
            currentZ += line.getValue('Z', 0.0);
        else
            currentZ = line.getValue('Z', currentZ);
        
        if (relativeDMoves)
            currentD += line.getValue(extruderLetterD, 0.0);
        else
            currentD = line.getValue(extruderLetterD, currentD);
        
        if (relativeEMoves)
            currentE += line.getValue(extruderLetterE, 0.0);
        else
            currentE = line.getValue(extruderLetterE, currentE);
        
        if (line.isValueSet('B'))
        {
            if (relativeBMoves && !hasNozzleValves)
                currentB += line.getValue('B', 0.0);
            else
                currentB = line.getValue('B', currentB);
            
            if (hasNozzleValves)
            {
                // Partial valve opens have a minimum open value. If the original value
                // was smaller, the viewer will show this as a large blob. The original
                // B value is included in the comment and stored in 'b'. It is used
                // in place of B to suppress the annoying blobs.
                if (line.isValueSet('b')) {
                    double littleB = line.getValue('b', currentB);
                    bScale = littleB / currentB;
                    currentB = littleB;
                }
                else if (bScale < 1.0) {
                    if (currentB <= 0.0) {
                        currentB = 0.0;
                        bScale = 1.0;
                    }
                    else {
                        double scaledB = currentB * bScale;
                        currentB = scaledB;
                    }
                }
            }
        }
        
        // F is always absolute?
        currentF = line.getValue('F', currentF);
        
        if (layerHeightUpdateRequired && currentZ > currentLayerHeight + MINIMUM_HEIGHT_DIFFERENCE)
        {
            layerHeightUpdateRequired = false;
            // First move since layer change without height parameter.
            // Infer layer height from Z value. Wont work if layers are not horizontal!
            if (currentLayerHeight > -Double.MAX_VALUE)
                currentLayerThickness = currentZ - currentLayerHeight;
            else
                currentLayerThickness = currentZ;
            currentLayerHeight = currentZ;
            LayerDetails details = layerMap.get(currentLayer);
            details.setLayerThickness(currentLayerThickness);
            details.setLayerHeight(currentLayerHeight);
        }
        
        generateEntity();
    }

    public void processG28(GCodeLine line)
    {
        // Home axis.
        if (line.isValueSet('X'))
            currentX = 0.0;
        if (line.isValueSet('Y'))
            currentY = 0.0;
        if (line.isValueSet('Z'))
            currentZ = 0.0;
        if (line.isValueSet('A'))
            currentA = 0.0;
        if (line.isValueSet('B'))
            currentB = 0.0;
        if (line.isValueSet('C'))
            currentC = 0.0;
        if (line.isValueSet(extruderLetterD))
            currentD = 0.0;
        if (line.isValueSet(extruderLetterE))
            currentE = 0.0;
        if (line.isValueSet('F'))
            currentF = 0.0;
        generateEntity();
    }

    public void processG92(GCodeLine line)
    {
        // Set Axes to absolute position.
        if (line.isValueSet('X'))
        {
            currentX = line.getValue('X', currentX);
            previousX = currentX;
        }
        if (line.isValueSet('Y'))
        {
            currentY = line.getValue('Y', currentY);
            previousY = currentY;
        }
        if (line.isValueSet('Z'))
        {
            currentZ = line.getValue('Z', currentZ);
            previousZ = currentZ;
        }
        if (line.isValueSet('A'))
        {
            currentA = line.getValue('A', currentA);
            previousA = currentA;
        }
        if (line.isValueSet('B'))
        {
            currentB = line.getValue('B', currentB);
            previousB = currentB;
        }

        if (line.isValueSet('C'))
        {
            currentC = line.getValue('C', currentC);
            previousC = currentC;
        }

        if (line.isValueSet(extruderLetterD))
        {
            currentD = line.getValue(extruderLetterD, currentD);
            previousD = currentD;
        }
        if (line.isValueSet(extruderLetterE))
        {
            currentE = line.getValue(extruderLetterE, currentE);
            previousE = currentE;
        }
        if (line.isValueSet('F'))
        {
            currentF = line.getValue('F', currentF);
            previousF = currentF;
        }
    }
    
    public void setRelativeMoves(GCodeLine line, boolean isRelative)
    {
        if (line.hasNoValues())
        {
            relativeXMoves = isRelative;
            relativeYMoves = isRelative;
            relativeZMoves = isRelative;
        }
        else {
            if (line.isValueSet('X'))
                relativeXMoves = isRelative;
            if (line.isValueSet('Y'))
                relativeYMoves = isRelative;
            if (line.isValueSet('Z'))
                relativeZMoves = isRelative;
            if (line.isValueSet('A'))
                relativeAMoves = isRelative;
            if (line.isValueSet('B'))
                relativeBMoves = isRelative;
            if (line.isValueSet('C'))
                relativeCMoves = isRelative;
            if (line.isValueSet(extruderLetterD))
                relativeDMoves = isRelative;
            if (line.isValueSet(extruderLetterE))
                relativeEMoves = isRelative;
        }
    }

    public void generateEntity()
    {
        double deltaB = currentB - previousB;
        double deltaD = currentD - previousD;
        double deltaE = currentE - previousE;
        boolean isNozzleMove = (hasNozzleValves && Math.abs(deltaB) > 0);
        boolean isFilamentMove = (Math.abs(deltaD) > MINIMUM_EXTRUSION ||
                                  Math.abs(deltaE) > MINIMUM_EXTRUSION);
        boolean isExtrusion = (isNozzleMove || isFilamentMove);
        if (isNozzleMove)
            typeSet.add(VALVE_MOVE_TYPE);

        Vector3f direction = new Vector3f((float)(currentX - previousX), (float)(currentY - previousY), (float)(currentZ - previousZ));
        float length = direction.length();
        if (length > MINIMUM_STEP)
        {
            direction.normalize();
            Vector3f normal = new Vector3f(direction).cross(0.0F, 0.0F, 1.0F);
            if (normal.length() < 0.1) // Close to vertical.
            {
                Vector3f nx = new Vector3f(direction).cross(1.0F, 0.0F, 0.0F);
                Vector3f ny =  new Vector3f(direction).cross(0.0F, 1.0F, 0.0F);
                if (nx.length() >= ny.length())
                    normal = nx;
                else
                    normal = ny;
            }
            normal.normalize();

            float width = 0.1F;
            float thickness = (float)currentLayerThickness;
            if (isExtrusion)
            {
                // Calculate volume of filament to extrude.
                double v = renderParameters.getFilamentFactorForTool(currentTool) * (deltaD >= deltaE ? deltaD : deltaE);
                if (isNozzleMove)
                    v -= renderParameters.getNozzleEjectVolumeForTool(currentTool) * deltaB;

                if (v > 0) {
                    // Calculate cross sectional area of segment from volume.
                    double a = v / length;
                    
                    // The segment extruded is drawn as a diamond <>. The width of the diamond is the distance between
                    // the horizontal points. The thickness is the distance between the vertical points. The cross-sectional
                    // area of a diamond is given by 0.5 * width * thickness.
                    if (thickness > 0.0) {
                        // Calculate width of diamond with given thickness and cross sectional area.
                        width = (float)(2.0 * a / thickness);
                    }
                    else {
                        // Calculate width of a square (i.e. diamond with width = thickness) with the given cross sectional area.
                        width = (float)sqrt(2.0 * a);
                        thickness = width;
                    }
                    
                    if (currentType.equalsIgnoreCase("FILL"))
                    {
                        // Infill can be a multiple of the layer thickness.
                        double infillLayerThickness = settingsMap.getOrDefault("infillLayerThickness", 0.0);
                        double fillExtrusionWidth = settingsMap.getOrDefault("fillExtrusionWidth_mm", 0.0);
                        if (infillLayerThickness > 0.0 && fillExtrusionWidth > 0.0 && width > fillExtrusionWidth)
                        {
                            // Calculated width is wider than the specified infill width.
                            // Recalculate the thickness based on the width.
                            width = (float)fillExtrusionWidth;
                            thickness = (float)(2.0 * a / fillExtrusionWidth);
                        }
                    }
                }
            }

            Vector3f toPosition = new Vector3f((float)(currentX), (float)(currentY), (float)(currentZ));
            Vector3f fromPosition = new Vector3f((float)(previousX), (float)(previousY), (float)(previousZ));
            Vector3f entityPosition = VectorUtils.calculateCenterBetweenVectors(fromPosition, toPosition);
            Entity entity = new Entity(null, entityPosition, direction, normal,
                                       length, width, thickness,
                                       currentLayer, currentLine, currentTool, !isExtrusion, null);
            if (isExtrusion)
            {
                String t = (isNozzleMove ? VALVE_MOVE_TYPE : currentType);
                entity.setType(t);
                entity.setTypeIndex(renderParameters.getIndexForType(t));
                Vector3f typeColour = configuration.getColourForType(t);
                entity.setTypeColour(typeColour);
                entity.setColour(typeColour);
                entity.setDataValue(Entity.DATA_A, (float)currentA);
                entity.setDataValue(Entity.DATA_B, (float)currentB);
                entity.setDataValue(Entity.DATA_C, (float)currentC);
                entity.setDataValue(Entity.DATA_D, (float)currentD);
                entity.setDataValue(Entity.DATA_E, (float)currentE);
                entity.setDataValue(Entity.DATA_F, (float)currentF);
                segments.add(entity);
            }
            else
            {
                entity.setColour(null);
                entity.setTypeColour(null);
                entity.setDataValues(null);
                moves.add(entity);
            }

            previousX = currentX;
            previousY = currentY;
            previousZ = currentZ;
            previousA = currentA;
            previousB = currentB;
            previousC = currentC;
            previousD = currentD;
            previousE = currentE;
            previousF = currentF;
        }
        else if (isExtrusion)
        {
            // Probably a retraction. Update position but do not create an entity.
            previousX = currentX;
            previousY = currentY;
            previousZ = currentZ;
            previousA = currentA;
            previousB = currentB;
            previousC = currentC;
            previousD = currentD;
            previousE = currentE;
            previousF = currentF;
        }
        
        updateDataRange(Entity.DATA_A, currentA);
        updateDataRange(Entity.DATA_B, currentB);
        updateDataRange(Entity.DATA_C, currentC);
        updateDataRange(Entity.DATA_D, currentD);
        updateDataRange(Entity.DATA_E, currentE);
        updateDataRange(Entity.DATA_F, currentF);
        // updateDataRange(Entity.DATA_X, currentX);
        // updateDataRange(Entity.DATA_Y, currentY);
        // updateDataRange(Entity.DATA_Z, currentZ);
    }
    
    private void completeLayerDetails() {
            if (currentLayer != Entity.NULL_LAYER) {
                LayerDetails details = layerMap.get(currentLayer);
                details.setEndLine(currentLine);
                details.calcNumberOfLines();
            }        
    }

    @Override
    public void complete() {
        ++currentLine; // Step to one beyond the last line.
        completeLayerDetails();
    }
}
