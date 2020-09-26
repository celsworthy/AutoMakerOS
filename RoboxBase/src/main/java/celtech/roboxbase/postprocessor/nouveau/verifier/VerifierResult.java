package celtech.roboxbase.postprocessor.nouveau.verifier;

import celtech.roboxbase.postprocessor.nouveau.nodes.GCodeEventNode;

/**
 *
 * @author Ian
 */
public class VerifierResult
{
    public enum ResultType
    {

        EXTRUDE_NOT_FULLY_OPEN("Extrude without nozzle being fully open"),
        EXTRUDE_NO_HEAT("Extrude without heater");

        private final String description;

        private ResultType(String description)
        {
            this.description = description;
        }

        public String getDescription()
        {
            return description;
        }
    }
    
    private final ResultType resultType;
    private final GCodeEventNode nodeInError;
    private final int layerNumber;
    private final int toolnumber;

    public VerifierResult(ResultType resultType,
            GCodeEventNode nodeInError,
            int layerNumber,
            int toolnumber)
    {
        this.resultType = resultType;
        this.nodeInError = nodeInError;
        this.layerNumber = layerNumber;
        this.toolnumber = toolnumber;
    }

    public ResultType getResultType()
    {
        return resultType;
    }

    public GCodeEventNode getNodeInError()
    {
        return nodeInError;
    }

    public int getLayerNumber()
    {
        return layerNumber;
    }

    public int getToolnumber()
    {
        return toolnumber;
    }
}
