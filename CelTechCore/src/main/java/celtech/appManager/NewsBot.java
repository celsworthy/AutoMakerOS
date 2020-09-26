package celtech.appManager;

import celtech.configuration.ApplicationConfiguration;
import celtech.roboxbase.configuration.BaseConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import libertysystems.stenographer.Stenographer;
import libertysystems.stenographer.StenographerFactory;
import org.apache.commons.lang.StringEscapeUtils;

/**
 *
 * @author Ian
 */
public class NewsBot
{

    private static Stenographer steno = StenographerFactory.getStenographer(NewsBot.class.getName());
    private static NewsBot instance = null;
    private static final String baseURL = "https://www.cel-uk.com/wp-json/wp/v2/posts/?categories=871";
    private Timer newsCheckTimer = new Timer(true);
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final List<NewsListener> newsListeners = new ArrayList<>();
    private final List<NewsArticle> unreadNewsArticles = new ArrayList<>();
    private String lastTimeThereWasNewNewsDateString = null;
    private int strikes = 0;
    private final int NORMAL_FREQUENCY_POLLING_PERIOD_MS = 60000;
    private final int LOWER_FREQUENCY_POLLING_THRESHOLD = 3;
    private final int LOWER_FREQUENCY_POLLING_PERIOD_MS = 120000;
    private final int STRIKES_AND_YOURE_OUT = 6;

    public class NewsArticle
    {

        private int id;
        private String title;
        private String content;
        private String link;
        private String dateString;

        private NewsArticle(int id, String title, String content, String link, String dateString)
        {
            this.id = id;
            this.title = title;
            this.content = content;
            this.link = link;
            this.dateString = dateString;
        }

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public String getLink()
        {
            return link;
        }

        public void setLink(String link)
        {
            this.link = link;
        }

        public String getDateString()
        {
            return dateString;
        }

        public void setDateString(String dateString)
        {
            this.dateString = dateString;
        }
    }

    private NewsBot()
    {
        lastTimeThereWasNewNewsDateString = ApplicationConfiguration.getLastNewsRetrievalTimeAsString();
        newsCheckTimer.schedule(new PeriodicNewsCheckTask(), 1000, NORMAL_FREQUENCY_POLLING_PERIOD_MS);
    }

    public static NewsBot getInstance()
    {
        if (instance == null)
        {
            instance = new NewsBot();
        }

        return instance;
    }

    public void registerListener(NewsListener newsListener)
    {
        newsListeners.add(newsListener);
        if (!unreadNewsArticles.isEmpty())
        {
            newsListener.hereIsTheNews(unreadNewsArticles);
        }
    }

    private class PeriodicNewsCheckTask extends TimerTask
    {

        @Override
        public void run()
        {
            try
            {
                String newsCheckURLToUse = baseURL;
                if (lastTimeThereWasNewNewsDateString != null)
                {
                    newsCheckURLToUse += "&after=" + lastTimeThereWasNewNewsDateString;
                }

                URL obj = new URL(newsCheckURLToUse);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                // optional default is GET
                con.setRequestMethod("GET");

                //add request header
                con.setRequestProperty("User-Agent", BaseConfiguration.getApplicationName());

                con.setConnectTimeout(5000);
                int responseCode = con.getResponseCode();

                if (responseCode == 200)
                {
                    JsonNode jsonNode = jsonMapper.readTree(con.getInputStream());
                    if (jsonNode.isArray())
                    {
                        List<NewsArticle> articlesToAdd = new ArrayList<>();

                        Iterator<JsonNode> elementIterator = jsonNode.elements();
                        while (elementIterator.hasNext())
                        {
                            JsonNode childNode = elementIterator.next();
                            int id = childNode.get("id").asInt();

                            String title = StringEscapeUtils.unescapeHtml(childNode.get("title").get("rendered").asText());
                            String content = childNode.get("content").get("rendered").asText();
                            String link = childNode.get("link").asText();
                            String postDate = childNode.get("date").asText();
                            NewsArticle newsArticle = new NewsArticle(id, title, content, link, postDate);
                            if (articlesToAdd.isEmpty())
                            {
                                //The first article is the newest, so grab the date string
                                lastTimeThereWasNewNewsDateString = postDate;
                            }
                            articlesToAdd.add(newsArticle);
                        }

                        if (articlesToAdd.size() > 0)
                        {
                            addNewNewsArticles(articlesToAdd);
                        }
                    }

                    strikes = 0;
                } else
                {
                    strikes++;
                }
            } catch (IOException ex)
            {
                steno.warning("Error whilst polling for news " + ex + " is the internet connection down?");
                strikes++;
            }

            if (strikes >= STRIKES_AND_YOURE_OUT)
            {
                steno.info("Too many failed attempts to look for news - stopping news service...");
                newsCheckTimer.cancel();
            } else if (strikes == LOWER_FREQUENCY_POLLING_THRESHOLD)
            {
                steno.info("Repeated failure to contact news service - switching to lower poll rate.");
                newsCheckTimer.cancel();
                newsCheckTimer = new Timer(true);
                newsCheckTimer.schedule(new PeriodicNewsCheckTask(), LOWER_FREQUENCY_POLLING_PERIOD_MS, LOWER_FREQUENCY_POLLING_PERIOD_MS);
            }
        }
    }

    private void addNewNewsArticles(List<NewsArticle> newsArticles)
    {
        unreadNewsArticles.addAll(newsArticles);
        updateListeners();
    }

    private void addNewNewsArticle(NewsArticle newsArticle)
    {
        unreadNewsArticles.add(newsArticle);
        updateListeners();
    }

    private void updateListeners()
    {
        newsListeners.forEach((listener) ->
        {
            listener.hereIsTheNews(unreadNewsArticles);
        });
    }

    public void allNewsHasBeenRead()
    {
        ApplicationConfiguration.setLastNewsRetrievalTime(lastTimeThereWasNewNewsDateString);
        unreadNewsArticles.clear();
        updateListeners();
    }

    public void articleRead(NewsArticle newsArticle)
    {
        unreadNewsArticles.remove(newsArticle);
        updateListeners();
    }
}
