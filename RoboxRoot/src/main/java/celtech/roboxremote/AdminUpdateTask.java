package celtech.roboxremote;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.servlets.tasks.Task;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author ianhudson
 */
public class AdminUpdateTask extends Task
{

    public AdminUpdateTask()
    {
        super("update");
    }

    @Override
    public void execute(Map<String, List<String>> map, PrintWriter writer) throws Exception {
        System.out.println("Asked to execute update");
        for (Entry<String, List<String>> entry : map.entrySet())
        {
            String valueString = "";
            for (String value : entry.getValue()) {
                if (!valueString.isEmpty())
                    valueString += ", ";
                valueString += value;
            }
            System.out.println(entry.getKey() + ":" + valueString);
        }
    }
}
