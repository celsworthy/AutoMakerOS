package celuk.groot.remote;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ListPrintersResponse
{
    private List<String> printerIDs;

    public ListPrintersResponse()
    {
        // Jackson deserialization
    }

    public ListPrintersResponse(List<String> printerIDs)
    {
        this.printerIDs = printerIDs;
    }

    @JsonProperty
    public List<String> getPrinterIDs()
    {
        return printerIDs;
    }

    @JsonProperty
    public void setPrinterIDs(List<String> printerIDs)
    {
        this.printerIDs = printerIDs;
    }

    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append("ListPrintersResponse");
        output.append('\n');
        output.append('\n');
        printerIDs.forEach(id ->
        {
            output.append(id);
            output.append('\n');
        });
        output.append("================");

        return output.toString();
    }
}
