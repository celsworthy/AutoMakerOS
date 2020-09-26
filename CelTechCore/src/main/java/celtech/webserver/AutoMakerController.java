package celtech.webserver;

import celtech.Lookup;
import celtech.roboxbase.BaseLookup;
import celtech.roboxbase.printerControl.model.Printer;
import celtech.roboxbase.printerControl.model.PrinterException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;

/**
 *
 * @author Ian
 */
public class AutoMakerController implements HttpHandler
{

    private static String abortPrintPage = "/abortPrint";
    private static String printSample1Page = "/printSample1";

    private final Stenographer steno = StenographerFactory.getStenographer(
        AutoMakerController.class.
        getName());

    @Override
    public void handle(HttpExchange t) throws IOException
    {
        // Object exists and is a file: accept with response code 200.
        String mime = "text/html";

        Headers h = t.getResponseHeaders();
        h.set("Content-Type", mime);

        String statusResponse;

        List<Printer> connectedPrinters = BaseLookup.getConnectedPrinters();

        steno.info(t.getRequestURI().getPath());
        if (t.getRequestURI().getPath().matches(abortPrintPage))
        {
            statusResponse = "<h3>OK</h3>";
            if (connectedPrinters.size() > 0)
            {
                try
                {
                    connectedPrinters.get(0).cancel(null, Lookup.getUserPreferences().isSafetyFeaturesOn());
                } catch (PrinterException ex)
                {
                    steno.error("Error attempting to abort");
                }
            }
            t.getResponseHeaders().add("Location", "/");
            t.sendResponseHeaders(302, statusResponse.length());
        } else if (t.getRequestURI().getPath().matches(printSample1Page))
        {
            statusResponse = "<h3>OK</h3>";
            if (connectedPrinters.size() > 0)
            {
//                try
//                {
////                    connectedPrinters.get(0).executeGCodeFileWithoutPurgeCheck(ApplicationConfiguration.getApplicationModelDirectory() + "commissioningTestPrint.gcode", true);
//                } catch (PrinterException ex)
//                {
//                    steno.error("Error attempting to print sample 1");
//                }
            }
            t.getResponseHeaders().add("Location", "/");
            t.sendResponseHeaders(302, statusResponse.length());
        } else
        {
            statusResponse = "<h3>AutoMaker Remote</h3>"
                + "<p>Attached Printers:";

            for (Printer printer : BaseLookup.getConnectedPrinters())
            {
                statusResponse += printer.getPrinterIdentity().printerUniqueIDProperty().get()
                    + "\r";
            }

            statusResponse += "</p>";
            statusResponse
                += "<form method=\"get\" action=\"" + abortPrintPage + "\">"
                + "<button type=\"submit\">Abort Print</button></form>";
            statusResponse
                += "<form method=\"get\" action=\"" + printSample1Page + "\">"
                + "<button type=\"submit\">Print Sample 1</button></form>";

            t.sendResponseHeaders(200, statusResponse.length());
        }

        OutputStream os = t.getResponseBody();
        os.write(statusResponse.getBytes());
//        InputStream is = ApplicationConfiguration.class.getResourceAsStream(
//            "/celtech/resources/webpages/status.html");
//        final byte[] buffer = new byte[0x10000];
//        int count = 0;
//        while ((count = is.read(buffer)) >= 0)
//        {
//            os.write(buffer, 0, count);
//        }
//        is.close();
        os.close();

//        
//        String root = "./wwwroot";
//        URI uri = t.getRequestURI();
//        System.out.println("looking for: " + root + uri.getPath());
//        String path = uri.getPath();
//        File file = new File(root + path).getCanonicalFile();
//
//        if (!file.getPath().startsWith(root))
//        {
//            // Suspected path traversal attack: reject with 403 error.
//            String response = "403 (Forbidden)\n";
//            t.sendResponseHeaders(403, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        } else if (!file.isFile())
//        {
//            // Object does not exist or is not a file: reject with 404 error.
//            String response = "404 (Not Found)\n";
//            t.sendResponseHeaders(404, response.length());
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        } else
//        {
//            // Object exists and is a file: accept with response code 200.
//            String mime = "text/html";
//            if (path.substring(path.length() - 3).equals(".js"))
//            {
//                mime = "application/javascript";
//            }
//            if (path.substring(path.length() - 3).equals("css"))
//            {
//                mime = "text/css";
//            }
//
//            Headers h = t.getResponseHeaders();
//            h.set("Content-Type", mime);
//            t.sendResponseHeaders(200, 0);
//
//            OutputStream os = t.getResponseBody();
//            FileInputStream fs = new FileInputStream(file);
//            final byte[] buffer = new byte[0x10000];
//            int count = 0;
//            while ((count = fs.read(buffer)) >= 0)
//            {
//                os.write(buffer, 0, count);
//            }
//            fs.close();
//            os.close();
////        
////        String root = "/var/www/";
////        URI uri = t.getRequestURI();
////        File file = new File(root + uri.getPath()).getCanonicalFile();
////        if (!file.getPath().startsWith(root))
////        {
////            // Suspected path traversal attack: reject with 403 error.
////            String response = "403 (Forbidden)\n";
////            t.sendResponseHeaders(403, response.length());
////            OutputStream os = t.getResponseBody();
////            os.write(response.getBytes());
////            os.close();
////        } else if (!file.isFile())
////        {
////            // Object does not exist or is not a file: reject with 404 error.
////            String response = "404 (Not Found)\n";
////            t.sendResponseHeaders(404, response.length());
////            OutputStream os = t.getResponseBody();
////            os.write(response.getBytes());
////            os.close();
////        } else
////        {
////            // Object exists and is a file: accept with response code 200.
////            t.sendResponseHeaders(200, 0);
////            OutputStream os = t.getResponseBody();
////            FileInputStream fs = new FileInputStream(file);
////            final byte[] buffer = new byte[0x10000];
////            int count = 0;
////            while ((count = fs.read(buffer)) >= 0)
////            {
////                os.write(buffer, 0, count);
////            }
////            fs.close();
////            os.close();
////        }
    }
}
