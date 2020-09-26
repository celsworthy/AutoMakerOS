package celtech.roboxremote;

import com.codahale.metrics.health.HealthCheck;

/**
 *
 * @author Ian
 */
public class AppSetupHealthCheck extends HealthCheck
{

    private final String applicationPIN;

    public AppSetupHealthCheck(String applicationPIN)
    {
        this.applicationPIN = applicationPIN;
    }

    @Override
    protected Result check() throws Exception
    {
        if (applicationPIN == null)
        {
            return Result.unhealthy("applicationPIN is empty");
        } else
        {
            return Result.healthy();
        }
    }
}
