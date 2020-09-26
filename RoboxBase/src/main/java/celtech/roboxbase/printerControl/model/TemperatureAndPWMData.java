package celtech.roboxbase.printerControl.model;

import celtech.roboxbase.configuration.OperatingVoltage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Ian
 */
public class TemperatureAndPWMData
{

    private int nozzle1Temperature;
    private int nozzleHeater1PWMDutyCycle;
    private int nozzle2Temperature;
    private int nozzleHeater2PWMDutyCycle;
    private int bedTemperature;
    private OperatingVoltage operatingVoltage;
    private int bedHeaterPWMDutyCycle;
    private int ambientTemperature;
    private int ambientFanPWMDutyCycle;

    public int getNozzle1Temperature()
    {
        return nozzle1Temperature;
    }

    public void setNozzle1Temperature(int nozzle1Temperature)
    {
        this.nozzle1Temperature = nozzle1Temperature;
    }

    public int getNozzleHeater1PWMDutyCycle()
    {
        return nozzleHeater1PWMDutyCycle;
    }

    public void setNozzleHeater1PWMDutyCycle(int nozzleHeater1PWMDutyCycle)
    {
        this.nozzleHeater1PWMDutyCycle = nozzleHeater1PWMDutyCycle;
    }

    public int getNozzle2Temperature()
    {
        return nozzle2Temperature;
    }

    public void setNozzle2Temperature(int nozzle2Temperature)
    {
        this.nozzle2Temperature = nozzle2Temperature;
    }

    public int getNozzleHeater2PWMDutyCycle()
    {
        return nozzleHeater2PWMDutyCycle;
    }

    public void setNozzleHeater2PWMDutyCycle(int nozzleHeater2PWMDutyCycle)
    {
        this.nozzleHeater2PWMDutyCycle = nozzleHeater2PWMDutyCycle;
    }

    public int getBedTemperature()
    {
        return bedTemperature;
    }

    public void setBedTemperature(int bedTemperature)
    {
        this.bedTemperature = bedTemperature;
    }

    public OperatingVoltage getOperatingVoltage()
    {
        return operatingVoltage;
    }

    public void setOperatingVoltage(OperatingVoltage operatingVoltage)
    {
        this.operatingVoltage = operatingVoltage;
    }

    public int getBedHeaterPWMDutyCycle()
    {
        return bedHeaterPWMDutyCycle;
    }

    public void setBedHeaterPWMDutyCycle(int bedHeaterPWMDutyCycle)
    {
        this.bedHeaterPWMDutyCycle = bedHeaterPWMDutyCycle;
    }

    public int getAmbientTemperature()
    {
        return ambientTemperature;
    }

    public void setAmbientTemperature(int ambientTemperature)
    {
        this.ambientTemperature = ambientTemperature;
    }

    public int getAmbientFanPWMDutyCycle()
    {
        return ambientFanPWMDutyCycle;
    }

    public void setAmbientFanPWMDutyCycle(int ambientFanPWMDutyCycle)
    {
        this.ambientFanPWMDutyCycle = ambientFanPWMDutyCycle;
    }

    void populateFromPrinterData(String response) throws PrinterException
    {
        // String coming back from the printer is
        // S:nn @nn T:nn @nn B:nn (^/$)nn A:nn *nn\r\nok\r\n
        // Noz1 Temp, Noz1 PWM, Noz2 Temp, Noz2 PWM, Bed Temp, Bed voltage det, Bed PWM, Ambient Temp, Ambient fan PWM
        Pattern compile = Pattern.compile(
            "S:(?<noz1Tmp>[-0-9]+)"
            + " @(?<noz1PWM>[-0-9]+)"
            + " T:(?<noz2Tmp>[-0-9]+)"
            + " @(?<noz2PWM>[-0-9]+)"
            + " B:(?<bedTmp>[-0-9]+)"
            + " (?<voltageDet>[\\^\\$]+)"
            + "(?<bedPWM>[-0-9]+)"
            + " A:(?<ambTmp>[-0-9]+)"
            + " \\*(?<ambPWM>[-0-9]+)"
            + "[.\\s]+");

        Matcher matcher = compile.matcher(response);
        matcher.find();

        try
        {
            String noz1TmpString = matcher.group("noz1Tmp");
            int noz1Tmp = Integer.valueOf(noz1TmpString);
            setNozzle1Temperature(noz1Tmp);

            String noz1PWMString = matcher.group("noz1PWM");
            int noz1PWM = Integer.valueOf(noz1PWMString);
            setNozzleHeater1PWMDutyCycle(noz1PWM);

            String noz2TmpString = matcher.group("noz2Tmp");
            int noz2Tmp = Integer.valueOf(noz2TmpString);
            setNozzle2Temperature(noz2Tmp);

            String noz2PWMString = matcher.group("noz2PWM");
            int noz2PWM = Integer.valueOf(noz2PWMString);
            setNozzleHeater2PWMDutyCycle(noz2PWM);

            String bedTmpString = matcher.group("bedTmp");
            int bedTmp = Integer.valueOf(bedTmpString);
            setBedTemperature(bedTmp);

            String voltageDetString = matcher.group("voltageDet");
            if (voltageDetString.equals("^"))
            {
                operatingVoltage = OperatingVoltage._240V;
            } else if (voltageDetString.equals("$"))
            {
                operatingVoltage = OperatingVoltage._110V;
            } else
            {
                operatingVoltage = OperatingVoltage.UNKNOWN;
            }

            String bedPWMString = matcher.group("bedPWM");
            int bedPWM = Integer.valueOf(bedPWMString);
            setBedHeaterPWMDutyCycle(bedPWM);

            String ambientTmpString = matcher.group("ambTmp");
            int ambientTmp = Integer.valueOf(ambientTmpString);
            setAmbientTemperature(ambientTmp);

            String ambientPWMString = matcher.group("ambPWM");
            int ambientFanPWM = Integer.valueOf(ambientPWMString);
            setAmbientFanPWMDutyCycle(ambientFanPWM);
        } catch (NumberFormatException | IllegalStateException ex)
        {
            throw new PrinterException("Failed to convert printer temperature and PWM data");
        }
    }
}
