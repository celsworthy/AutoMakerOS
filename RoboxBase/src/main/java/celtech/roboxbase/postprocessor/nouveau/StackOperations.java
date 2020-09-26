package celtech.roboxbase.postprocessor.nouveau;

import org.parboiled.Context;
import org.parboiled.ContextAware;

/**
 *
 * @author Ian
 */
public class StackOperations implements ContextAware<Object>
{

    private Context<Object> context;

    @Override
    public void setContext(Context<Object> context)
    {
        this.context = context;
    }

    boolean push(Object node)
    {
        context.getValueStack().push(node);
        return true;
    }
}
