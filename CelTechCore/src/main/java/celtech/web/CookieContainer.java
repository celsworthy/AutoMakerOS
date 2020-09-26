package celtech.web;

import java.net.HttpCookie;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Ian
 */
public class CookieContainer
{

    private String uri;
    private ArrayList<String> headers = new ArrayList<>();

    public CookieContainer()
    {
    }

    public CookieContainer(String uri, List<HttpCookie> cookies)
    {
        this.uri = uri;

        cookies.stream().forEach(cookie ->
        {
            headers.add(cookieToHeader(cookie));
        });
    }

    public static String cookieToHeader(HttpCookie cookie)
    {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("EEE, dd-MMM-yyyy H:m:s");
        Calendar calendar = Calendar.getInstance(Locale.UK);
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, (int)cookie.getMaxAge());

        StringBuilder header = new StringBuilder();

        header.append(cookie.getName() + "=");
        header.append(cookie.getValue());
        header.append("; ");

//        if (cookie.getComment() != null)
//        {
//            header.append("comment=");
//            header.append(cookie.getComment());
//            header.append("; ");
//        }
//
        header.append("domain=");
        header.append(cookie.getDomain());
        header.append("; ");

        header.append("max-age=");
        header.append(cookie.getMaxAge());
        header.append("; ");

        header.append("path=");
        header.append(cookie.getPath());
        header.append("; ");

        header.append("expires=");
        header.append(dateTimeFormatter.format(calendar.getTime()));
        header.append("; ");

        return header.toString();
    }

//    public static Map<String, List<String>> cookieToHeaderMap(HttpCookie cookie)
//    {
//        Map<String, List<String>> mapToReturn = new HashMap<>();
//
//        ArrayList<String> tempArrayList = new ArrayList<>();
//        tempArrayList.add(cookie.getValue());
//        mapToReturn.put(cookie.getName(), tempArrayList);
//
//        if (cookie.getComment() != null)
//        {
//            tempArrayList = new ArrayList<>();
//            tempArrayList.add(cookie.getComment());
//            mapToReturn.put("comment", tempArrayList);
//        }
//
//        tempArrayList = new ArrayList<>();
//        tempArrayList.add(cookie.getDomain());
//        mapToReturn.put("domain", tempArrayList);
//
//        tempArrayList = new ArrayList<>();
//        tempArrayList.add(String.valueOf(cookie.getMaxAge()));
//        mapToReturn.put("max-age", tempArrayList);
//
//        tempArrayList = new ArrayList<>();
//        tempArrayList.add(cookie.getPath());
//        mapToReturn.put("path", tempArrayList);
//
//        tempArrayList = new ArrayList<>();
//        tempArrayList.add(String.valueOf(cookie.getVersion()));
//        mapToReturn.put("version", tempArrayList);
//
//        return mapToReturn;
//    }
    public String getUri()
    {
        return uri;
    }

    public void setUri(String uri)
    {
        this.uri = uri;
    }

    public ArrayList<String> getHeaders()
    {
        return headers;
    }

    public void setHeaders(ArrayList<String> headers)
    {
        this.headers = headers;
    }

    public List<HttpCookie> revealTheCookies()
    {
        ArrayList<HttpCookie> returnedCookies = new ArrayList<>();

        headers.stream().forEach(header ->
        {
            List<HttpCookie> cookies = HttpCookie.parse(header);
            cookies.stream().forEach(cookie ->
            {
                if (!cookie.hasExpired())
                {
                    returnedCookies.add(cookie);
                }
            });
        }
        );

        return returnedCookies;
    }
}
