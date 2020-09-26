package celtech.roboxbase.comms.tx;

import celtech.roboxbase.comms.remote.FixedDecimalFloatFormat;

/**
 *
 * @author ianhudson
 */
public class SetTemperatures extends RoboxTxPacket
{

    /**
     *
     */
    public SetTemperatures()
    {
        super(TxPacketTypeEnum.SET_TEMPERATURES, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData)
    {
        setMessagePayloadBytes(byteData);
        return false;
    }

    /**
     *
     * @param nozzle0FirstLayerTarget
     * @param nozzle0Target
     * @param nozzle1FirstLayerTarget
     * @param nozzle1Target
     * @param bedFirstLayerTarget
     * @param bedTarget
     * @param ambientTarget
     */
    public void setTemperatures(double nozzle0FirstLayerTarget, double nozzle0Target,
            double nozzle1FirstLayerTarget, double nozzle1Target,
            double bedFirstLayerTarget, double bedTarget, double ambientTarget)
    {
        StringBuilder payload = new StringBuilder();

        FixedDecimalFloatFormat decimalFloatFormatter = new FixedDecimalFloatFormat();

        payload.append(decimalFloatFormatter.format(nozzle0Target));
        payload.append(decimalFloatFormatter.format(nozzle0FirstLayerTarget));
        payload.append(decimalFloatFormatter.format(nozzle1Target));
        payload.append(decimalFloatFormatter.format(nozzle1FirstLayerTarget));
        payload.append(decimalFloatFormatter.format(bedTarget));
        payload.append(decimalFloatFormatter.format(bedFirstLayerTarget));
        payload.append(decimalFloatFormatter.format(ambientTarget));

        this.setMessagePayload(payload.toString());
    }
}
